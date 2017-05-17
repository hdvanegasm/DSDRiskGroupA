
package server.accountmanager.model;

import server.gamebuilder.model.SessionInvitation;
import java.util.LinkedList;

/**
 * This class is the model of a basic account. It has the basic attributes of the account, and his basic methods.
 * @author Hernan Dario Vanegas Madrigal
 */
public class Account {

    public AccountStatus status;
    public String username;
    public String password;
    public String email;
    public int numberOfSessionLost;
    public int numberOfSessionWon;
    public LinkedList<Contact> contactList;
    public LinkedList<SessionInvitation> sessionInvitations;
    public float percentajeOfWins;

    /**
     * This method is the constructor of Account class. This method is used by the static method create()
     * @param status It is the status of the account, it represents one of three states of the user in the system: online, offline and playing.
     * @param username It is the identifier of the user in the system. The username is unique in all the system.
     * @param password It is the password of the account that is used in the login phase, it is associated to an username.
     * @param email This attribute stores the e-mail of the user in order to add other features to the system.
     * @param numberOfSessionLost It represents the number of sessions lost by the user.
     * @param numberOfSessionWon It represents the number of session won by the user.
     * @param percentajeOfWins It represents the percentage of wins. This value can be calculated based on the number of session won and the number of session lost.
     */
    private Account(AccountStatus status, String username, String password, String email, int numberOfSessionLost, int numberOfSessionWon, float percentajeOfWins) {
        this.status = status;
        this.username = username;
        this.password = password;
        this.email = email;
        this.numberOfSessionLost = numberOfSessionLost;
        this.numberOfSessionWon = numberOfSessionWon;
        this.percentajeOfWins = percentajeOfWins;
        this.contactList = new LinkedList<>();
    }

    /**
     * This method is an static method that creates a User and return a reference to the created object. This is the only method to create a new User.
     * @param status It represents the status of the user and it takes a value from three possible values: online, offline and playing.
     * @param username It is an unique identifier of the user in the system. 
     * @param password This is the password related to the username.
     * @param email This is the e-mail of the account. This is stored in order to use the e-mail communication channel to confirm account issues.
     * @return This method returns the reference to the new object that has the attributes taken from the parameters.
     */
    public static Account create(AccountStatus status, String username, String password, String email) {
        return new Account(status, username, password, email, 0, 0, 0);
    }

    /**
     * This method changes the status of a user to "Online". It happens when the user logs in into the system.
     */
    public void login() {
        this.status = AccountStatus.ONLINE;
    }

    /**
     * This method allows the change of the password as a basic functionality of the account management. The password is changed if and only if the confirmation process is successful.
     * @param newPassword This is the new password of the account.
     * @param confirmPasword This is a confirmation password of the new password. It is in order to reduce the error in the password changing. The password changes only when the confirmation and the new password are the same. 
     * @return It returns true if the password was successfully changed, otherwise it returns false
     */
    public boolean changePassword(String newPassword, String confirmPasword) {
        if (newPassword.equals(confirmPasword)) {
            this.password = newPassword;
            return true;
        }
        return false;
    }

    /**
     * This method changes the status of the user to "Offline" in order to log out this user from the system.
     */
    public void logout() {
        this.status = AccountStatus.OFFLINE;
    }
}
