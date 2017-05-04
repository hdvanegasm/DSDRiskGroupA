/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accountmanager.model;

import java.util.LinkedList;

/**
 *
 * @author Hernan Dario Vanegas Madrigal
 */
public class Account {

    private AccountStatus status;
    private String username;
    private String password;
    private String email;
    private int numberOfSessionLost;
    private int numberOfSessionWon;
    private LinkedList<Contact> contactList;
    private LinkedList<SessionInvitation> sessionInvitations;
    private float percentajeOfWins;

    public Account(AccountStatus status, String username, String password, String email, int numberOfSessionLost, int numberOfSessionWon, float percentajeOfWins) {
        this.status = status;
        this.username = username;
        this.password = password;
        this.email = email;
        this.numberOfSessionLost = numberOfSessionLost;
        this.numberOfSessionWon = numberOfSessionWon;
        this.percentajeOfWins = percentajeOfWins;
        this.contactList = new LinkedList<>();
    }

    public static Account create(AccountStatus status, String username, String password, String email) {
        return new Account(status, username, password, email, 0, 0, 0);
    }

    public void login() {
        this.status = AccountStatus.ONLINE;

        // TODO Update status in database
    }

    public void changePassword(String newPassword, String confirmPasword) {
        if (newPassword.equals(confirmPasword)) {
            this.password = newPassword;
        }

        //TODO Update Password in database
    }

    public void logout() {
        this.status = AccountStatus.OFFLINE;

        // TODO Update status in database
    }
}
