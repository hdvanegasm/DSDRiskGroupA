package server.gamebuilder.model;

/**
 * This class represents a session invitation that is sent by a host of a
 * session to his contact.
 *
 * @author Hernán Darío Vanegas Madrigal
 */
public class SessionInvitation {

    public int id;
    public SessionInvitationState state;

    /**
     * This method is the main constructor of the class, it receives an ID an
     * the state of the session.
     *
     * @param id This attribute represents the ID of the session which will
     * represents the invitation in the database and the system.
     * @param state This parameter represents the state of the session
     * invitation.
     */
    public SessionInvitation(int id, SessionInvitationState state) {
        this.id = id;
        this.state = state;
    }

    /**
     * This is the second version of the constructor of a session invitation
     * that establishes by default the value of the state to "unanswered".
     */
    public SessionInvitation() {
        this.state = SessionInvitationState.UNANSWERED;
    }

    /**
     * This method allows to the contact to answer a session invitation posted
     * by the host of a session.
     *
     * @param response This attribute represents the answer of the session
     * invitation
     * @return The method returns true if the session invitation was accepted,
     * otherwise it returns "false".
     */
    public boolean answer(SessionInvitationState response) {
        if (response == SessionInvitationState.ACCEPTED) {
            this.state = SessionInvitationState.ACCEPTED;
            return true;
        }
        this.state = SessionInvitationState.NOT_ACCEPTED;
        return false;
    }
}
