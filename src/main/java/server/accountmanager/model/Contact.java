package server.accountmanager.model;

import java.util.Iterator;
import server.gamebuilder.model.SessionInvitation;
import server.gamebuilder.model.SessionInvitationState;

/**
 * This class represents a contact of an user. A contact is an User itself. This
 * class is created in order to make a differentiation between types.
 *
 * @author Hernán Dario Vanegas Madrigal
 */
public class Contact extends User {

    /**
     * This is the constructor of the class contact. It allows us to create a
     * contact given an account.
     *
     * @param account This is the account that will be associated with the new
     * contact.
     */
    public Contact(Account account) {
        super(account);
    }

    /**
     * This method allows to remove a contact from a contact list of a given
     * user. It iterates in the list until he finds the contact in the list and
     * removes it from the list.
     *
     * @param user This is the user that provides the list in which the contact
     * will be removed.
     */
    public void remove(User user) {
        Iterator<Contact> iterator = user.account.contactList.listIterator();
        while (iterator.hasNext()) {
            Contact actualContact = iterator.next();
            if (actualContact.account.username.equals(this.account.username)) {
                iterator.remove();
            }
        }
    }

    /**
     * This method allows to a user to send an invitation to a contact in order
     * to join him to a session. The method creates a new invitation and adds it
     * to the invitation list of the contact.
     *
     * @param id The method receives the id of the new invitation.
     * @return The method returns the new invitation added to the contact list.
     */
    public SessionInvitation invite(int id) {
        SessionInvitation invitation = new SessionInvitation(id, SessionInvitationState.UNANSWERED);
        this.account.sessionInvitations.add(invitation);
        return invitation;
    }

    /**
     * The method allows to a user to add this contact to the contact list if
     * and only if the contact does not exists in the list at that moments
     *
     * @param user This parameter represents the user that will add to this
     * contact to his contact list.
     * @return The method returns "true" if the contact was added successfully
     * to the contact list, i.e. the contact was not in the contact list,
     * otherwise it returns "false"
     */
    public boolean add(User user) {
        if (!user.account.contactList.contains(this)) {
            user.account.contactList.add(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Contact{ "+ super.toString() + " }";
    }
    
    
}
