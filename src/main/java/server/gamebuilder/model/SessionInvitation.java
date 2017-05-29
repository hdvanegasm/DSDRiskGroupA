package server.gamebuilder.model;

/**
 *
 * @author Admin
 */
public class SessionInvitation {
    public int id;
    public SessionInvitationState state;

    public SessionInvitation(SessionInvitationState state) {
        this.state = state;
    }
    
    public SessionInvitation(int id, SessionInvitationState state) {
        this.state = state;
        this.id = id;
    }
    
    public SessionInvitation() {
        this.state = SessionInvitationState.UNANSWERED;
    }
    
    public void answer(String response) {
        if(response.equals("accepted")) {
            this.state = SessionInvitationState.ACCEPTED;
        } else if(response.equals("not_accepted")) {
            this.state = SessionInvitationState.NOT_ACCEPTED;
        }
    }
}
