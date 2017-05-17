
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is a connection channel between the program and the database. It uses the singleton pattern in order to ensure the existence of one instance of the class
 * @author Hernan Darío Vanegas Madrigal
 */
public class DatabaseConnector {

    // Server port and database information 
    private int port = 3306;
    private String serverPath;
    private String dataBaseName;
    private String dataBaseUser;
    private String dataBasePassword;

    // Connection instances with JDBC
    private String driver;
    private String dataBase;
    private Connection connection;
    private Statement statement;    
    
    // Singleton instance
    private static DatabaseConnector instance;
    
    /**
     * This method constructs the instance of the connector with the specified requirements and information of the server.
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
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

    /**
     * Singleton method of the class in order to ensure a unique instance.
     * @return The method returns the instance of the class. It is important to notice that this instance is unique in the server.
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public static DatabaseConnector getInstance() throws ClassNotFoundException, SQLException {
        if (instance == null) {
            instance = new DatabaseConnector();
            return instance;
        } else {
            return instance;
        }
    }

    /**
     * Getter method to access to statement object. This object is necessary to execute some queries.
     * @return The method returns a reference to the statement object that will be used to execute some queries.
     */
    public Statement getStatement() {
        return statement;
    }
}
