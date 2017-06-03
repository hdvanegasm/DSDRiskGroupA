package server.gamebuilder.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.accountmanager.model.User;
import server.gamebuilder.model.Request;
import server.gamebuilder.model.Session;
import server.DatabaseConnector;
import server.accountmanager.model.Account;
import server.accountmanager.model.AccountStatus;
import server.gamebuilder.model.RequestState;
import server.gamebuilder.model.SessionState;

/**
 * This class handles the requests and route the answers to database and the
 * host of the session
 *
 * @author Hernán Darío Vanegas Madrigal
 */
public class RequestHandler {

    /**
     * This method allows to a user to make a request to a session in order to
     * ask for authorization to enter to the session construction. The method
     * creates a new request and adds it to the database.
     *
     * @param session It represents the session which will receive the request
     * for access
     * @param user This is a reference to the user that is sending the
     * invitation, this user must be online and the session must have a
     * "creating" status in order to make a successful request
     * @return The method returns "true" if the method make a request
     * successfully, otherwise the method returns false.
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the executeQuery method.
     */
    public static String makeRequest(String json) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            int idSession = (int) parsedObject.get("sessionId");

            Session session = Session.create(idSession);
            User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));

            //Verify if the session is creating
            String querySession = "SELECT state FROM session WHERE id=" + session.id + ";";
            ResultSet resultQuerySession = null;

            resultQuerySession = DatabaseConnector.getInstance().getStatement().executeQuery(querySession);

            if (resultQuerySession.next()) {
                String stateSession = resultQuerySession.getString("state");
                if (!stateSession.equals(SessionState.CREATING.toString())) {
                    JSONObject returnJson = new JSONObject();
                    returnJson.put("status", false);
                    returnJson.put("message", "You cannot join to this session now");
                    return returnJson.toJSONString();
                }
            } else {
                JSONObject returnJson = new JSONObject();
                returnJson.put("status", false);
                returnJson.put("message", "This session does not exist");
                return returnJson.toJSONString();
            }

            // Obtain the next ID to the register
            String queryId = "SELECT MAX(id) AS id FROM request;";
            ResultSet result = DatabaseConnector.getInstance().getStatement().executeQuery(queryId);
            int newId = 1;

            if (result.next()) {
                newId = result.getInt("id") + 1;
            }

            Request request = Request.make(session, user);
            request.id = newId;

            String insertRequestQuery = "INSERT INTO request(id, session, state, username) VALUES(" + newId + ", " + session.id + ", '"
                    + request.state + "', '" + user.account.username + "');";
            DatabaseConnector.getInstance().getStatement().executeUpdate(insertRequestQuery);

            JSONObject returnJson = new JSONObject();
            returnJson.put("status", true);
            returnJson.put("message", "You are joined to the session");
            return returnJson.toJSONString();
        } catch (SQLException | ClassNotFoundException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }

    }

    /**
     * This method allows to the host to answer a request that was sent to his
     * session. The method modify the request status in the database and returns
     * a boolean according to the answer.
     *
     * @param request It is the object of the request that will be answered. It
     * has all of the information needed to answer the request and update the
     * information in the database.
     * @param response It is an instance of the enumeration class that
     * represents the answer of the host of the session.
     * @return The method returns "true" if the the host "accepts" the request,
     * otherwise it returns "false".
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the executeQuery method.
     */
    public static String answerRequest(String json) throws ParseException {

        JSONParser parser = new JSONParser();
        String jsonToString = "[" + json + "]";
        Object obj = parser.parse(jsonToString);
        JSONArray jsonArray = (JSONArray) obj;

        JSONObject parsedObject = (JSONObject) jsonArray.get(0);

        int idSession = (int) parsedObject.get("sessionId");
        boolean response = (boolean) parsedObject.get("response");

        Request request = new Request(idSession, RequestState.UNANSWERED);

        RequestState responseState = null;

        if (response) {
            responseState = RequestState.ACCEPTED;
        } else {
            responseState = RequestState.NOT_ACCEPTED;
        }

        //Initializes the value of the answer
        boolean answer = false;

        if (request.state == RequestState.UNANSWERED) {
            try {
                answer = request.answer(responseState);
                String queryUpdate = "UPDATE request SET state= '" + request.state + "' WHERE id = " + request.id + ";";
                DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdate);
            } catch (ClassNotFoundException | SQLException ex) {
                JSONObject returnJson = new JSONObject();
                returnJson.put("status", false);
                returnJson.put("message", ex.getMessage());
                return returnJson.toJSONString();
            }
        }
        if (answer) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", answer);
            returnJson.put("message", "Request accepted");
            return returnJson.toJSONString();
        } else {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", answer);
            returnJson.put("message", "Request rejected");
            return returnJson.toJSONString();
        }

    }
}
