package server.accountmanager.model;

/**
 * This enumeration represents the possible states of the request. This states can change with the answer() method in the Request class.
 * @author Hernán Darío Vanegas Madrigal.
 */
enum RequestState {
    ACCEPTED,
    NOT_ACCEPTED,
    UNANSWERED
}
