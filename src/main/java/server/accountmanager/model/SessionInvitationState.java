package server.accountmanager.model;

/**
 * This class represents the state of the session invitation. There are three possible states: accepted (when the invitation was accepted by the user), not accepted (if the invitation was not accepted) and unanswered (if the session was sent but it has not a response yet).
 * @author Hernán Darío Vanegas Madrigal
 */
enum SessionInvitationState {
    ACCEPTED,
    NOT_ACCEPTED,
    UNANSWERED
}
