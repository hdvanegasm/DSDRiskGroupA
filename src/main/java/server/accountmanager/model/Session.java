/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.accountmanager.model;

import java.util.LinkedList;

/**
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
