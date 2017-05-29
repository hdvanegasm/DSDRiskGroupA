package server.gamebuilder.model;

// TODO Add documentation

/**
 *
 * @author Admin
 */
public class SessionInvitation {
    public int id;
    public SessionInvitationState state;

    // TODO Add documentation
    public SessionInvitation(int id, SessionInvitationState state) {
        this.id = id;
        this.state = state;
    }
    
    // TODO Add documentation
    public SessionInvitation() {
        this.state = SessionInvitationState.UNANSWERED;
    }
    
    // TODO Add documentation
    public boolean answer(SessionInvitationState response) {
        if(response == SessionInvitationState.ACCEPTED) {
            this.state = SessionInvitationState.ACCEPTED;
            return true;
        }
        this.state = SessionInvitationState.NOT_ACCEPTED;
        return false;
    }
}
