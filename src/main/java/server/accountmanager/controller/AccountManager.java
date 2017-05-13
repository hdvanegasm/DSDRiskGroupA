package server.accountmanager.controller;

import server.accountmanager.model.User;
import java.sql.*;

/**
 * This class manage all queries necessary handle the user´s account
 *
 * @author David Ochoa Uribe
 */
public class AccountManager {

    //connection attributes
    private static final int port = 8080;
    private static final String serverPath = "";
    private static final String dataBaseName = "";
    private final static String dataBaseUser = "";
    private final static String dataBasePassword = "";

    private final static String driver = "com.mysql.jdbc.Driver";
    private final static String dataBase = "jdbc:mysql://" + serverPath + ":" + port + "/" + dataBaseName;
    private static Connection connection;
    private static Statement statement;

    /**
     * This method allows the connection with the server´s data base
     */
    public static void connectMySQL() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(dataBase, dataBaseUser, dataBasePassword);
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method allows to create a new user´s account
     *
     * @param user this object has the account's attributes of the new user
     * @return a boolean that indicates the success of the query
     */
    public static boolean createAccount(User user) {
        //insert into account table the new user
        String query = "INSERT INTO ACCOUNT VALUES ( " + user.account.username
                + ", " + user.account.password + ", " + user.account.email + ",0,0,0)";
        try {
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
     * @param user this object has the account's attributes of the user
     * @return a boolean that indicates the success of the query
     */
    public static boolean logOut(User user) {
        // set user's status to offline
        String query = "UPDATE USER SET status = 'Offline' WHERE username = " + user.account.username;
        try {
            statement.executeUpdate(query);
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
     * @param user this object has the account's attributes of the user
     * @return a boolean that indicates the success of the query
     */
    public static boolean logIn(User user) {
        String query1 = "SELECT username, password FROM ACCOUNT WHERE username = " + user.account.username;
        String query2 = "UPDATE USER SET status = 'Online' WHERE username = " + user.account.username;
        //search the username and password
        try {
            ResultSet resultset = statement.executeQuery(query1);
            while (resultset.next()) {
                String auxUssername = resultset.getString("ussername");
                String auxPassword = resultset.getString("password");
                //compare ussername and password in database
                if (auxUssername.equals(user.account.username) && auxPassword.equals(user.account.password)) {
                    try {
                        statement.executeUpdate(query2);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
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
}
