
package server.gamebuilder.controller;

import server.accountmanager.model.User;
import server.gamebuilder.model.Request;
import server.gamebuilder.model.Session;
import server.DatabaseConnector;

/**
 * This class handles the requests and route the answers to database and the host of the session
 * @author Hernán Darío Vanegas Madrigal
 */
public class RequestHandler {
    public static boolean makeRequest(User username, Session session) {
        return true;
    }
    
    public boolean answerRequest(Request request) {
        return true;
    }
}
