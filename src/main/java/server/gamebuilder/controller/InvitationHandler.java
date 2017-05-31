package server.gamebuilder.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.DatabaseConnector;
import server.accountmanager.model.AccountStatus;
import server.accountmanager.model.Contact;
import server.gamebuilder.model.Host;
import server.gamebuilder.model.SessionInvitation;
import server.gamebuilder.model.SessionInvitationState;

/**
 * This class handles the invitations provided by host to his contacts. This
 * class acts as a controller that makes the connection with the database in
 * order to manage the invitations
 *
 * @author Hernán Darío Vanegas Madrigal
 */
public class InvitationHandler {

    /**
     * This method allows to invite a contact to a session by a host, it creates
     * a new session invitation and insert it into the database
     *
     * @param host It represents the host that creates the invitation
     * @param contact It represents the contact of the host which will receive
     * the invitation
     * @return The method returns true if the session invitation was sent
     * successfully, otherwise it returns false.
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the statement reference.
     */
    public static boolean inviteContact(Host host, Contact contact) throws SQLException, ClassNotFoundException {
        String onlineContactQuery = "SELECT status FROM account WHERE username='" + contact.account.username + "';";
        ResultSet result;

        result = DatabaseConnector.getInstance().getStatement().executeQuery(onlineContactQuery);

        if (result.next()) {
            String status = result.getString("status");
            if (!status.equals(AccountStatus.ONLINE.toString())) {
                return false;
            }
        } else {
            return false;
        }

        // Get new ID
        String querySelectId = "SELECT MAX(id) AS id FROM invitation;";

        result = DatabaseConnector.getInstance().getStatement().executeQuery(querySelectId);

        int newId = 1;

        if (result.next()) {
            newId = result.getInt("id") + 1;
        }

        // Create session invitation objetct
        SessionInvitation invitation = contact.invite(newId);
        String insertInvitationQuery = "INSERT INTO invitation(id, host, contact_username, state) VALUES(" + invitation.id + ", '" + host.account.username + "', '" + contact.account.username + "', '" + invitation.state + "');";

        DatabaseConnector.getInstance().getStatement().executeUpdate(insertInvitationQuery);

        return true;
    }

    /**
     * This method is implemented in order to allow to a user to answer an
     * invitation. It only changes the answer of the invitation to the database
     * and changes his status according to his response.
     *
     * @param invitation This object represents the invitation that will be
     * answered
     * @param response This is an enumeration value that represents the response
     * of the contact that answers the session invitation
     * @return The method returns true if the invitation was accepted, otherwise
     * it returns false.
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the statement reference.
     */
    public static boolean answerInvitation(SessionInvitation invitation, SessionInvitationState response) throws ClassNotFoundException, SQLException {
        boolean result = invitation.answer(response);

        String updateInvitationStateQuery = "UPDATE invitation SET state=? WHERE id=?";

        PreparedStatement update = DatabaseConnector.getInstance().getConnection().prepareStatement(updateInvitationStateQuery);
        update.setString(1, invitation.state.toString());
        update.setInt(2, invitation.id);
        update.executeUpdate();

        return result;
    }
}
