package server.accountmanager.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.DatabaseConnector;
import server.accountmanager.model.Contact;
import server.accountmanager.model.User;

/**
 * This class has all the basic functions that supports the management of
 * contacts.
 *
 * @author Hernán Darío Vanegas Madrigal
 */
public class ContactManager {

    // TODO Add documentation
    public boolean addContact(User user, Contact contact) {
        contact.add(user);

        PreparedStatement preparedStatement = null;

        // Insert the contact in the DB
        String insertContactQuery = "INSERT INTO contact VALUES(?)";
        try {
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(insertContactQuery);
            preparedStatement.setString(1, user.account.contactList.getLast().account.username);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ContactManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        // Update class state in user table
        String updateUserType = "UPDATE user SET typeOfUser=? WHERE username=?";
        try {
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(updateUserType);
            preparedStatement.setString(1, Contact.class.getSimpleName().toUpperCase());
            preparedStatement.setString(2, user.account.contactList.getLast().account.username);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ContactManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        // Add both user and contact to contact list
        String insertContactListQuery = "INSERT INTO contactlist VALUES(?, ?)";
        try {
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(insertContactListQuery);
            preparedStatement.setString(1, user.account.username);
            preparedStatement.setString(2, user.account.contactList.getLast().account.username);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ContactManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    // TODO Add documentation
    public boolean removeContact(User user, Contact contact) {
        //SELECT COUNT(contact_username) AS number FROM contactlist WHERE contact_username='spinos';

        contact.remove(user);

        PreparedStatement preparedStatement = null;

        // Remove the pair (user, contact) from database
        String deleteFromContactListQuery = "DELETE FROM contactlist WHERE user_username=? AND contact_username=?";
        try {
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(deleteFromContactListQuery);
            preparedStatement.setString(1, user.account.username);
            preparedStatement.setString(2, contact.account.username);
            preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ContactManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        // Now we check if the user contact of someone else
        ResultSet result = null;
        String queryCheck = "SELECT COUNT(contact_username) AS number FROM contactlist WHERE contact_username=?";
        try {
            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(queryCheck);
            preparedStatement.setString(1, contact.account.username);
            result = preparedStatement.executeQuery();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ContactManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        int numberOfOccurrences = 0;

        try {
            result.next();
            numberOfOccurrences = result.getInt("number");
        } catch (SQLException ex) {
            Logger.getLogger(ContactManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        // If the number of occurrences is cero then we must change the state of the user
        if (numberOfOccurrences == 0) {
            String deleteFromContactQuery = "DELETE FROM contact WHERE username=?";
            try {
                preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(deleteFromContactQuery);
                preparedStatement.setString(1, contact.account.username);
                preparedStatement.executeUpdate();
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(ContactManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            String updateUserType = "UPDATE user SET typeOfUser=NULL WHERE username=?";
            try {
                preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(updateUserType);
                preparedStatement.setString(1, contact.account.username);
                preparedStatement.executeUpdate();
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(ContactManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        
        return true;
    
    }
}
