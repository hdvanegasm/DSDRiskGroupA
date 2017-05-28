/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.accountmanager.model.Account;
import server.accountmanager.model.AccountStatus;
import server.accountmanager.model.User;
import server.gamebuilder.controller.RequestHandler;
import server.gamebuilder.model.Request;
import server.gamebuilder.model.RequestState;
import server.gamebuilder.model.Session;
import server.gamebuilder.model.SessionState;
import server.gamebuilder.model.SessionType;

/**
 *
 * @author Admin
 */
public class Test {

    public static boolean makeRequestTest() {
        String username = "hernan";
        int idSession = 1;

        Request userRequest = new Request(RequestState.UNANSWERED);
        Session session = Session.create(idSession);
        User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));
        
        try {
            return RequestHandler.makeRequest(userRequest, session, user);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static boolean answerRequestTest() {
        Session session = Session.create(1);
        Request request = new Request(1, RequestState.UNANSWERED);
        boolean response;
        try {
            response = RequestHandler.answerRequest(request, RequestState.ACCEPTED);
        } catch (ClassNotFoundException ex) {
            response = false;
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            response = false;
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    public static void main(String[] args) {
        answerRequestTest();
    }
}
