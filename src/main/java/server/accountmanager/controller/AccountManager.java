package server.accountmanager.controller;

import server.accountmanager.model.User;
import java.sql.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.DatabaseConnector;
import server.accountmanager.model.Account;
import server.accountmanager.model.AccountStatus;

/**
 * This class manage all queries necessary handle the user´s account
 *
 * @author David Ochoa Uribe
 */
public class AccountManager {

    /**
     * This method allows to create a new user's account based on a JSON as
     * input.
     *
     * @param json This parameter represents a JSON that contains the username,
     * the password, the password confirmation and the email of the user that
     * wants to create a new account.
     * @return The method returns a JSON that contains two fields: the first
     * field is a boolean value that contains the status of the transaction, it
     * takes the value of "true" if the transaction is successful, otherwise it
     * takes the value of false; the second attribute is the message according
     * to the state of the transaction.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public static String createAccount(String json) throws ParseException {

        try {
            // Parse JSON to Object
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            String password = (String) parsedObject.get("password");
            String email = (String) parsedObject.get("e-mail");
            
            Account newAccount = Account.create(AccountStatus.OFFLINE, username, password, email);
            User user = new User(newAccount);

            // Insert into account table the new user
            String query = "INSERT INTO user VALUES (NULL, \"" + user.account.username + "\");";

            DatabaseConnector.getInstance().getStatement().executeUpdate(query);

            query = "INSERT INTO account VALUES ( \"" + user.account.username
                    + "\" , \"" + user.account.password + "\", \"" + user.account.email + "\", 0, 0, 0,'" + AccountStatus.OFFLINE + "');";

            DatabaseConnector.getInstance().getStatement().executeUpdate(query);

        } catch (ClassNotFoundException | SQLException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }

        // Build the response JSON
        JSONObject returnJson = new JSONObject();
        returnJson.put("status", true);
        returnJson.put("message", "Account created");
        return returnJson.toJSONString();
    }

    /**
     * This method update the user's status in the database after logout
     *
     * @param json This attribute represents a JSON that contains the username
     * of the user that wants to logout.
     * @return The method returns a JSON that contains two fields: the first
     * field is a boolean value that contains the status of the transaction, it
     * takes the value of "true" if the logout action was successful, otherwise
     * it takes the value of false; the second attribute is the message
     * according to the state of the transaction.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public static String logOut(String json) throws ParseException {
        try {
            // Parse JSON to Object
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");

            User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));

            // set user's status to offline
            String query = "UPDATE account SET status = '" + AccountStatus.OFFLINE + "' WHERE username = \"" + user.account.username + "\";";

            DatabaseConnector.getInstance().getStatement().executeUpdate(query);

            JSONObject returnJson = new JSONObject();
            returnJson.put("status", true);
            returnJson.put("message", "Account logged out successfully");
            return returnJson.toJSONString();

        } catch (ClassNotFoundException | SQLException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }

    /**
     * This method compare the user's login information with the database, if
     * it's correct , the method updates the status of the user in the database.
     *
     * @param json This parameter represents a JSON that contains the username
     * and the password of the user that wants to login in the system.
     * @return The method returns a JSON that contains two fields: the first
     * field is a status of the login, it takes a boolean value of true if the
     * login was successful, otherwise it takes a false value; the second
     * attribute is a message that contains information about the transaction.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public static String logIn(String json) throws ParseException {

        try {
            // Parse JSON to object
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            String password = (String) parsedObject.get("password");

            User user = new User(Account.create(AccountStatus.OFFLINE, username, password, null));

            // Declare queries
            String querySelect = "SELECT username, password FROM account WHERE username = \"" + user.account.username + "\";";
            String queryUpdate = "UPDATE account SET status = '" + AccountStatus.ONLINE + "' WHERE username = \"" + user.account.username + "\";";
            //search the username and password

            ResultSet resultset = DatabaseConnector.getInstance().getStatement().executeQuery(querySelect);
            while (resultset.next()) {
                String auxUsername = resultset.getString("username");
                String auxPassword = resultset.getString("password");
                //compare ussername and password in database
                if (auxUsername.equals(user.account.username) && auxPassword.equals(user.account.password)) {

                    DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdate);
                    JSONObject returnJson = new JSONObject();
                    returnJson.put("status", true);
                    returnJson.put("message", "Login successfull");
                    return returnJson.toJSONString();

                } else {
                    JSONObject returnJson = new JSONObject();
                    returnJson.put("status", false);
                    returnJson.put("message", "Username or password incorrect");
                    return returnJson.toJSONString();
                }
            }

            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", "Username or password incorrect");
            return returnJson.toJSONString();

        } catch (ClassNotFoundException | SQLException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }

    /**
     * Method used in order to change the password of the account
     *
     * @param json This attribute represents a JSON that contains three
     * attributes: the username of the user that wants to change the password,
     * the actual password of the account and the new password.
     * @return The method returns a JSON that contains two fields: the first
     * field is a status of the password changing, it takes a boolean value of true if the
     * change was successful, otherwise it takes a false value; the second
     * attribute is a message that contains information about the transaction.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public static String changePassword(String json) throws ParseException {

        try {
            // Parse JSON to object
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            String password = (String) parsedObject.get("actualPassword");
            String newPassword = (String) parsedObject.get("newPassword");

            User user = new User(Account.create(AccountStatus.ONLINE, username, password, null));

            // Check if the password digited is correct
            String querySelect = "SELECT password FROM account WHERE username = '" + user.account.username + "';";
            ResultSet result = null;

            result = DatabaseConnector.getInstance().getStatement().executeQuery(querySelect);

            if (result != null) {
                result.next();
            } else {
                JSONObject returnJson = new JSONObject();
                returnJson.put("status", false);
                returnJson.put("message", "The username does not exists");
                return returnJson.toJSONString();
            }

            String oldPassword = result.getString("password");

            if (!oldPassword.equals(user.account.password)) {
                JSONObject returnJson = new JSONObject();
                returnJson.put("status", false);
                returnJson.put("message", "The previous password does not match");
                return returnJson.toJSONString();
            }

            String queryUpdate = "UPDATE account SET password = '" + newPassword + "' WHERE username = '" + user.account.username + "';";

            DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdate);

            JSONObject returnJson = new JSONObject();
            returnJson.put("status", true);
            returnJson.put("message", "Password changed successfully");
            return returnJson.toJSONString();

        } catch (ClassNotFoundException | SQLException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }
}
