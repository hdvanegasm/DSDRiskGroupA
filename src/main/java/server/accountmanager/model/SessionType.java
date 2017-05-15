package server.accountmanager.model;

/**
 * This enumeration class represents the possible types of the session. Each possible type is based on the rules of the game, and each type determines the rules of the session and the dynamic of the game. Also it determines the number of players supported in the session.
 * @author Hernán Darío Vanegas Madrigal
 */
public enum SessionType {
    WORLD_DOMINATION_RISK,
    SECRET_MISSION_RISK,
    CAPITAL_RISK,
    RISK_FOR_TWO_PLAYERS
}
