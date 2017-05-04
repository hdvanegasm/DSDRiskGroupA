/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accountmanager.controller;

import accountmanager.model.Account;

/**
 * 
 * @author Hernan Dario Vanegas Madrigal
 */
public class AccountManager {
    
    public static final int port = 8080;
    public static final String serverPath = "Put path here";
    
    public static boolean createAccount(Account account) {
        //TODO Register account to database
        return true;
    }
    
    public static boolean updatePassword(Account account) {
        // TODO Update password of the account
        return true;
    }
    
    public static boolean logOut(Account account) {
        // TODO Update account status in DBA
        return true;
    }
    
    public static boolean logIn(Account account) {
        //TODO Update account status in DBA
        return true;
    }
}
