package server.accountmanager.model;

/**
 * This class represents a player. A player is a user that is in the session construction phase or he is in a session.
 * @author Hernán Darío Vanegas Madrigal
 */
public class Player extends User { 
    
    public Color color;

    /**
     * This method allows to create a player using a given account. This is in order to create a player based on a existing user.
     * @param account This is the base account to create a player. In a normal process, this account belongs to the user that will be transformed to a player when he joins to a session.
     */
    public Player(Account account) {
        super(account);
    }
    
    /**
     * This method is used to take out a player from a session in the construction phase.
     * @return It returns true if the user was successfully taken out from the session, otherwise it returns false. 
     */
    public boolean takeOut() {
        return true;
    }

}
