package AGV;

import shared.Commodity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        AGV agv = new AGV();
        Location l1 = Area.getLocation(Location.LocationType.FLOORSHELF, 1);
        Location r1 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 1);
        Location l2 = Area.getLocation(Location.LocationType.FLOORSHELF, 2);
        Location r2 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 3);
        agv.getAGVTaskScheduler().createTask(l1, r1, new Commodity(1, 10));
        agv.getAGVTaskScheduler().createTask(l2, r2, new Commodity(2, 30));
        agv.startAGV();
        agv.stopAGV();
    }
}
