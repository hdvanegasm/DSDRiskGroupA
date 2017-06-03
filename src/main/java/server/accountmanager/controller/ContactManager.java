package server.accountmanager.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.DatabaseConnector;
import server.accountmanager.model.Account;
import server.accountmanager.model.AccountStatus;
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
     * @param json
     * @return The method returns a boolean value; it returns "true" if the
     * contact has been added successfully, otherwise it returns "false"
     * @throws org.json.simple.parser.ParseException
     */
    public static String addContact(String json) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            String contactUsername = (String) parsedObject.get("contactUsername");

            User newContact = new User(Account.create(AccountStatus.ONLINE, contactUsername, null, null));
            User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));

            Contact contact = new Contact(newContact.account);

            if (!contact.add(user)) {
                JSONObject returnJson = new JSONObject();
                returnJson.put("status", false);
                returnJson.put("message", "Contact cannot be added");
                return returnJson.toJSONString();
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

            JSONObject returnJson = new JSONObject();
            returnJson.put("status", true);
            returnJson.put("message", "Contact added successfully");
            return returnJson.toJSONString();

        } catch (SQLException | ClassNotFoundException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
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
    public static String removeContact(String json) throws ParseException {
        try {
            //SELECT COUNT(contact_username) AS number FROM contactlist WHERE contact_username='spinos';

            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            String contactUsername = (String) parsedObject.get("contactUsername");

            User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));
            Contact contact = new Contact(Account.create(AccountStatus.ONLINE, contactUsername, null, null));

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

                // If the contact was player
                String checkPlayer = "SELECT COUNT(user) AS number FROM player WHERE user=?";
                preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(checkPlayer);
                preparedStatement.setString(1, contact.account.username);
                result = preparedStatement.executeQuery();
                result.next();
                numberOfOccurrences = result.getInt("number");

                String updateUserType = "";

                if (numberOfOccurrences == 0) {
                    updateUserType = "UPDATE user SET typeOfUser=NULL WHERE username=?";
                } else {
                    updateUserType = "UPDATE user SET typeOfUser='PLAYER' WHERE username=?";
                }

                preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(updateUserType);
                preparedStatement.setString(1, contact.account.username);
                preparedStatement.executeUpdate();

            }
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", true);
            returnJson.put("message", "Contact removed successfully");
            return returnJson.toJSONString();
        } catch (SQLException | ClassNotFoundException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }

    /**
     * This method is implemented in order to obtain all of the contacts from a
     * user with a given username, it constructs all the player with the main
     * attributes in order to send it to the client side.
     *
     * @param username It is the username of the user in which the method will
     * base on in order to construct a list of contacts.
     * @return The method returns a linked list with the assembled contacts.
     * @throws SQLException The method returns the this exception when a
     * database error occurs.
     * @throws ClassNotFoundException The method returns the this exception when
     * a the class is not found in the executeQuery method.
     */
    private static LinkedList<Contact> getContactListFromUser(String username) throws SQLException, ClassNotFoundException {

        LinkedList<Contact> contacts = new LinkedList<>();

        String queryContact = "SELECT * FROM contactlist, account WHERE user_username=? AND account.username=contactlist.contact_username";
        PreparedStatement preparedStatement = DatabaseConnector.getInstance().getConnection().prepareStatement(queryContact);
        preparedStatement.setString(1, username);
        ResultSet resultContacts = preparedStatement.executeQuery();

        while (resultContacts.next()) {
            String usernameContact = resultContacts.getString("username");
            String emailContact = resultContacts.getString("email");
            int numberOfSessionWon = resultContacts.getInt("numberOfSessionswon");
            int numberOfSessionLost = resultContacts.getInt("numberOfSessionLost");
            float percentageOfWins = resultContacts.getFloat("percentageOfWins");

            Contact contact = new Contact(Account.create(AccountStatus.ONLINE, usernameContact, null, emailContact));
            contact.account.numberOfSessionLost = numberOfSessionLost;
            contact.account.numberOfSessionWon = numberOfSessionWon;
            contact.account.percentageOfWins = percentageOfWins;

            contacts.add(contact);
        }

        return contacts;
    }

    public static String getContactsFromUser(String json) throws ParseException {
        try {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + json + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;
            
            JSONObject parsedObject = (JSONObject) jsonArray.get(0);
            
            String username = (String) parsedObject.get("username");
            
            LinkedList<Contact> contacts = getContactListFromUser(username);
            
            Iterator<Contact> contactIterator = contacts.listIterator();
            
            JSONObject result = new JSONObject();
            JSONArray contactArray = new JSONArray();
            
            while(contactIterator.hasNext()) {
                Contact actualContact = contactIterator.next();
                JSONObject contact = new JSONObject();
                contact.put("username", actualContact.account.username);
                contact.put("email", actualContact.account.username);
                contact.put("numberOfSessionsWon", actualContact.account.username);
                contact.put("numberOfSessionLost", actualContact.account.username);
                contact.put("percentageOfWins", actualContact.account.username);
                contact.put("status", actualContact.account.status.toString());
                
                contactArray.add(contact);
            }
            result.put("players", contactArray);
            result.put("status", true);
            
            return result.toJSONString();
        } catch (SQLException | ClassNotFoundException ex) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", false);
            returnJson.put("message", ex.getMessage());
            return returnJson.toJSONString();
        }
    }
}
