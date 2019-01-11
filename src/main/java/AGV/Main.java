package AGV;

import shared.Commodity;

public class Main {

    public static void main(String[] args) {
        Location l1 = Area.getLocation(Location.LocationType.FLOORSHELF, 1);
        Location r1 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 1);
        TaskScheduler.createTask(l1, r1, new Commodity(20, 10));
    }
}
