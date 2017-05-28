
package server.gamebuilder.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import server.accountmanager.model.User;
import server.gamebuilder.model.Request;
import server.gamebuilder.model.Session;
import server.DatabaseConnector;
import server.gamebuilder.model.RequestState;
import server.gamebuilder.model.SessionState;

/**
 * This class handles the requests and route the answers to database and the host of the session
 * @author Hernán Darío Vanegas Madrigal
 */
public class RequestHandler {
    
    
    
    public static boolean makeRequest(Request request, Session session, User user) throws ClassNotFoundException, SQLException {
        
        //Verify if the session is creating
        String querySession = "SELECT state FROM session WHERE id=" + session.id + ";";
        ResultSet resultQuerySession = DatabaseConnector.getInstance().getStatement().executeQuery(querySession);
        if(resultQuerySession.next()) {
            String stateSession = resultQuerySession.getString("state");
            if(!stateSession.equals(SessionState.CREATING.toString())) {
                return false;
            }
        } 
        
        // Obtain the next ID to the register
        String queryId = "SELECT MAX(id) AS id FROM request;";
        ResultSet result = DatabaseConnector.getInstance().getStatement().executeQuery(queryId);
        int newId = 1;
        if(result.next()) {
            newId = result.getInt("id") + 1;
        }
        
        request.id = newId;
        
        String insertSession = "INSERT INTO request(id, session, state, user) VALUES(" + newId + ", " + session.id + ", '" +
                                request.state + "', '" + user.account.username + "';";
        
        DatabaseConnector.getInstance().getStatement().executeUpdate(insertSession);

        return true;
    }
    
    public boolean answerRequest(Request request) {
        return true;
    }
}
