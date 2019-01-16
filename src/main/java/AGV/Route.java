package AGV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * used to accumulate several tasks for one forklift
 */
class Route {

    private HashMap<Location, List<Task>> stops = new HashMap<>(); //there can be multiple tasks at one location

    void addLocation(Location location, Task task) {
        if (!stops.containsKey(location)) {
            List l = new ArrayList<>();
            l.add(task);
            stops.put(location, l);
            return;
        }
        stops.get(location).add(task);
    }

    HashMap<Location, List<Task>> getStops() {
        return stops;
    }
}
