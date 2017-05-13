package server.accountmanager.model;

/**
 * This enumeration class represents the states of a session. There are three possible states: playing (if the session was created successfully and it was started), finished (if the session was finished) and creating (if the session is in the creating process but not started).
 * @author Hernán Darío Vanegas Madrigal
 */
public enum SessionState {
    PLAYING,
    FINISHED,
    CREATING
}
