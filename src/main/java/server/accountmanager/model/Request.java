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
public class Request {
    public int id;
    public RequestState state;

    public Request(int id, RequestState state) {
        this.id = id;
        this.state = state;
    }
    
    public void make() {
        
    }
    
    public boolean answer() {
        return true;
    }
}
