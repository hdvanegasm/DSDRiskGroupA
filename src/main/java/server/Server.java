/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import static spark.Spark.post;
import server.accountmanager.controller.AccountManager;
import server.accountmanager.model.Account;
import server.accountmanager.model.AccountStatus;
import server.accountmanager.model.User;

/**
 *
 * @author root
 */
public class Server {

    public static void main(String[] args) {

        // TODO Implement services
        post("/createAccount", (request, response) -> {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + request.body() + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            String password = (String) parsedObject.get("password");
            String confirmPass = (String) parsedObject.get("confirmPass");
            String email = (String) parsedObject.get("e-mail");

            if (confirmPass.equals(password)) {
                Account newAccount = Account.create(AccountStatus.OFFLINE, username, password, email);
                User user = new User(newAccount);
                return AccountManager.createAccount(user);
            } else {
                return false;
            }
        });

        post("/login", (request, response) -> {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + request.body() + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            //TODO Get attributes
            String username = (String) parsedObject.get("username");
            String password = (String) parsedObject.get("password");

            User user = new User(Account.create(AccountStatus.OFFLINE, username, password, null));

            return AccountManager.logIn(user);
        });

        post("/logout", (request, response) -> {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + request.body() + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            //TODO Get attributes
            String username = (String) parsedObject.get("username");

            User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));

            return AccountManager.logOut(user);
        });
    }
}
