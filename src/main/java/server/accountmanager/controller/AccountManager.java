/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.accountmanager.controller;

import server.accountmanager.model.User;
import java.sql.*;

/**
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

    //connection method
    public static void connectMySQL() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(dataBase, dataBaseUser, dataBasePassword);
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
