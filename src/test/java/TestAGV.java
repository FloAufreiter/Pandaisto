import AGV.AGV;
import AGV.Area;
import AGV.Location;
import org.junit.Test;
import shared.ItemType;
import warehouse.Database;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class TestAGV {
	
    @Test
    public void testCommunicationAGVWarehouse() throws SQLException, InterruptedException {
        AGV agv = AGV.getInstance();
        Database.getInstance().initTestDB();
        //add enough items so no reorder is triggered
        Location l2 = Area.getInstance().getLocation(Location.LocationType.TOP_SHELF1, 1);
        Location l3 = Area.getInstance().getLocation(Location.LocationType.FLOOR_SHELF, 33);
        agv.startAGV();
        agv.getAGVTaskScheduler().createTask(l2, l3, ItemType.SCREW);
        agv.stopAGV();
        AGV.getInstance().getSchedulerThread().join();
        assertEquals(5, Database.getInstance().itemsInStock(ItemType.SCREW));
        Database.getInstance().deleteWarehouse(0);
    }

    @Test
    public void testCommunicationAGVWithRobotArms() {}

//	@Test
//    public void testCommunicationAGVWithRobotArms() throws SQLException, InterruptedException {
//        AGV agv = AGV.getInstance();
//        RobotScheduler.getInstance().startRobotArms();
//        Database.getInstance().initTestDB();
//        Location l1 = Area.getLocation(Location.LocationType.FLOOR_SHELF, 0);
//        Location r0 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 0);
//        ShelfType s = AGVInterface.getItemLocation(ItemType.RED_PAINT);
//        Location l2 = Area.getLocation(s.getType(), s.getId());
//        Location r4 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 4);
//        agv.startAGV();
//        agv.getAGVTaskScheduler().createTask(l1, r0, ItemType.CAR_BODY);
//        agv.getAGVTaskScheduler().createTask(l2, r4, ItemType.RED_PAINT);
//
//        RobotScheduler.getInstance().startRobotArms();
//
//        Thread.sleep(30000);
//
//        //check if Database is consistent
//        assertEquals(18, Database.getInstance().itemsInStock(ItemType.CAR_BODY));
//        assertEquals(18, Database.getInstance().itemsInStock(ItemType.RED_PAINT));
//        Database.getInstance().deleteTestDB();
//	}

    @Test
    public void testItemReorder() throws SQLException, InterruptedException {
    	Database db = Database.getInstance();
    	db.initTestDB();
    	assertEquals(5, db.itemsInStock(ItemType.SCREW));
    	for(int i = 0; i < 3; i++) db.deleteItem(i);
      	AGV.getInstance().stopAGV();
        AGV.getInstance().getSchedulerThread().join();
    	assertEquals(5, db.itemsInStock(ItemType.SCREW));
    	db.deleteTestDB();
    }
}
