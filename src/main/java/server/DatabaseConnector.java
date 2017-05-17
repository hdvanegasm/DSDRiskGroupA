/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author lemark
 */
public class DatabaseConnector {

    private int port = 3306;
    private String serverPath;
    private String dataBaseName;
    private String dataBaseUser;
    private String dataBasePassword;

    private String driver;
    private String dataBase;
    private Connection connection;
    private Statement statement;

    private static DatabaseConnector instance;

    public DatabaseConnector() throws ClassNotFoundException, SQLException {
        port = 3306;
        serverPath = "localhost";
        dataBaseName = "dsdrisk";
        dataBaseUser = "dsdriskuser";
        dataBasePassword = "12345";

        driver = "com.mysql.jdbc.Driver";
        dataBase = "jdbc:mysql://" + serverPath + ":" + port + "/" + dataBaseName;

        Class.forName(driver);
        connection = DriverManager.getConnection(dataBase, dataBaseUser, dataBasePassword);
        statement = connection.createStatement();
    }

    public static DatabaseConnector getInstance() throws ClassNotFoundException, SQLException {
        if (instance == null) {
            instance = new DatabaseConnector();
            return instance;
        } else {
            return instance;
        }
    }

    public Statement getStatement() {
        return statement;
    }
}
