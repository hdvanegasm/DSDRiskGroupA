package server.accountmanager.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    /**
     * This method manages the connection with the database in order to add new
     * contacts and relate it with his corresponding user in the database. The
     * method assigns the new id to the contact in the contact table and makes
     * sure to add the contact to contact table if and only if the contact does
     * not exist.
     *
     * @param user This is the user that will add the contact to his contact
     * list
     * @param newContact This attribute represents the new contact that will be
     * added to the user's contact list.
     * @return The method returns a boolean value; it returns "true" if the
     * contact has been added successfully, otherwise it returns "false"
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the prepared statement reference.
     */
    public static boolean addContact(User user, User newContact) throws ClassNotFoundException, SQLException {
        Contact contact = new Contact(newContact.account);

        if(!contact.add(user)) {
            return false;
        }

        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        // Check if the contact is in the database
        String checkContact = "SELECT COUNT(username) AS occurrences FROM contact WHERE username=?";

        preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(checkContact);
        preparedStatement.setString(1, newContact.account.username);
        result = preparedStatement.executeQuery();
        result.next();

        if (result.getInt("occurrences") == 0) {

            // Insert the contact in the DB
            String insertContactQuery = "INSERT INTO contact VALUES(?)";

            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(insertContactQuery);
            // Get the last contact of the list, i.e. the new contact
            preparedStatement.setString(1, user.account.contactList.getLast().account.username);
            preparedStatement.executeUpdate();

            // Update class state in user table
            String updateUserType = "UPDATE user SET typeOfUser=? WHERE username=?";

            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(updateUserType);
            preparedStatement.setString(1, Contact.class.getSimpleName().toUpperCase());
            preparedStatement.setString(2, user.account.contactList.getLast().account.username);
            preparedStatement.executeUpdate();

        }

        // Add both user and contact to contact list
        String insertContactListQuery = "INSERT INTO contactlist VALUES(?, ?)";

        preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(insertContactListQuery);
        preparedStatement.setString(1, user.account.username);
        preparedStatement.setString(2, user.account.contactList.getLast().account.username);
        preparedStatement.executeUpdate();

        return true;
    }

    /**
     * This method allows to the server to remove a contact from a contact list.
     * The main function of this method is delete the contact but if the contact
     * is in the contact list of another user, then the contact won't be removed
     * from the contact table. Also, the method updates all of the attributes in
     * the user table and account table in the database.
     *
     * @param user This is the user that wants to remove the contact from his
     * contact list.
     * @param contact This reference represents the contact that will be deleted
     * from the contact list of the user in the parameters.
     * @return The method returns "true" if the contact was removed
     * successfully, otherwise it returns "false".
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the prepared statement reference.
     */
    public static boolean removeContact(User user, Contact contact) throws SQLException, ClassNotFoundException {
        //SELECT COUNT(contact_username) AS number FROM contactlist WHERE contact_username='spinos';

        contact.remove(user);

        PreparedStatement preparedStatement = null;

        // Remove the pair (user, contact) from database
        String deleteFromContactListQuery = "DELETE FROM contactlist WHERE user_username=? AND contact_username=?";

        preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(deleteFromContactListQuery);
        preparedStatement.setString(1, user.account.username);
        preparedStatement.setString(2, contact.account.username);
        preparedStatement.executeUpdate();

        // Now we check if the user contact of someone else
        ResultSet result = null;
        String queryCheck = "SELECT COUNT(contact_username) AS number FROM contactlist WHERE contact_username=?";

        preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(queryCheck);
        preparedStatement.setString(1, contact.account.username);
        result = preparedStatement.executeQuery();

        int numberOfOccurrences = 0;

        result.next();
        numberOfOccurrences = result.getInt("number");

        // If the number of occurrences is cero then we must change the state of the user
        if (numberOfOccurrences == 0) {
            String deleteFromContactQuery = "DELETE FROM contact WHERE username=?";

            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(deleteFromContactQuery);
            preparedStatement.setString(1, contact.account.username);
            preparedStatement.executeUpdate();

            String updateUserType = "UPDATE user SET typeOfUser=NULL WHERE username=?";

            preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(updateUserType);
            preparedStatement.setString(1, contact.account.username);
            preparedStatement.executeUpdate();

        }

        return true;

    }
}
