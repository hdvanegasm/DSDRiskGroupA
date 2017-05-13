
package server.accountmanager.model;

/**
 * This enumeration class represents the status of the account. There are three types of states: online (after the login of the user), offline(after the logout of the user) and playing (when the user is online and he is playing in a session).
 * @author Hernán Dario Vanegas Madrigal
 */
public enum AccountStatus {
    ONLINE,
    OFFLINE,
    PLAYING
}
