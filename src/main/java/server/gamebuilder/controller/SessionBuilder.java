package server.gamebuilder.controller;

import server.gamebuilder.model.Color;
import server.gamebuilder.model.Host;
import server.gamebuilder.model.Session;
import java.sql.*;
import server.DatabaseConnector;
import server.accountmanager.model.User;
import server.accountmanager.model.AccountStatus;
import server.accountmanager.model.Contact;
import server.gamebuilder.model.Player;
import server.gamebuilder.model.SessionState;


// TODO Add documentation
/**
 *
 * @author David Ochoa
 */
public class SessionBuilder {

    // TODO Add documentation
    public static boolean createSession(User user, Session session) throws ClassNotFoundException, SQLException {
        Host host = new Host(user.account);
        session.join(host);

        // Get actual session id and update in the class
        String querySelectId = "SELECT MAX(id) as id FROM session";

        ResultSet resultset = DatabaseConnector.getInstance().getStatement().executeQuery(querySelectId);
        boolean first = true;
        while (resultset.next()) {
            session.id = resultset.getInt("id") + 1;
            first = false;
        }
        if (first) {
            session.id = 1;
        }

        //create session in database
        String queryInsertSession = "INSERT INTO session VALUES (\"" + session.map.name + "\", " + session.id + ", \"" + SessionState.CREATING + "\", \"" + session.type + "\");";

        //update host table
        String queryInsertHost = "INSERT INTO host VALUES (\"" + host.account.username + "\",  " + session.id + " );";
        //update player table with host, remember delete player rows when game fihish and make colors dinamic
        String queryInsertHostToPlayer = "INSERT INTO player VALUES(\"" + host.account.username
                + "\", '" + Color.YELLOW + "' , 'non-captured', 0, 0, 0, 0,'" + Host.class.getSimpleName().toUpperCase() + "'," + session.id + ")";
        String queryUpdateHostStatus = "UPDATE user SET typeOfUser = '" + Player.class.getSimpleName().toUpperCase() + "' WHERE username = \"" + user.account.username + "\";";
        String queryUpdateUserStatus = "UPDATE account SET status = \"" + AccountStatus.PLAYING + "\" WHERE username = \"" + host.account.username + "\";";
        //simulate another 3 players, this is for the first increment later the request handle this

        // update tables: session, player,host, this the original update
        DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdateHostStatus);
        DatabaseConnector.getInstance().getStatement().executeUpdate(queryInsertSession);
        DatabaseConnector.getInstance().getStatement().executeUpdate(queryInsertHostToPlayer);
        DatabaseConnector.getInstance().getStatement().executeUpdate(queryInsertHost);
        DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdateUserStatus);

        return true;
    }

    // TODO add documentation
    public static Player joinSession(Contact contact, Session session) throws ClassNotFoundException, SQLException {
        
        // If the limit of players was reached, then the contact cannot be added
        if(session.players.size() == session.numberOfPlayers) {
            return null;
        }
        
        Player newPlayer = session.join(contact);
        
        // Insert player to the database associated with the session
        String insertPlayerQuery = "INSERT INTO player VALUES('" + newPlayer.account.username + "', '" + newPlayer.color + "',\"non-captured\",0,0,0,FALSE,NULL," + session.id + ")";
        DatabaseConnector.getInstance().getStatement().executeUpdate(insertPlayerQuery);

        // Update user status
        String queryUpdateUserStatus = "UPDATE account SET status = \"" + newPlayer.account.status + "\" WHERE username = \"" + newPlayer.account.username + "\";";
        DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdateUserStatus);

        // Update player type
        String queryUpdatePlayerType = "UPDATE user SET typeOfUser = '" + Player.class.getSimpleName().toUpperCase() + "' WHERE username = \"" + newPlayer.account.username + "\";";
        DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdatePlayerType);

        return newPlayer;
    }

    // TODO add documentation
    public static Player joinSession(User user, Session session) throws SQLException, ClassNotFoundException {
        
        // If the limit of players was reached, then the contact cannot be added
        if(session.players.size() == session.numberOfPlayers) {
            return null;
        }
        
        Player newPlayer = session.join(user);

        // Insert player to the database associated with the session
        String insertPlayerQuery = "INSERT INTO player VALUES('" + newPlayer.account.username + "', '" + newPlayer.color + "',\"non-captured\",0,0,0,FALSE,NULL," + session.id + ")";
        DatabaseConnector.getInstance().getStatement().executeUpdate(insertPlayerQuery);

        // Update user status
        String queryUpdateUserStatus = "UPDATE account SET status = \"" + newPlayer.account.status + "\" WHERE username = \"" + newPlayer.account.username + "\";";
        DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdateUserStatus);

        // Update player type
        String queryUpdatePlayerType = "UPDATE user SET typeOfUser = '" + Player.class.getSimpleName().toUpperCase() + "' WHERE username = \"" + newPlayer.account.username + "\";";
        DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdatePlayerType);

        return newPlayer;
    }

    // TODO add documentation
    public static boolean removePlayerFromSession(Session session, Player player) throws ClassNotFoundException, SQLException {
        session.leave(player);
        
        PreparedStatement preparedStatement = null;
        
        // Remove player from BD
        String removePlayerQuery = "DELETE FROM player WHERE username=?";
        preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(removePlayerQuery);
        preparedStatement.setString(1, player.account.username);
        preparedStatement.executeUpdate();
        
        // Change the status to online
        String updateStatusQuery = "UPDATE account SET status=? WHERE username=?";
        preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(updateStatusQuery);
        preparedStatement.setString(1, AccountStatus.ONLINE.toString());
        preparedStatement.setString(2, player.account.username);
        preparedStatement.executeUpdate();
        
        // Change the type of the user. If it is contact, then we change the status to contact
        String numberOccurrencesQuery = "SELECT COUNT(username) as counting FROM contact WHERE username=?";
        preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(numberOccurrencesQuery);
        preparedStatement.setString(1, player.account.username);
        ResultSet result = preparedStatement.executeQuery();
        result.next();
        int occurrences = result.getInt("counting");
        String type = "";
        if(occurrences > 0) {
            type = Contact.class.getSimpleName().toUpperCase();
        } else {
            type = "NULL";
        }
        
        String changeTypeQuery = "";
        if(type.equals("NULL")) {
            changeTypeQuery = "UPDATE user SET typeOfUser=NULL WHERE username=?";
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(type);
            preparedStatement.setString(1, player.account.username);
        } else {
            changeTypeQuery = "UPDATE user SET typeOfUser=? WHERE username=?";
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(changeTypeQuery);
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, player.account.username);
        }
        preparedStatement.executeUpdate();
        
        return true;
    }
}
