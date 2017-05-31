package server.accountmanager.controller;

import server.accountmanager.model.User;
import java.sql.*;
import server.DatabaseConnector;
import server.accountmanager.model.AccountStatus;

/**
 * This class manage all queries necessary handle the user´s account
 *
 * @author David Ochoa Uribe
 */
public class AccountManager {

    /**
     * This method allows to create a new user's account
     *
     * @param user this object has the attributes of the new user's account
     * @return a boolean that indicates the success of the query
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static boolean createAccount(User user) throws ClassNotFoundException, SQLException {
        //insert into account table the new user

        String query = "INSERT INTO user VALUES (NULL, \"" + user.account.username + "\");";

        DatabaseConnector.getInstance().getStatement().executeUpdate(query);
        query = "INSERT INTO account VALUES ( \"" + user.account.username
                + "\" , \"" + user.account.password + "\", \"" + user.account.email + "\", 0, 0, 0,'" + AccountStatus.OFFLINE + "');";
        DatabaseConnector.getInstance().getStatement().executeUpdate(query);

        return true;
    }

    /**
     * This method update the status of the user in the database after logout
     *
     * @param user this object has the attributes of the user's account
     * @return a boolean that indicates the success of the query
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static boolean logOut(User user) throws ClassNotFoundException, SQLException {
        // set user's status to offline
        String query = "UPDATE account SET status = '" + AccountStatus.OFFLINE + "' WHERE username = \"" + user.account.username + "\";";

        DatabaseConnector.getInstance().getStatement().executeUpdate(query);

        return true;
    }

    /**
     * This method compare the user´s log-in information with the database, if
     * it´s correct updates the status of the user in the database
     *
     * @param user this object has the attributes of the user's account
     * @return a boolean that indicates the success of the query
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public static boolean logIn(User user) throws SQLException, ClassNotFoundException {
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
                return true;

            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Method used in order to change the password of the account
     *
     * @param user It represents the information of the user that will change
     * the password
     * @param newPassword This attribute represents the new password of the
     * account
     * @return The method returns true if the password was changed successfully,
     * otherwise it returns false.
     * @throws SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public static boolean changePassword(User user, String newPassword) throws SQLException, ClassNotFoundException {

        // Check if the password digited is correct
        String querySelect = "SELECT password FROM account WHERE username = '" + user.account.username + "';";
        ResultSet result = null;

        result = DatabaseConnector.getInstance().getStatement().executeQuery(querySelect);

        if (result != null) {
            result.next();
        } else {
            return false;
        }

        String oldPassword = result.getString("password");

        if (!oldPassword.equals(user.account.password)) {
            return false;
        }

        String queryUpdate = "UPDATE account SET password = '" + newPassword + "' WHERE username = '" + user.account.username + "';";

        DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdate);

        return true;
    }
}
