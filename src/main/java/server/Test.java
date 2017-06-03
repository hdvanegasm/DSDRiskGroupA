/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.accountmanager.controller.AccountManager;
import server.accountmanager.controller.ContactManager;
import server.accountmanager.model.Account;
import server.accountmanager.model.AccountStatus;
import server.gamebuilder.controller.RequestHandler;
import server.gamebuilder.controller.SessionManager;
import server.gamebuilder.model.Map;
import server.gamebuilder.model.Player;
import server.gamebuilder.model.Session;
import server.gamebuilder.model.SessionState;
import server.gamebuilder.controller.InvitationHandler;
import server.gamebuilder.model.Color;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Admin
 */
public class Test {

    public static String createAccountTest(String json) throws ParseException {
        return AccountManager.createAccount(json);
    }

    public static String logInTest(String json) throws ParseException {
        return AccountManager.logIn(json);
    }

    public static String logOutTest(String json) throws ParseException {
        return AccountManager.logOut(json);
    }

    public static String changePasswordTest(String json) throws ParseException {
        return AccountManager.changePassword(json);
    }

    public static String createSessionTest(String json) throws ParseException {
        return SessionManager.createSession(json);
    }

    public static String makeRequestTest(String json) throws ParseException {
        return RequestHandler.makeRequest(json);
    }

    public static String answerRequestTest(String json) throws ParseException {
        return RequestHandler.answerRequest(json);
    }

    public static String joinSessionTest(String json) throws ParseException {
        return SessionManager.joinToSession(json);
    }

    public static String inviteContactTest(String json) throws ParseException {
        return InvitationHandler.inviteContact(json);
    }

    public static String answerInvitationTest(String json) throws ParseException {
        return InvitationHandler.answerInvitation(json);
    }

    // TODO implement test
    public static String addContactTest(String json) throws ParseException {
        return ContactManager.addContact(json);
    }

    public static String removeContactTest(String json) throws ParseException {
        return ContactManager.removeContact(json);
    }

    public static String getAllCreatingSessionsTest() {
        return SessionManager.getAllCreatingSession();
    }

    public static String getPlayersFromSessionTest(String json) throws ParseException {
        return SessionManager.getPlayersFromSession(json);
    }

    public static String getContactsFromUser(String json) throws ParseException {
        return ContactManager.getContactsFromUser(json);
    }

    public static String takeOutPlayerFromSessionTest(String json) throws ParseException {
        return SessionManager.takeOutPlayerFromSession(json);
    }

    public static String playerLeavesSessionTest(String json) throws ParseException {
        return SessionManager.leaveSession(json);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
        File fileJson = new File("jsonTest.json");
        BufferedReader readJson = new BufferedReader(new FileReader("jsonTest.json"));
        String line = null;
        StringBuilder jsonTest = new StringBuilder();
        while ((line = readJson.readLine()) != null) {
            jsonTest.append(line);
        }
        System.out.println("#####################################################");
        System.out.println("# ACCOUNT MANAGER AND GAME BUILDER INTEGRATION TEST #");
        System.out.println("#####################################################");
        StringBuilder menu = new StringBuilder();
        menu.append("code: 1 - createAccount\n");
        menu.append("code: 2 - logOut\n");
        menu.append("code: 3 - logIn\n");
        menu.append("code: 4 - changePassword\n");
        menu.append("code: 5 - createSession\n");
        menu.append("code: 6 - makeRequest\n");
        menu.append("code: 7 - answerRequest\n");
        menu.append("code: 8 - joinSession\n");
        menu.append("code: 9 - inviteContact\n");
        menu.append("code: 10 - answerInvitation\n");
        menu.append("code: 11 - addContact\n");
        menu.append("code: 12 - removeContact\n");
        menu.append("code: 13 - getAllCreatingSessions\n");
        menu.append("code: 14 - getPlayersFromSession\n");
        menu.append("code: 15 - getContactsFromUser\n");
        menu.append("code: 16 - takeOutPlayerFromSession\n");
        menu.append("code: 17 - playerLeavesSession\n");
        menu.append("Insert test code: ");
        System.out.print(menu);

        Scanner entrada = new Scanner(System.in);
        int method = entrada.nextInt();

        System.out.println("============================================= JSON RESULT ==============================================");
        switch (method) {
            case 1:
                System.out.println(createAccountTest(jsonTest.toString()));
                break;
            case 2:
                System.out.println(logOutTest(jsonTest.toString()));
                break;
            case 3:
                System.out.println(logInTest(jsonTest.toString()));
                break;
            case 4:
                System.out.println(changePasswordTest(jsonTest.toString()));
                break;
            case 5:
                System.out.println(createSessionTest(jsonTest.toString()));
                break;
            case 6:
                System.out.println(makeRequestTest(jsonTest.toString()));
                break;
            case 7:
                System.out.println(answerRequestTest(jsonTest.toString()));
                break;
            case 8:
                System.out.println(joinSessionTest(jsonTest.toString()));
                break;
            case 9:
                System.out.println(inviteContactTest(jsonTest.toString()));
                break;
            case 10:
                System.out.println(answerInvitationTest(jsonTest.toString()));
                break;
            case 11:
                System.out.println(addContactTest(jsonTest.toString()));
                break;
            case 12:
                System.out.println(removeContactTest(jsonTest.toString()));
                break;
            case 13:
                System.out.println(getAllCreatingSessionsTest());
                break;
            case 14:
                System.out.println(getPlayersFromSessionTest(jsonTest.toString()));
                break;
            case 15:
                System.out.println(getContactsFromUser(jsonTest.toString()));
                break;
            case 16:
                System.out.println(takeOutPlayerFromSessionTest(jsonTest.toString()));
                break;
            case 17:
                System.out.println(playerLeavesSessionTest(jsonTest.toString()));
                break;
        }
    }
}
