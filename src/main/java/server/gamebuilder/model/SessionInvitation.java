package server.gamebuilder.model;

// TODO Add documentation

/**
 *
 * @author Admin
 */
public class SessionInvitation {
    public SessionInvitationState state;

    // TODO Add documentation
    public SessionInvitation(SessionInvitationState state) {
        this.state = state;
    }
    
    // TODO Add documentation
    public SessionInvitation() {
        this.state = SessionInvitationState.UNANSWERED;
    }
    
    // TODO Add documentation
    public void answer(String response) {
        if(response.equals("accepted")) {
            this.state = SessionInvitationState.ACCEPTED;
        } else if(response.equals("not_accepted")) {
            this.state = SessionInvitationState.NOT_ACCEPTED;
        }
    }
}
