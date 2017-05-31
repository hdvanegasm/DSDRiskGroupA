/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.accountmanager.controller.ContactManager;
import server.accountmanager.model.Account;
import server.accountmanager.model.AccountStatus;
import server.accountmanager.model.User;
import server.gamebuilder.controller.RequestHandler;
import server.gamebuilder.controller.SessionManager;
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
        try {
            String username = "hernan";
            int idSession = 1;

            Session session = Session.create(idSession);
            User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));

            return RequestHandler.makeRequest(session, user);
        } catch (SQLException | ClassNotFoundException ex) {
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
        } catch (ClassNotFoundException | SQLException ex) {
            response = false;
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    public static Player joinSessionTest() {
        Map map = new Map("Prado Centro");
        Session session = Session.create(4, SessionType.WORLD_DOMINATION_RISK, SessionState.CREATING, map);

        User userJoin = new User(Account.create(AccountStatus.ONLINE, "spinos", "1234", "s@unal"));
        Player newPlayer;
        try {
            newPlayer = SessionManager.joinToSession(userJoin, session);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return newPlayer;
    }

    public static boolean inviteContactTest() {
        Host host = new Host(Account.create(AccountStatus.ONLINE, "hernan", "1234", "hdvanegasm@unal.edu.co"), null);
        Contact contact = new Contact(Account.create(AccountStatus.ONLINE, "spinos", "1234", "s@unal"));
        try {
            return InvitationHandler.inviteContact(host, contact);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean answerInvitationTest() throws ClassNotFoundException, SQLException {
        SessionInvitation invitation = new SessionInvitation(1, SessionInvitationState.UNANSWERED);
        SessionInvitationState response = SessionInvitationState.ACCEPTED;
        return InvitationHandler.answerInvitation(invitation, response);
    }

    // TODO implement test
    public static boolean addContactTest() {
        try {
            User user = new User(Account.create(AccountStatus.ONLINE, "hernan", "1234", "hdvanegasm@unal.edu.co"));
            User newContact = new User(Account.create(AccountStatus.ONLINE, "spinos", "1234", "spinos@unal"));
            try {
                ContactManager.addContact(user, newContact);
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }

            user = new User(Account.create(AccountStatus.ONLINE, "edalpin", "1234", "edalpin@unal.edu.co"));
            return ContactManager.addContact(user, newContact);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // TODO implement test
    public static boolean removeContactTest() {
        User user = new User(Account.create(AccountStatus.ONLINE, "hernan", "1234", "hdvanegasm@unal.edu.co"));
        Contact contact = new Contact(Account.create(AccountStatus.ONLINE, "spinos", "1234", "spinos@unal"));
        try {
            return ContactManager.removeContact(user, contact);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static void main(String[] args) {
        removeContactTest();
    }
}
