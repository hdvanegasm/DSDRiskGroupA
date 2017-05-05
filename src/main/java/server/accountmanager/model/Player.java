/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.accountmanager.model;

/**
 *
 * @author Hernán Darío Vanegas Madrigal
 */
public class Player extends User{
    
    public Color color;

    public Player(Account account) {
        super(account);
    }
    
    public boolean takeOut() {
        return true;
    }

}
