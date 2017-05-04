/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accountmanager.model;

import java.util.LinkedList;

/**
 *
 * @author Hernán Darío Vanegas Madrigal
 */
public class Session {
    private int id;
    private int numberOfPlayers;
    private SessionType type;
    private Map map;
    private LinkedList<Request> requests;
    private LinkedList<Player> players;
    private SessionState state;

    public Session(int id, int numberOfPlayers, SessionType type, SessionState state) {
        this.id = id;
        this.numberOfPlayers = numberOfPlayers;
        this.type = type;
        this.state = state;
        this.requests = new LinkedList<>();
        this.players = new LinkedList<>();
    } 
}
