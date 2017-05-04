/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accountmanager.model;

/**
 *
 * @author Admin
 */
public class SessionInvitation {
    private SessionInvitationState state;

    public SessionInvitation(SessionInvitationState state) {
        this.state = state;
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
        
        //TODO Update to database
    }
}
