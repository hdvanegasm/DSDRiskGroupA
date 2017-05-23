package server.gamebuilder.controller;


import server.gamebuilder.model.Color;
import server.gamebuilder.model.Host;
import server.gamebuilder.model.Session;
import java.sql.*;
import server.DatabaseConnector;
import server.accountmanager.model.User;
import server.accountmanager.model.AccountStatus;
import server.accountmanager.model.Account;
import server.accountmanager.model.Contact;
import server.gamebuilder.model.Player;
import server.gamebuilder.model.SessionState;


/**
 * 
 * @author David Ochoa
 */
public class SessionBuilder {

    public static boolean createSession(User user, Session session) {
        Host host = new Host(user.account, Color.YELLOW);
        
        // get actual session id and update in the class
        String querySelectId = "SELECT MAX(id) as id FROM session";
        try {
            ResultSet resultset = DatabaseConnector.getInstance().getStatement().executeQuery(querySelectId);
            boolean first = true;
            while (resultset.next()) {
                session.id = resultset.getInt("id") + 1;
                first = false;
            }
            if (first) {
                session.id = 1;
            }
        } catch (Exception e) {
            System.out.println("error: createSession() - " + e.getMessage());
            return false;
        }
        //create session in database
        String queryInsertSession = "INSERT INTO session VALUES (\"" + session.map.name + "\", " + session.id + ", \""+ SessionState.CREATING +"\", \"" + session.type + "\");";
        
        //update host table
        String queryInsertHost = "INSERT INTO host VALUES (\"" + host.account.username + "\",  " + session.id + " );";
        //update player table with host, remember delete player rows when game fihish and make colors dinamic
        String queryInsertHostToPlayer = "INSERT INTO player VALUES(\"" + host.account.username
                + "\", '" + Color.YELLOW + "' , 'non-captured', 0, 0, 0, 0,'"+ Host.class + "'," + session.id + ")";
        String queryUpdateHostStatus = "UPDATE user SET typeOfUser = '"+ Player.class +"' WHERE username = \"" + user.account.username + "\";";
        String queryUpdateUserStatus = "UPDATE account SET status = \"" + AccountStatus.PLAYING + "\" WHERE username = \"" + host.account.username + "\";";
        //simulate another 3 players, this is for the first increment later the request handle this
        
        try {
            // update tables: session, player,host, this the original update
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdateHostStatus);
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryInsertSession);
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryInsertHostToPlayer);
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryInsertHost);
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdateUserStatus);
            session.numberOfPlayers++;
        } catch (Exception e) {
            System.out.println("error: createSession() (2) - " + e.getMessage());
            return false;
        }

        return true;
    }
    
    public static Session joinSession(Contact contact, Session session) {   
        return session;
    }
    
    public static boolean joinSession(User user, Session session) {
        return true;
    }
    
    public static boolean leaveSession(Player player) {
        return true;
    }
    
    public static boolean takeOutPlayer(Player player) {
        return true;
    } 
}
