package server.gamebuilder.model;

import server.accountmanager.model.Account;
import server.accountmanager.model.User;

/**
 * This class represents a player. A player is a user that is in the session
 * construction phase or he is in a session.
 *
 * @author Hernán Darío Vanegas Madrigal
 */
public class Player extends User {

    public Color color;

    /**
     * This method allows to create a player using a given account. This is in
     * order to create a player based on a existing user.
     *
     * @param account This is the base account to create a player. In a normal
     * process, this account belongs to the user that will be transformed to a
     * player when he joins to a session.
     * @param color It represents the color of the player.
     */
    public Player(Account account, Color color) {
        super(account);
        this.color = color;
    }

    /**
     * This method is used to take out a player from a session in the
     * construction phase.
     *
     * @param session This parameter represents the session in which the user
     * will be deleted
     * @return It returns true if the user was successfully taken out from the
     * session, otherwise it returns false.
     */
    public boolean takeOut(Session session) {
        session.players.remove(this);
        return true;
    }

    @Override
    public String toString() {
        return "Player{" + "color=" + color + ", " + account + "}";
    }

}
