package server.gamebuilder.controller;

import server.gamebuilder.model.Color;
import server.gamebuilder.model.Host;
import server.gamebuilder.model.Session;
import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
     * @param json The method receives a JSON that contains the username of the
     * user that wants to create the session.
     * @return The method returns a JSON with a status field, this field takes a
     * value "true" if the session was successfully created, otherwise it
     * returns "false". Also the method returns a message with the success of
     * the operation or the exception in case of error.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public static String createSession(String json) throws ParseException {

        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String hostUsername = (String) parsedObject.get("username");

            Account account = Account.create(AccountStatus.ONLINE, hostUsername, null, null);
            Host host = new Host(account, null);

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
            String queryUpdateHostStatus = "UPDATE user SET typeOfUser = '" + Player.class.getSimpleName().toUpperCase() + "' WHERE username = \"" + host.account.username + "\";";
            String queryUpdateUserStatus = "UPDATE account SET status = \"" + AccountStatus.PLAYING + "\" WHERE username = \"" + host.account.username + "\";";
            //simulate another 3 players, this is for the first increment later the request handle this

            // update tables: session, player,host, this the original update
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdateHostStatus);
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryInsertSession);
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryInsertHostToPlayer);
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryInsertHost);
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdateUserStatus);

            JSONObject returnJson = new JSONObject();
            returnJson.put("status", true);
            returnJson.put("sessionId", session.id);
            returnJson.put("message", "Session created");
            return returnJson.toJSONString();
        } catch (SQLException | ClassNotFoundException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }

    /**
     * This method is used when a contact will be joined to a session after he
     * accepts an invitation from the host. This method updates the attributes
     * of the contact to "player" and put his state to "playing". Also the the
     * system assigns a random color to the user and finally add it to the
     * player list of the session.
     *
     * @param json The method receives a JSON that contains the username of the
     * user that will be joined to the session and the session ID of the
     * respective session.
     * @return The method returns a reference to the player that was joined to
     * the session.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public static String joinToSession(String json) throws ParseException {

        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            int sessionId = Integer.parseInt(String.valueOf(parsedObject.get("sessionId")));

            Session session = Session.create(sessionId);

            LinkedList<Player> playerList = getPlayerListFromSession(sessionId);
            Iterator<Player> playerIterator = playerList.listIterator();
            while (playerIterator.hasNext()) {
                Player actualPlayer = playerIterator.next();
                session.players.add(actualPlayer);
                session.availableColors.remove(actualPlayer.color);
            }

            // Construction of user
            User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));

            // If the limit of players was reached, then the contact cannot be added
            if (session.players.size() == 6) {
                JSONObject returnJson = new JSONObject();
                returnJson.put("status", false);
                returnJson.put("message", "Limir of players reached");
                return returnJson.toJSONString();
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

            JSONObject returnJson = new JSONObject();
            returnJson.put("status", true);
            returnJson.put("message", "User joined to session successfully");
            return returnJson.toJSONString();
        } catch (SQLException | ClassNotFoundException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }

    /**
     * This method is implemented in order to remove a player from the database,
     * more specifically, this method allows to the host to remove a contact
     * from the session that is creating.
     *
     * @param json The method receives a JSON that contains the username of the
     * user that will be taken out from the session and the respective session
     * id.
     * @return The method return a JSON with two fields, the first field is a
     * status flag which takes a value of "true" if the player was removed
     * successfully, otherwise it returns "false", and a message field that
     * contains a confirmation message of the transaction or a message that
     * contains the exception thrown if an error occurs.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public static String takeOutPlayerFromSession(String json) throws ParseException {

        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            int sessionId = Integer.parseInt(String.valueOf(parsedObject.get("sessionId")));

            Session session = Session.create(sessionId, null, SessionState.CREATING, null);
            Player player = new Player(Account.create(AccountStatus.ONLINE, username, null, null), null);

            player.takeOut(session);
            session.availableColors.add(player.color);

            PreparedStatement preparedStatement = null;

            // Remove player from BD
            String removePlayerQuery = "DELETE FROM player WHERE user=?";
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
                preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(changeTypeQuery);
                preparedStatement.setString(1, player.account.username);
            } else {
                changeTypeQuery = "UPDATE user SET typeOfUser=? WHERE username=?";
                preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(changeTypeQuery);
                preparedStatement.setString(1, type);
                preparedStatement.setString(2, player.account.username);
            }
            preparedStatement.executeUpdate();

            JSONObject returnJson = new JSONObject();
            returnJson.put("status", true);
            returnJson.put("message", "Contact removed successfully");
            return returnJson.toJSONString();
        } catch (SQLException | ClassNotFoundException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }

    /**
     * This method allows to a player inside a session to leave this session in
     * the "creating" phase. The method removes the player of the player list of
     * the corresponding session. Also the method updates the state of the
     * player to "online".
     *
     * @param json The method receives a JSON that contains the username of the
     * user that wants to leave the session and the respective session ID.
     * @return The method return a JSON with two fields, the first field is a
     * status flag which takes a value of "true" if the player leaves the
     * session successfully, otherwise it returns "false", and a message field
     * that contains a confirmation message of the transaction or a message that
     * contains the exception thrown if an error occurs.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public static String leaveSession(String json) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            int sessionId = Integer.parseInt(String.valueOf(parsedObject.get("sessionId")));

            Session session = Session.create(sessionId, null, SessionState.CREATING, null);
            Player player = new Player(Account.create(AccountStatus.ONLINE, username, null, null), null);

            session.leave(player);
            session.availableColors.add(player.color);

            PreparedStatement preparedStatement = null;

            // Remove player from BD
            String removePlayerQuery = "DELETE FROM player WHERE user=?";
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
                preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(changeTypeQuery);
                preparedStatement.setString(1, player.account.username);
            } else {
                changeTypeQuery = "UPDATE user SET typeOfUser=? WHERE username=?";
                preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(changeTypeQuery);
                preparedStatement.setString(1, type);
                preparedStatement.setString(2, player.account.username);
            }
            preparedStatement.executeUpdate();

            JSONObject returnJson = new JSONObject();
            returnJson.put("status", true);
            returnJson.put("message", "You have leave the session successfully");
            return returnJson.toJSONString();
        } catch (SQLException | ClassNotFoundException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
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
    private static LinkedList<Session> getAllCreatingSessionList() throws ClassNotFoundException, SQLException {
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

            Session session = Session.create(sessionId, sessionType, SessionState.CREATING, map);

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

            while (hostResult.next()) {
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

    /**
     * The method parses is used in order to return all the creating sessions in
     * a JSON. It uses the getAllCreatingSessionList() method in order to
     * retrieve all the session in a linked list, and finally it parses this
     * list into a JSON string.
     *
     * @return The method returns a JSON with all of the session with "creating"
     * status.
     */
    public static String getAllCreatingSession() {
        try {
            LinkedList<Session> allSessions = getAllCreatingSessionList();

            JSONObject jsonResult = new JSONObject();

            Iterator<Session> sessionIterator = allSessions.listIterator();

            JSONArray arraySessionJson = new JSONArray();

            while (sessionIterator.hasNext()) {
                Session actualSession = sessionIterator.next();

                JSONObject session = new JSONObject();
                session.put("id", actualSession.id);
                session.put("map", actualSession.map.name);
                session.put("type", actualSession.type);

                JSONArray playersJson = new JSONArray();

                Iterator<Player> playerIterator = actualSession.players.listIterator();
                while (playerIterator.hasNext()) {
                    Player actualPlayer = playerIterator.next();

                    JSONObject player = new JSONObject();
                    player.put("username", actualPlayer.account.username);
                    player.put("color", actualPlayer.color.toString());
                    player.put("email", actualPlayer.account.email);
                    player.put("percentajeOfWins", actualPlayer.account.percentageOfWins);
                    player.put("numberOfSessionsWon", actualPlayer.account.numberOfSessionWon);
                    player.put("numberOfSessionLost", actualPlayer.account.numberOfSessionLost);
                    player.put("status", actualPlayer.account.status.toString());

                    if (actualPlayer instanceof Host) {
                        player.put("type", Host.class.getSimpleName().toUpperCase());
                    } else {
                        player.put("type", Player.class.getSimpleName().toUpperCase());
                    }

                    playersJson.add(player);
                }

                session.put("players", playersJson);

                arraySessionJson.add(session);
            }
            jsonResult.put("sessions", arraySessionJson);
            jsonResult.put("status", true);
            return jsonResult.toJSONString();
        } catch (ClassNotFoundException | SQLException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }

    /**
     * This method is implemented in order to obtain all of the players in a
     * session from the database in order to send this information to the client
     * side. It assemble all of the attributes of the player and also extract
     * the host in a separate query.
     *
     * @param sessionId It represents the ID of the session that has all of the
     * players.
     * @return The method returns a linked list with all of the players of this
     * session.
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the executeQuery method.
     */
    private static LinkedList<Player> getPlayerListFromSession(int sessionId) throws SQLException, ClassNotFoundException {
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

    /**
     * This method builds a JSON string with all of the players in a session.
     * This method uses getPlayerListFromSession() method in order to obtain a
     * linked list and parse it into a JSON string.
     *
     * @param json This parameter represents a JSON that contains the session ID
     * wich will be used to extract all of the player of this session.
     * @return The method retursn a JSON that contains the players of the
     * session.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public static String getPlayersFromSession(String json) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            int sessionId = (int) parsedObject.get("sessionId");

            LinkedList<Player> players = getPlayerListFromSession(sessionId);

            JSONObject resultJson = new JSONObject();

            JSONArray playersJson = new JSONArray();

            Iterator<Player> playerIterator = players.listIterator();
            while (playerIterator.hasNext()) {
                Player actualPlayer = playerIterator.next();

                JSONObject player = new JSONObject();
                player.put("username", actualPlayer.account.username);
                player.put("color", actualPlayer.color);
                player.put("email", actualPlayer.account.email);
                player.put("percentajeOfWins", actualPlayer.account.percentageOfWins);
                player.put("numberOfSessionsWon", actualPlayer.account.numberOfSessionWon);
                player.put("numberOfSessionLost", actualPlayer.account.numberOfSessionLost);
                player.put("status", actualPlayer.account.status.toString());
                playersJson.add(player);
            }

            resultJson.put("players", playersJson);
            resultJson.put("status", true);
            return resultJson.toJSONString();
        } catch (SQLException | ClassNotFoundException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }

    public static String startSession(String json) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            int sessionId = Integer.parseInt(String.valueOf(parsedObject.get("sessionId")));
            String type = String.valueOf(parsedObject.get("type"));
            String mapName = String.valueOf("mapName");

            SessionType sessionType = null;

            switch (type) {
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

            Session session = Session.create(sessionId, sessionType, SessionState.CREATING, new Map(mapName));

            // Starts session model
            session.start();

            // Update session attributes
            String queryUpdate = "UPDATE session SET map=?, type=?, state=? WHERE id=?";
            PreparedStatement preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(queryUpdate);
            preparedStatement.setString(1, session.map.name);
            preparedStatement.setString(2, session.type.toString());
            preparedStatement.setString(3, session.state.toString());
            preparedStatement.setInt(4, session.id);
            preparedStatement.executeUpdate();

            // Get all players
            session.players = getPlayerListFromSession(sessionId);

            // Get session host
            String queryHost = "SELECT * FROM player, host, account WHERE host.session=? AND host.player=account.username AND host.player=player.user;";
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(queryHost);
            preparedStatement.setInt(1, sessionId);
            ResultSet hostResult = preparedStatement.executeQuery();

            while (hostResult.next()) {
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

            // Constructs the JSON return
            JSONObject resultJson = new JSONObject();
            resultJson.put("id", session.id);
            resultJson.put("map", session.map.name);
            resultJson.put("type", session.type.toString());

            JSONArray playersJson = new JSONArray();

            Iterator<Player> playerIterator = session.players.listIterator();
            while (playerIterator.hasNext()) {
                Player actualPlayer = playerIterator.next();

                JSONObject player = new JSONObject();
                player.put("username", actualPlayer.account.username);
                player.put("color", actualPlayer.color.toString());
                player.put("email", actualPlayer.account.email);
                player.put("percentajeOfWins", actualPlayer.account.percentageOfWins);
                player.put("numberOfSessionsWon", actualPlayer.account.numberOfSessionWon);
                player.put("numberOfSessionLost", actualPlayer.account.numberOfSessionLost);
                player.put("status", actualPlayer.account.status.toString());

                if (actualPlayer instanceof Host) {
                    player.put("type", Host.class.getSimpleName().toUpperCase());
                } else {
                    player.put("type", Player.class.getSimpleName().toUpperCase());
                }

                playersJson.add(player);
            }

            resultJson.put("players", playersJson);
            
            return resultJson.toJSONString();

        } catch (SQLException | ClassNotFoundException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }
}
