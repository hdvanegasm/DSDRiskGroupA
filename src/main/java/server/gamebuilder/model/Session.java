package server.gamebuilder.model;

import java.util.LinkedList;
import server.accountmanager.model.Contact;
import server.accountmanager.model.User;
import java.util.Random;

/**
 * This class represents a session in the game. It has the basic attributes in
 * order to create and manage a session in the game.
 *
 * @author Hernán Darío Vanegas Madrigal
 */
public class Session {

    public int id;
    public int numberOfPlayers;
    public SessionType type;
    public Map map;
    public LinkedList<Request> requests;
    public LinkedList<Player> players;
    public SessionState state;

    private LinkedList<Color> availableColors;

    /**
     * The constructor of the session is based on basic attributes. It allows to
     * create a fill the basic attributes of a session.
     *
     * @param id It is the identification of the session. This identification is
     * unique in the system and it can be used as a reference for the players.
     * @param numberOfPlayers This attribute store the number of players in the
     * session.
     * @param type It represents the type of game that will be played. Each type
     * of game has different rules and number of players.
     * @param state This attribute shows what is the actual state of the
     * session. A session can be in a "creating" status, a "playing" status
     * (this status is selected when the host pushes the "Start game" button),
     * and "finished".
     */

    private Session(int numberOfPlayers, SessionType type, SessionState state, Map map) {

        // Load available colors
        availableColors = new LinkedList<>();
        for (int i = 0; i < Color.values().length; i++) {
            availableColors.add(Color.values()[i]);
        }

        this.numberOfPlayers = numberOfPlayers;
        this.type = type;
        this.state = state;
        this.requests = new LinkedList<>();
        this.players = new LinkedList<>();
        this.map = map;

    }

    // TODO Add documentation
    private Session(int id) {
        this.id = id;
        this.requests = new LinkedList<>();
        this.players = new LinkedList<>();
    }

    /**
     * This method allows to a common user to join to the session through the
     * request system. Once the request is accepted, the user can join to the
     * session an he is downgraded to Player in the hierarchy level.
     *
     * @param user This is the reference to the user that will be joined to the
     * session.
     * @return The method returns true if the user was successfully joined,
     * otherwise it returns false.
     */
    public boolean join(User user) {
        // Creates a random number to select the available
        Random random = new Random();
        int randomIndex = (int) (random.nextFloat() * availableColors.size());

        // Creates a player based on the user parameter
        Player player = new Player(user.account, availableColors.remove(randomIndex));
        players.add(player);

        return true;
    }

    /**
     * This method allows to a contact to join to the session through the
     * invitation system. If the contact accepts the invitation, he will join to
     * the session and it will be converted to a Player.
     *
     * @param contact It is the contact that will be joined to the session.
     * @return The method returns true if the contact was joined successfully,
     * and it returns false if not.
     */
    public boolean join(Contact contact) {
        // Creates a random number to select the available
        Random random = new Random();
        int randomIndex = (int) (random.nextFloat() * availableColors.size());

        // Creates a player based on the contact parameter
        Player player = new Player(contact.account, availableColors.remove(randomIndex));
        players.add(player);
        return true;
    }

    /**
     * This method creates a session with a given set of parameters. This is the
     * only way to create a session.
     *
     * @param id This parameter represents the ID of the session. This number
     * will identify the session in the game.
     * @param numberOfPlayers This number represents the number of players that
     * will participate in the game.
     * @param type This parameter determines the type of session that will be
     * played. The value of this attribute will determine the rules of the game
     * and the number of players that will be playing.
     * @param state This parameter represents the actual state of the session.
     * There are three possible values for this parameter: playing, creating and
     * finished.
     * @return The method returns a reference to the new object created.
     */
    public static Session create(int numberOfPlayers, SessionType type, SessionState state, Map map) {
        return new Session(numberOfPlayers, type, state, map);
    }

    // TODO add documentation
    public static Session create(int id) {
        return new Session(id);
    }

    /**
     * This method allows to a player to leave the session in the "creating"
     * phase. It will change the status of the player to
     *
     * @param player This parameter represents the player that will leave the
     * session.
     * @return The method returns true if the player leaves the session
     * successfully, otherwise, it returns false.
     */
    public boolean leave(Player player) {
        players.remove(player);
        return true;
    }
}
