package AGV;
import shared.ItemType;
import warehouse.Database;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        Database.getInstance();
        AGV agv = AGV.getInstance();
        Location l1 = Area.getLocation(Location.LocationType.FLOORSHELF, 1);
        Location r1 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 1);
        Location l2 = Area.getLocation(Location.LocationType.FLOORSHELF, 2);
        Location r2 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 3);
        agv.getAGVTaskScheduler().createTask(l1, r1, ItemType.CAR_BODY);
        agv.getAGVTaskScheduler().createTask(l2, r2, ItemType.RED_PAINT);
        agv.startAGV();
        agv.stopAGV();
    }
}
