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
import server.gamebuilder.controller.SessionBuilder;
import server.gamebuilder.model.Host;
import server.gamebuilder.model.Map;
import server.gamebuilder.model.Player;
import server.gamebuilder.model.Request;
import server.gamebuilder.model.RequestState;
import server.gamebuilder.model.Session;
import server.gamebuilder.model.SessionState;
import server.gamebuilder.model.SessionType;
import server.accountmanager.model.Contact;
import server.gamebuilder.controller.InvitationHandler;
import server.gamebuilder.model.SessionInvitation;
import server.gamebuilder.model.SessionInvitationState;

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
        
        return RequestHandler.makeRequest(userRequest, session, user);
    }
    
    public static boolean answerRequestTest() {
        Session session = Session.create(1);
        Request request = new Request(1, RequestState.UNANSWERED);
        boolean response;
        try {
            response = RequestHandler.answerRequest(request, RequestState.ACCEPTED);
        } catch (ClassNotFoundException | SQLException ex) {
            response = false;
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
    
    public static Player joinSessionTest() {
        Map map = new Map("Prado Centro");
        Session session = Session.create(4, SessionType.WORLD_DOMINATION_RISK, SessionState.CREATING, map);
        User userCreator = new User(Account.create(AccountStatus.ONLINE, "hernan", "1234", "hdvanegasm@unal.edu.co"));
        
        SessionBuilder.createSession(userCreator, session);
        
        User userJoin = new User(Account.create(AccountStatus.ONLINE, "spinos", "1234", "s@unal"));
        Player newPlayer;
        try {
            newPlayer = SessionBuilder.joinSession(userJoin, session);
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return newPlayer;
    }
    
    public static boolean inviteContactTest() {
        Host host = new Host(Account.create(AccountStatus.ONLINE, "hernan", "1234", "hdvanegasm@unal.edu.co"));
        Contact contact = new Contact(Account.create(AccountStatus.ONLINE, "spinos", "1234", "s@unal"));
        return InvitationHandler.inviteContact(host, contact);
    }    
    
    public static boolean answerInvitationTest() {
        SessionInvitation invitation = new SessionInvitation(1, SessionInvitationState.UNANSWERED);
        SessionInvitationState response = SessionInvitationState.ACCEPTED;
        return InvitationHandler.answerInvitation(invitation, response);
    }

    public static void main(String[] args) {
        System.out.println(Contact.class.getSimpleName());
    }
}
