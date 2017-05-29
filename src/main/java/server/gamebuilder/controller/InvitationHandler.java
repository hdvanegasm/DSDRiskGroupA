package server.gamebuilder.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.DatabaseConnector;
import server.accountmanager.model.AccountStatus;
import server.accountmanager.model.Contact;
import server.gamebuilder.model.Host;
import server.gamebuilder.model.SessionInvitation;

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
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        try {
            if(result.next()) {
                String status = result.getString("status");
                if(!status.equals(AccountStatus.ONLINE)) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        // Create session invitation objetct
        SessionInvitation invitation = contact.invite();
        
        String insertInvitationQuery = "INSERT INTO invitation VALUES('" + host.account.username +"', '" + contact.account.username + "', '" + invitation.state + "';";
        try {
            DatabaseConnector.getInstance().getStatement().executeQuery(insertInvitationQuery);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(InvitationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }

    // TODO add documentation
    public static boolean answerInvitation() {
        return true;
    }
}
