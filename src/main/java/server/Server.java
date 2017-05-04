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

/**
 *
 * @author root
 */
public class Server {
    public static void main(String[] args) {
        // TODO Implement services
        
        post("/createAccount", (request, response) -> {
            JSONParser parser = new JSONParser();
            String jsonToString = "["+ request.body() +"]";           
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);
            
            //TODO Get attributes
            String username = (String) parsedObject.get("username");
            
            //TODO get response from DBA
            return true;

        });
    }
}
