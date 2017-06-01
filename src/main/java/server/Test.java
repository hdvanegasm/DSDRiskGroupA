/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.accountmanager.controller.AccountManager;
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
import server.gamebuilder.model.Color;
import server.gamebuilder.model.SessionInvitation;
import server.gamebuilder.model.SessionInvitationState;

/**
 *
 * @author Admin
 */
public class Test {

    public static boolean createAccountTest() {
        String username = "pedro";
        String password = "1234";
        String confirmPass = "1234";
        String email = "pedro@unal.edu.co";

        if (confirmPass.equals(password)) {
            Account newAccount = Account.create(AccountStatus.OFFLINE, username, password, email);
            User user = new User(newAccount);
            try {
                return AccountManager.createAccount(user);
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return false;
        }

    }

    public static boolean logInTest() {

        String username = "pedro";
        String password = "1234";

        User user = new User(Account.create(AccountStatus.OFFLINE, username, password, null));

        try {
            return AccountManager.logIn(user);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean logOutTest() {
        String username = "pedro";

        User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));

        try {
            return AccountManager.logOut(user);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean createSessionTest() {
        String hostUsername = "pedro";

        Account account = Account.create(AccountStatus.ONLINE, hostUsername, null, null);
        User hostUser = new User(account);

        try {
            return SessionManager.createSession(hostUser);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean changePasswordTest() {
        String username = "hernan";
        String password = "1234";
        String newPassword = "12345";

        User user = new User(Account.create(AccountStatus.ONLINE, username, password, null));

        try {
            return AccountManager.changePassword(user, newPassword);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean makeRequestTest() {
        try {
            String username = "edalpin";
            int idSession = 2;

            Session session = Session.create(idSession);
            User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));

            return RequestHandler.makeRequest(session, user);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean answerRequestTest() {
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
        Session session = Session.create(2, 2, SessionType.WORLD_DOMINATION_RISK, SessionState.CREATING, map);

        User userJoin = new User(Account.create(AccountStatus.ONLINE, "edalpin", "1234", "edalpin@unal.edu.co"));
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

            User newContact = new User(Account.create(AccountStatus.ONLINE, "spinos", "1234", "spinos@unal"));

            User user = new User(Account.create(AccountStatus.ONLINE, "edalpin", "1234", "edalpin@unal.edu.co"));
            return ContactManager.addContact(user, newContact);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean removeContactTest() {
        User user = new User(Account.create(AccountStatus.ONLINE, "edalpin", "1234", "edalpin@unal.edu.co"));
        Contact contact = new Contact(Account.create(AccountStatus.ONLINE, "spinos", "1234", "spinos@unal"));
        try {
            return ContactManager.removeContact(user, contact);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static LinkedList<Session> getAllCreatingSessionsTest() {
        try {
            return SessionManager.getAllCreatingSessions();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static LinkedList<Player> getPlayersFromSession() {
        try {
            return SessionManager.getPlayersFromSession(1);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static LinkedList<Contact> getContactsFromUser() {
        try {
            return ContactManager.getContactsFromUser("edalpin");
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static boolean takeOutPlayerFromSessionTest() {
        Session session = Session.create(1, 4, null, SessionState.CREATING, new Map("Prado Centro"));
        Player player = new Player(Account.create(AccountStatus.ONLINE, "spinos", null, "spinos@unal.edu.co"), Color.RED);
        try {
            return SessionManager.takeOutPlayerFromSession(session, player);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static boolean playerLeavesSessionTest() {
        Session session = Session.create(1, 4, null, SessionState.CREATING, new Map("Prado Centro"));
        Player player = new Player(Account.create(AccountStatus.ONLINE, "spinos", null, "spinos@unal.edu.co"), Color.RED);
        try {
            return SessionManager.leaveSession(session, player);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        joinSessionTest();
    }
}
