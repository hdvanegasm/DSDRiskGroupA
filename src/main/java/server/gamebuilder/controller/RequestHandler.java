package server.gamebuilder.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.accountmanager.model.User;
import server.gamebuilder.model.Request;
import server.gamebuilder.model.Session;
import server.DatabaseConnector;
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
     * ask for autorization to enter to the session construction. The method
     * creates a new request and adds it to the database.
     *
     * @param session It represents the session which will receive the request
     * for access
     * @param user This is a reference to the user that is sending the
     * invitation, this user must be online and the session must have a
     * "creating" status in order to make a successful request
     * @return The method returns "true" if the method make a request
     * successfully, otherwise the method returns false.
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public static boolean makeRequest(Session session, User user) throws SQLException, ClassNotFoundException {

        //Verify if the session is creating
        String querySession = "SELECT state FROM session WHERE id=" + session.id + ";";
        ResultSet resultQuerySession = null;

        resultQuerySession = DatabaseConnector.getInstance().getStatement().executeQuery(querySession);

        if (resultQuerySession.next()) {
            String stateSession = resultQuerySession.getString("state");
            if (!stateSession.equals(SessionState.CREATING.toString())) {
                return false;
            }
        } else {
            return false;
        }

        // Obtain the next ID to the register
        String queryId = "SELECT MAX(id) AS id FROM request;";
        ResultSet result = DatabaseConnector.getInstance().getStatement().executeQuery(queryId);
        int newId = 1;

        if (result.next()) {
            newId = result.getInt("id") + 1;
        }

        Request request = Request.make(session);
        request.id = newId;

        String insertRequestQuery = "INSERT INTO request(id, session, state, username) VALUES(" + newId + ", " + session.id + ", '"
                + request.state + "', '" + user.account.username + "');";
        System.out.println(insertRequestQuery);
        DatabaseConnector.getInstance().getStatement().executeUpdate(insertRequestQuery);

        return true;

    }

    /**
     * This method allows to the host to anser a request that was sended to his
     * session. The method modify the request status in the database and returns
     * a boolean according to the answer.
     *
     * @param request It is the object of the request that will be answered. It
     * has all of the information needed to anser the request and update the
     * information in the database.
     * @param response It is an instance of the ennumeration class that
     * represents the answer of the host of the session.
     * @return The method returns "true" if the the host "accepts" the request,
     * otherwise it returns "false".
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static boolean answerRequest(Request request, RequestState response) throws ClassNotFoundException, SQLException {

        //Initializes the value of the answer
        boolean answer = false;

        if (request.state == RequestState.UNANSWERED) {
            answer = request.answer(response);

            // Update query
            String queryUpdate = "UPDATE request SET state= '" + request.state + "' WHERE id = " + request.id + ";";
            DatabaseConnector.getInstance().getStatement().executeUpdate(queryUpdate);
        }
        return answer;
    }
}
