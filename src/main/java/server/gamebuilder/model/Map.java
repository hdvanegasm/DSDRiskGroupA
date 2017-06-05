package server.gamebuilder.model;

/**
 * This class represents the basic information of a map used in the Setup
 * Module.
 *
 * @author Hernán Darío Vanegas Madrigal
 */
public class Map {

    public String name;

    /**
     * This method creates a map with the specified name.
     *
     * @param name This is the name of the map, it is a unique identifier in the
     * system.
     */
    public Map(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Map{" + "name=" + name + '}';
    }

}
