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

/**
 * This class is implemented in order to manage the operations of the session in
 * the database. It implements some functions that allows to the software to
 * keep the database up to date with the information of the session.
 *
 * @author David Ochoa
 */
public class SessionManager {

    /**
     * This method allow to a user to create a session given the user that makes
     * the request to create a session and a session reference that contains the
     * parameters selected by that user. The method changes the user's type to
     * host and update all the attributes of the session to creating
     *
     * @param user It is the user that makes the request to create the session.
     * @return The method returns "true" if the session was successfully
     * created, otherwise it returns "false"
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the executeQuery method.
     */
    public static boolean createSession(User user) throws ClassNotFoundException, SQLException {
        Host host = new Host(user.account);

        // Get actual session id and update in the class
        String querySelectId = "SELECT MAX(id) as id FROM session";
        int sessionId = 1;
        ResultSet resultset = DatabaseConnector.getInstance().getStatement().executeQuery(querySelectId);
        boolean first = true;
        while (resultset.next()) {
            sessionId = resultset.getInt("id") + 1;
            first = false;
        }

        if (first) {
            sessionId = 1;
        }

        Session session = Session.create(sessionId);
        session.join(host);

        //create session in database
        String queryInsertSession = "INSERT INTO session VALUES (NULL, " + session.id + ", \"" + SessionState.CREATING + "\", NULL);";

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

    /**
     * This method is used when a contact will be joined to a session after he
     * accepts an invitation from the host. This method updates the attributes
     * of the contact to "player" and put his state to "playing". Also the the
     * system assigns a random color to the user and finally add it to the
     * player list of the session.
     *
     * @param user This reference represents the user that will be joined to the
     * session. It is important to notice that due the hierarchy relation
     * between Contact and User, this method can be invoked for objects of both
     * types.
     * @param session This reference represents the session in which the user
     * will be joined.
     * @return The method returns a reference to the player that was joined to
     * the session.
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the executeQuery method.
     */
    public static Player joinToSession(User user, Session session) throws SQLException, ClassNotFoundException {

        // If the limit of players was reached, then the contact cannot be added
        if (session.players.size() == session.numberOfPlayers) {
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
    

    /**
     * This method is implemented in order to remove a player from the database.
     * It is important to notice that this
     *
     * @param session
     * @param player
     * @return
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the executeQuery method.
     */
    public static boolean takeOutPlayerFromSession(Session session, Player player) throws ClassNotFoundException, SQLException {
        player.takeOut(session);
        session.availableColors.add(player.color);

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
        if (occurrences > 0) {
            type = Contact.class.getSimpleName().toUpperCase();
        } else {
            type = "NULL";
        }

        String changeTypeQuery = "";
        if (type.equals("NULL")) {
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
    
    public static boolean leaveSession(Session session, Player player) throws ClassNotFoundException, SQLException {
        session.leave(player);
        session.availableColors.add(player.color);

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
        if (occurrences > 0) {
            type = Contact.class.getSimpleName().toUpperCase();
        } else {
            type = "NULL";
        }

        String changeTypeQuery = "";
        if (type.equals("NULL")) {
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
