import AGV.AGV;
import assembly_robot_arms.RobotScheduler;
import org.junit.Test;
import shared.ItemType;
import AGV.Location;
import AGV.Area;
import warehouse.Database;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class TestAGV {

    @Test
    public void testCommunicationAGVWarehouse() throws SQLException, InterruptedException {
        AGV agv = AGV.getInstance();
        Database.getInstance().insertItem(0, ItemType.CAR_BODY);
        Database.getInstance().insertItem(1, ItemType.RED_PAINT);
        Location l1 = Area.getLocation(Location.LocationType.FLOORSHELF, 0);
        Location r1 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 1);
        Location l2 = Area.getLocation(Location.LocationType.TOPSHELF1, 1);
        Location l3 = Area.getLocation(Location.LocationType.FLOORSHELF, 3);

        agv.getAGVTaskScheduler().createTask(l1, r1, ItemType.CAR_BODY);
        agv.getAGVTaskScheduler().createTask(l2, l3, ItemType.RED_PAINT);
        agv.startAGV();
        agv.stopAGV();
        AGV.getSchedulerThread().join();
        assertEquals(0, Database.getInstance().itemsInStock(ItemType.CAR_BODY));
        assertEquals(1, Database.getInstance().itemsInStock(ItemType.RED_PAINT));
    }

    @Test
    public void addSomethingToDB() throws SQLException {
        Database.getInstance().insertWarehouse(4, 100);
        Database.getInstance().insertShelf(50000, 4, 0);
        Database.getInstance().insertItem(50000, ItemType.CAR_BODY);
        assertEquals(1, Database.getInstance().itemsInStock(ItemType.CAR_BODY));
        Database.getInstance().deleteItem(50000);
        assertEquals(0, Database.getInstance().itemsInStock(ItemType.CAR_BODY));

    }

    @Test
    public void testCommunicationAGVWithRobotArms() throws SQLException, InterruptedException {
        AGV agv = AGV.getInstance();
        RobotScheduler.getInstance().startRobotArms();
        Database.getInstance().insertItem(0, ItemType.CAR_BODY);
        Database.getInstance().insertItem(1, ItemType.RED_PAINT);
        Location l1 = Area.getLocation(Location.LocationType.FLOORSHELF, 0);
        Location r0 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 0);
        Location l2 = Area.getLocation(Location.LocationType.TOPSHELF1, 1);
        Location r4 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 4);
        agv.startAGV();
        agv.getAGVTaskScheduler().createTask(l1, r0, ItemType.CAR_BODY);
        agv.getAGVTaskScheduler().createTask(l2, r4, ItemType.RED_PAINT);
        agv.stopAGV();
        RobotScheduler.getInstance().startRobotArms();
        AGV.getSchedulerThread().join();
    }

}
