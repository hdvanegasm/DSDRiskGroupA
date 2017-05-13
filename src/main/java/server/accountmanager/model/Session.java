package server.accountmanager.model;

import java.util.LinkedList;

/**
 * This class represents a session in the game. It has the basic attributes in order to create and manage a session in the game.
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

    /**
     * The constructor of the session is based on basic attributes. It allows to create a fill the basic attributes of a session.
     * @param id It is the identification of the session. This identification is unique in the system and it can be used as a reference for the players.
     * @param numberOfPlayers This attribute store the number of players in the session.
     * @param type It represents the type of game that will be played. Each type of game has different rules and number of players.
     * @param state This attribute shows what is the actual state of the session. A session can be in a "creating" status, a "playing" status (this status is selected when the host pushes the "Start game" button), and "finished". 
     */
    private Session(int id, int numberOfPlayers, SessionType type, SessionState state) {
        this.id = id;
        this.numberOfPlayers = numberOfPlayers;
        this.type = type;
        this.state = state;
        this.requests = new LinkedList<>();
        this.players = new LinkedList<>();
    } 
    
    public boolean join(User user) {
        return true;
    }
    
    public boolean join(Contact contact) {
        return true;
    }
    
    public static Session create(int id, int numberOfPlayers, SessionType type, SessionState state) {
        return new Session(id, numberOfPlayers, type, state);
    }
    
    public static boolean leave() {
        return true;
    }
}
