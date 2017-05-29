package server.accountmanager.model;

import java.util.Iterator;
import server.gamebuilder.model.SessionInvitation;
import server.gamebuilder.model.SessionInvitationState;

/**
 * This class represents a contact of an user. A contact is an User itself. This class is created in order to make a differentiation between types.
 * @author Hernán Dario Vanegas Madrigal
 */
public class Contact extends User{
    
    // TODO add documentation
    public Contact(Account account) {
        super(account);
    }
    
    // TODO add documentation
    public void remove(User user) {
        Iterator<Contact> iterator = user.account.contactList.listIterator();
        while(iterator.hasNext()) {
            Contact actualContact = iterator.next();
            if(actualContact.account.username.equals(this.account.username)) {
                iterator.remove();
            }
        }
    }
    
    // TODO add documentation
    public SessionInvitation invite(int id) {
        SessionInvitation invitation = new SessionInvitation(id, SessionInvitationState.UNANSWERED);
        this.account.sessionInvitations.add(invitation);
        return invitation;
    }
    
    // TODO Review this with the requirements
    public void add(User user) {
        user.account.contactList.add(this);
    }
}
