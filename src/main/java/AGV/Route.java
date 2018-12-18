package AGV;

import shared.Location;
import java.util.HashMap;

//Used to accumulate Tasks and define Routes for Forklifters
public class Route {

    private HashMap<Location, Forklifter.Action> routeActions;

    void addLocationAndAction(Location location, Forklifter.Action action) {
        routeActions.put(location, action);
    }

    public HashMap<Location, Forklifter.Action> getRouteActions() {
        return routeActions;
    }
}
