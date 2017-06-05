package server.gamebuilder.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
     * @param json This attribute represents a JSON string that contains the
     * username of the user that wants to join to the session and the ID of the
     * session in which the user wants to join.
     * @return The method retuns a JSON that contains two fields: the first
     * field is a status about the request sending, if the status is "true",
     * then the request was registered successfully, otherwise the status is
     * established to "false"; the second field is a message related to the
     * state of the request sending.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public synchronized static String makeRequest(String json) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            int idSession = Integer.parseInt(String.valueOf(parsedObject.get("sessionId")));

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
            returnJson.put("message", "Your request was sent successfully");
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
     * @param json This attribute represents a JSON string that contains the ID
     * of a request and the response provided by the host of the session.
     * @return The method returns three fields in a JSON string: the first field
     * is the status of the transaction in the database, if the transaction has
     * some problem like a database exception, then the status value is changed
     * to "false", otherwise it is true; the second attribute is the response
     * provided by the host in the session that is a boolean value, this boolean
     * takes the value of "true" if the request was accepted, otherwise it
     * returns "false"; the third field is a message that describes the status
     * of the process.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public synchronized static String answerRequest(String json) throws ParseException {

        JSONParser parser = new JSONParser();
        String jsonToString = "[" + json + "]";
        Object obj = parser.parse(jsonToString);
        JSONArray jsonArray = (JSONArray) obj;

        JSONObject parsedObject = (JSONObject) jsonArray.get(0);

        int requestId = Integer.parseInt(String.valueOf(parsedObject.get("requestId")));
        boolean response = (boolean) parsedObject.get("response");

        Request request = new Request(requestId, RequestState.UNANSWERED);

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
            returnJson.put("status", true);
            returnJson.put("response", answer);
            returnJson.put("message", "Request accepted");
            return returnJson.toJSONString();
        } else {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", true);
            returnJson.put("response", answer);
            returnJson.put("message", "Request rejected");
            return returnJson.toJSONString();
        }
    }

    /**
     * This method provides a JSON that contains all of the request sent to a
     * session, it is important to notice that all of these request have a
     * "unanswered" status. This method makes a query in the database in order
     * to accomplish this task.
     *
     * @param json This parameter represents a JSON string that contains the ID
     * of the session which will be used to retrieve all of the requests.
     * @return The method returns a JSON string that contains all of the request
     * in the database which have an "unanswered" status.
     * @throws org.json.simple.parser.ParseException This exeption is thrown if
     * the JSON in the parameter has a syntax error.
     */
    public synchronized static String getAllUnansweredRequests(String json) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            int sessionId = Integer.parseInt(String.valueOf(parsedObject.get("sessionId")));

            PreparedStatement preparedStatement = null;

            String getRequestsQuery = "SELECT * FROM request WHERE session=? AND state=?";
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(getRequestsQuery);
            preparedStatement.setInt(1, sessionId);
            preparedStatement.setString(2, RequestState.UNANSWERED.toString());
            ResultSet resultQuery = preparedStatement.executeQuery();

            JSONObject jsonResult = new JSONObject();
            JSONArray requests = new JSONArray();

            while (resultQuery.next()) {
                int requestId = resultQuery.getInt("id");
                int requestSessionId = resultQuery.getInt("session");
                String username = resultQuery.getString("username");

                JSONObject request = new JSONObject();
                request.put("id", requestId);
                request.put("session", requestSessionId);
                request.put("username", username);
                request.put("state", RequestState.UNANSWERED.toString());

                requests.add(request);
            }

            jsonResult.put("requests", requests);
            jsonResult.put("status", true);
            return jsonResult.toJSONString();
        } catch (ClassNotFoundException | SQLException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }
}
