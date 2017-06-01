package server.gamebuilder.controller;

import server.gamebuilder.model.Color;
import server.gamebuilder.model.Host;
import server.gamebuilder.model.Session;
import java.sql.*;
import java.util.LinkedList;
import server.DatabaseConnector;
import server.accountmanager.model.Account;
import server.accountmanager.model.User;
import server.accountmanager.model.AccountStatus;
import server.accountmanager.model.Contact;
import server.gamebuilder.model.Map;
import server.gamebuilder.model.Player;
import server.gamebuilder.model.SessionState;
import server.gamebuilder.model.SessionType;

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
        Host host = new Host(user.account, null);

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
     * This method is used when a contact will be joined to a session after he
     * accepts an invitation from the host. This method updates the attributes
     * of the contact to "player" and put his state to "playing". Also the the
     * system assigns a random color to the user and finally add it to the
     * player list of the session.
     *
     * @param contact This reference represents the contact that will be joined to the
     * session. This contact is in the contact list of the host.
     * @param session This reference represents the session in which the contact
     * will be joined.
     * @return The method returns a reference to the player that was joined to
     * the session.
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the executeQuery method.
     */
    public static Player joinToSession(Contact contact, Session session) throws SQLException, ClassNotFoundException {

        // If the limit of players was reached, then the contact cannot be added
        if (session.players.size() == session.numberOfPlayers) {
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

    /**
     * This method is implemented in order to remove a player from the database,
     * more specifically, this method allows to the host to remove a contact
     * from the session that is creating.
     *
     * @param session Represents the session in which the player will be removed
     * @param player This parameter represents the player that will be removed
     * from the session
     * @return The method return a boolean which takes a value of "true" if the
     * player was removed successfully, otherwise it returns "false".
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

    /**
     * This method allows to a player inside a session to leave this session in
     * the "creating" phase. The method removes the player of the player list of
     * the corresponding session. Also the method updates the state of the
     * player to "online".
     *
     * @param session This reference represents the session in which the player will be removed.
     * @param player This is a reference to the player that will be removed from the session.
     * @return The method returns a boolean that takes the value of "true" if the player leaves the session successfully, otherwise returns false.
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the executeQuery method.
     */
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

    /**
     * This method retrieves from the database all of the session which has a
     * creating state.
     *
     * @return The method returns a linked list which contains all of the
     * session that has a "creating" state.
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the executeQuery method.
     */
    public static LinkedList<Session> getAllCreatingSessions() throws ClassNotFoundException, SQLException {
        LinkedList<Session> creatingSessions = new LinkedList<>();

        PreparedStatement preparedStatement = null;

        String sessionsQuery = "SELECT * FROM session WHERE state=?";
        preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(sessionsQuery);
        preparedStatement.setString(1, SessionState.CREATING.toString());

        ResultSet results = preparedStatement.executeQuery();
        while (results.next()) {
            int sessionId = results.getInt("id");
            Map map = new Map(results.getString("map"));
            String typeString = results.getString("type");
            int numberOfPlayers = results.getInt("numberOfPlayers");

            SessionType sessionType = null;
            if (typeString != null) {
                switch (typeString) {
                    case "WORLD_DOMINATION_RISK":
                        sessionType = SessionType.WORLD_DOMINATION_RISK;
                        break;
                    case "RISK_FOR_TWO_PLAYERS":
                        sessionType = SessionType.RISK_FOR_TWO_PLAYERS;
                        break;
                    case "SECRET_MISSION_RISK":
                        sessionType = SessionType.SECRET_MISSION_RISK;
                        break;
                    case "CAPITAL_RISK":
                        sessionType = SessionType.CAPITAL_RISK;
                        break;
                }
            }

            Session session = Session.create(sessionId, numberOfPlayers, sessionType, SessionState.CREATING, map);

            String queryPlayers = "SELECT * FROM player, account WHERE sessionID=? AND player.user=account.username AND type IS NULL";
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(queryPlayers);
            preparedStatement.setInt(1, sessionId);
            ResultSet resultPlayers = preparedStatement.executeQuery();
            while (resultPlayers.next()) {
                String playerUsername = resultPlayers.getString("user");
                String playerColor = resultPlayers.getString("color");
                String playerEmail = resultPlayers.getString("email");
                Color color = null;
                if (playerColor.equals(Color.YELLOW.toString())) {
                    color = Color.YELLOW;
                } else if (playerColor.equals(Color.RED.toString())) {
                    color = Color.RED;
                } else if (playerColor.equals(Color.BLUE.toString())) {
                    color = Color.BLUE;
                } else if (playerColor.equals(Color.GREEN.toString())) {
                    color = Color.GREEN;
                } else if (playerColor.equals(Color.PURPLE.toString())) {
                    color = Color.PURPLE;
                } else if (playerColor.equals(Color.ORANGE.toString())) {
                    color = Color.ORANGE;
                }

                session.availableColors.remove(color);

                Player player = new Player(Account.create(AccountStatus.ONLINE, playerUsername, null, playerEmail), color);

                float percentageOfWins = resultPlayers.getFloat("percentageOfWins");
                int numberOfSessionWon = resultPlayers.getInt("numberOfSessionswon");
                int numberOfSessionLost = resultPlayers.getInt("numberOfSessionLost");
                player.account.numberOfSessionLost = numberOfSessionLost;
                player.account.numberOfSessionWon = numberOfSessionWon;
                player.account.percentageOfWins = percentageOfWins;

                session.players.add(player);

            }

            String queryHost = "SELECT * FROM player, host, account WHERE host.session=? AND host.player=account.username AND host.player=player.user;";
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(queryHost);
            preparedStatement.setInt(1, sessionId);
            ResultSet hostResult = preparedStatement.executeQuery();

            while(hostResult.next()) {
                String hostUsername = hostResult.getString("username");
                String hostColor = hostResult.getString("color");
                String hostEmail = hostResult.getString("email");
                Color color = null;
                if (hostColor.equals(Color.YELLOW.toString())) {
                    color = Color.YELLOW;
                } else if (hostColor.equals(Color.RED.toString())) {
                    color = Color.RED;
                } else if (hostColor.equals(Color.BLUE.toString())) {
                    color = Color.BLUE;
                } else if (hostColor.equals(Color.GREEN.toString())) {
                    color = Color.GREEN;
                } else if (hostColor.equals(Color.PURPLE.toString())) {
                    color = Color.PURPLE;
                } else if (hostColor.equals(Color.ORANGE.toString())) {
                    color = Color.ORANGE;
                }

                session.availableColors.remove(color);

                Host host = new Host(Account.create(AccountStatus.ONLINE, hostUsername, null, hostEmail), color);

                // TODO Take statistics of player
                float percentageOfWins = hostResult.getFloat("percentageOfWins");
                int numberOfSessionWon = hostResult.getInt("numberOfSessionswon");
                int numberOfSessionLost = hostResult.getInt("numberOfSessionLost");
                host.account.numberOfSessionLost = numberOfSessionLost;
                host.account.numberOfSessionWon = numberOfSessionWon;
                host.account.percentageOfWins = percentageOfWins;

                session.players.add(host);

            }

            creatingSessions.add(session);
        }
        return creatingSessions;
    }
    
     public static LinkedList<Player> getPlayersFromSession(int sessionId) throws SQLException, ClassNotFoundException {
        LinkedList<Player> players = new LinkedList<>();

        String queryPlayers = "SELECT * FROM player, account WHERE sessionID=? AND player.user=account.username AND type IS NULL";
        PreparedStatement preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(queryPlayers);
        preparedStatement.setInt(1, sessionId);
        ResultSet resultPlayers = preparedStatement.executeQuery();
        while (resultPlayers.next()) {
            String playerUsername = resultPlayers.getString("user");
            String playerColor = resultPlayers.getString("color");
            String playerEmail = resultPlayers.getString("email");
            Color color = null;
            if (playerColor.equals(Color.YELLOW.toString())) {
                color = Color.YELLOW;
            } else if (playerColor.equals(Color.RED.toString())) {
                color = Color.RED;
            } else if (playerColor.equals(Color.BLUE.toString())) {
                color = Color.BLUE;
            } else if (playerColor.equals(Color.GREEN.toString())) {
                color = Color.GREEN;
            } else if (playerColor.equals(Color.PURPLE.toString())) {
                color = Color.PURPLE;
            } else if (playerColor.equals(Color.ORANGE.toString())) {
                color = Color.ORANGE;
            }

            Player player = new Player(Account.create(AccountStatus.ONLINE, playerUsername, null, playerEmail), color);

            float percentageOfWins = resultPlayers.getFloat("percentageOfWins");
            int numberOfSessionWon = resultPlayers.getInt("numberOfSessionswon");
            int numberOfSessionLost = resultPlayers.getInt("numberOfSessionLost");
            player.account.numberOfSessionLost = numberOfSessionLost;
            player.account.numberOfSessionWon = numberOfSessionWon;
            player.account.percentageOfWins = percentageOfWins;

            players.add(player);

        }
        return players;
    }
}
