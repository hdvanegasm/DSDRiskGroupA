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
     * This method allows the connection with the server´s data base
     */
    /**
     * This method allows to create a new user´s account
     *
     * @param user this object has the attributes of the new user's account
     * @return a boolean that indicates the success of the query
     */
    public static boolean createAccount(User user) {
        //insert into account table the new user
        
        try {
            String query = "INSERT INTO user VALUES (NULL, \"" + user.account.username + "\");";
            
            DatabaseConnector.getInstance().getStatement().executeUpdate(query);
            query = "INSERT INTO account VALUES ( \"" + user.account.username
                + "\" , \"" + user.account.password + "\", \"" + user.account.email + "\", 0, 0, 0,'" + AccountStatus.OFFLINE +"');";
            DatabaseConnector.getInstance().getStatement().executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Fatal error: createAccount() - " + e);
            return false;
        }
        return true;
    }

    public static boolean updatePassword(User user) {
        // TODO Update password of the account
        return true;
    }

    /**
     * This method update the status of the user in the database after logout
     *
     * @param user this object has the attributes of the user's account
     * @return a boolean that indicates the success of the query
     */
    public static boolean logOut(User user) {
        // set user's status to offline
        String query = "UPDATE account SET status = '" + AccountStatus.OFFLINE + "' WHERE username = \"" + user.account.username + "\";";
        try {
            DatabaseConnector.getInstance().getStatement().executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * This method compare the user´s log-in information with the database, if
     * it´s correct updates the status of the user in the database
     *
     * @param user this object has the attributes of the user's account
     * @return a boolean that indicates the success of the query
     */
    public static boolean logIn(User user) {
        String querySelect = "SELECT username, password FROM account WHERE username = \"" + user.account.username + "\";";
        String queryUpdate = "UPDATE account SET status = '"+ AccountStatus.ONLINE +"' WHERE username = \"" + user.account.username + "\";";
        //search the username and password
        try {
            ResultSet resultset = DatabaseConnector.getInstance().getStatement().executeQuery(querySelect);
            while (resultset.next()) {
                String auxUsername = resultset.getString("username");
                String auxPassword = resultset.getString("password");
                //compare ussername and password in database
                if (auxUsername.equals(user.account.username) && auxPassword.equals(user.account.password)) {
                    try {
                        DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdate);
                        return true;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    
    public static boolean changePassword(User user, String newPassword) {
        String queryUpdate = "UPDATE account SET password = '" + newPassword + "' + WHERE username = '" + user.account.username +"'";
        try {
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdate);
        } catch (Exception e) {
            System.out.println("Error at changePassword()");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
