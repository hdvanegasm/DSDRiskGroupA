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
 * This class handles the invitations provided by host to his contacts. This class acts as a controller that makes the connection with the database in order to manage the invitations
 * @author Hernán Darío Vanegas Madrigal
 */
public class InvitationHandler {
    // TODO add documentation
    public static boolean inviteContact(Host host, Contact contact){
        
        
        // Verify if the user is online
        String onlineContactQuery = "SELECT status FROM account WHERE username='" + contact.account.username + "';";
        ResultSet result;
        try {
            result = DatabaseConnector.getInstance().getStatement().executeQuery(onlineContactQuery);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        try {
            if(result.next()) {
                String status = result.getString("status");
                if(!status.equals(AccountStatus.ONLINE.toString())) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // Get new ID
        String querySelectId = "SELECT MAX(id) AS id FROM invitation;";
        try {
            result = DatabaseConnector.getInstance().getStatement().executeQuery(querySelectId);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        int newId = 1;
        try {
            if(result.next()) {
                newId = result.getInt("id") + 1; 
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // Create session invitation objetct
        SessionInvitation invitation = contact.invite(newId);
        String insertInvitationQuery = "INSERT INTO invitation(id, host, contact_username, state) VALUES("+ invitation.id +", '" + host.account.username +"', '" + contact.account.username + "', '" + invitation.state + "');";
        try {
            DatabaseConnector.getInstance().getStatement().executeUpdate(insertInvitationQuery);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }

    // TODO add documentation
    public static boolean answerInvitation(SessionInvitation invitation, SessionInvitationState response) {
        boolean result = invitation.answer(response);
        
        String updateInvitationStateQuery = "UPDATE invitation SET state=? WHERE id=?";
        try {
            PreparedStatement update = DatabaseConnector.getInstance().getConnection().prepareStatement(updateInvitationStateQuery);
            update.setString(1, invitation.state.toString());
            update.setInt(2, invitation.id);
            update.executeUpdate();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return result;
    }
}
