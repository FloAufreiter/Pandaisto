package AGV;

import java.util.HashMap;

class Route {

    private HashMap<Location, Task> stops = new HashMap<>();

    void addLocation(Location location, Task task) {
        stops.put(location, task);
    }

    HashMap<Location, Task> getStops() {
        return stops;
    }
}
