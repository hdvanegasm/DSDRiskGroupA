/**
 *
 */
package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import static spark.Spark.post;
import server.accountmanager.controller.AccountManager;
import server.accountmanager.model.Account;
import server.accountmanager.model.AccountStatus;
import server.gamebuilder.model.Map;
import server.gamebuilder.model.Session;
import server.gamebuilder.model.SessionState;
import server.gamebuilder.model.SessionType;
import server.accountmanager.model.User;
import server.gamebuilder.controller.RequestHandler;
import server.gamebuilder.controller.SessionBuilder;
import server.gamebuilder.model.Request;
import server.gamebuilder.model.RequestState;

/**
 * This class is the main class of the server. It has all of the services used
 * by the server, and redirects the work to other modules.
 *
 * @author Hernan Dario Vanegas Madrigal
 */
public class Server {

    /**
     * Method main that has the implementation of the services.
     *
     * @param args
     */
    public static void main(String[] args) {

        // Creating account service
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
                System.out.println("Account created");
                Account newAccount = Account.create(AccountStatus.OFFLINE, username, password, email);
                User user = new User(newAccount);
                if (AccountManager.createAccount(user)) {    
                    response.header("result", "true");
                    response.body("{ \"result\" : \"true\" }");
                    return response.body();
                } else {
                    response.header("result", "true");
                    response.body("{ \"result\" : \"false\" }");
                    return response.body();
                }
            } else {
                response.header("result", "true");
                response.body("{ \"result\" : \"false\" }");
                return response.body();
            }
        });

        // Login account service
        post("/logIn", (request, response) -> {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + request.body() + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            String password = (String) parsedObject.get("password");

            User user = new User(Account.create(AccountStatus.OFFLINE, username, password, null));

            if (AccountManager.logIn(user)) {
                response.body("{ \"result\" : \"true\" }");
                response.header("result", "true");
                return response.body();
            } else {
                response.body("{ \"result\" : \"false\" }");
                response.header("result", "false");
                return response.body();
            }
        });

        // Logout account service
        post("/logout", (request, response) -> {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + request.body() + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");

            User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));

            return AccountManager.logOut(user);
        });

        // Create session service
        post("/createSession", (request, response) -> {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + request.body() + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;

            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String hostUsername = (String) parsedObject.get("username");
            int numberOfPlayers = Integer.parseInt((String) parsedObject.get("numberOfPlayers"));
            String sessionType = (String) parsedObject.get("type");
            String mapName = (String) parsedObject.get("mapName");

            SessionType sessionTypeEnum = null;

            if (sessionType.equals("world domination risk")) {
                sessionTypeEnum = SessionType.WORLD_DOMINATION_RISK;
            } else if (sessionType.equals("secret mission risk")) {
                sessionTypeEnum = SessionType.SECRET_MISSION_RISK;
            } else if (sessionType.equals("capital risk")) {
                sessionTypeEnum = SessionType.CAPITAL_RISK;
            } else if (sessionType.equals("risk for two players")) {
                sessionTypeEnum = SessionType.RISK_FOR_TWO_PLAYERS;
            }
            Map map = new Map(mapName);
            Session newSession = Session.create(numberOfPlayers, sessionTypeEnum, SessionState.CREATING, map);

            Account account = Account.create(AccountStatus.ONLINE, hostUsername, null, null);
            User hostUser = new User(account);

            return SessionBuilder.createSession(hostUser, newSession);
        });

        // Change password services
        post("/changePassword", (request, response) -> {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + request.body() + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;
            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            String password = (String) parsedObject.get("actualPassword");
            String newPassword = (String) parsedObject.get("newPassword");

            User user = new User(Account.create(AccountStatus.ONLINE, username, password, null));

            return AccountManager.changePassword(user, newPassword);
        });

        // Make request service
        post("/makeRequest", (request, response) -> {
            JSONParser parser = new JSONParser();
            String jsonToString = "[" + request.body() + "]";
            Object obj = parser.parse(jsonToString);
            JSONArray jsonArray = (JSONArray) obj;
            JSONObject parsedObject = (JSONObject) jsonArray.get(0);

            String username = (String) parsedObject.get("username");
            int idSession = Integer.parseInt((String) parsedObject.get("idSession"));

            Session session = Session.create(idSession);
            User user = new User(Account.create(AccountStatus.ONLINE, username, null, null));
            
            return RequestHandler.makeRequest(session, user);
        });
    }
}
