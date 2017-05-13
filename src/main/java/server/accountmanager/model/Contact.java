package server.accountmanager.model;

/**
 * This class represents a contact of an user. A contact is an User itself. This class is created in order to make a differentiation between types.
 * @author Hernán Dario Vanegas Madrigal
 */
public class Contact extends User{
    
    public Contact(Account account) {
        super(account);
    }
    
}
