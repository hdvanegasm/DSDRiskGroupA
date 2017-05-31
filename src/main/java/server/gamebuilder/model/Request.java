package server.gamebuilder.model;

import server.accountmanager.model.User;

/**
 * This class represents a request from a user to a host of a session in order
 * to include the user into the session construction phase.
 *
 * @author Hernán Darío Vanegas Madrigal
 */
public class Request {

    public int id;
    public RequestState state;
    public User senderUser;
    
    /**
     * This is the constructor of the class that determines the initial state of
     * the session.
     *
     * @param state This represents the actual state of the request. The
     * possible values of this attribute are: accepted, not accepted and
     * unanswered.
     */
    public Request(RequestState state, User sender) {
        this.senderUser = sender;
        this.state = state;
    }
    
    // TODO add documentation
    public Request(int id, RequestState state) {
        this.id = id;
        this.state = state;
    }

    /**
     * This method sends the request from the user to the specified session.
     * After the execution of this method, a host can answer the request.
     * @param session This parameter represents the session that will receive the request sent by the user.
     * @return The method returns the request made from the user.
     */
    public static Request make(Session session, User sender) {
        Request request = new Request(RequestState.UNANSWERED, sender);
        session.requests.add(request);
        return request;
    }

    /**
     * This method allows to the host user to answer the request in order to
     * include the user in the session construction phase.
     *
     * @param answer This attribute represents the answer of the request provided by the user
     * @return The method returns true if the host accepts the user in the
     * session, otherwise it returns false.
     */
    public boolean answer(RequestState answer) {
        this.state = answer;
        return answer == RequestState.ACCEPTED;
    }

    @Override
    public String toString() {
        return "Request{" + "id=" + id + ", state=" + state + '}';
    }
    
}
