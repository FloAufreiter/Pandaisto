import AGV.AGV;
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
        Database.getInstance().deleteWarehouse(4);
    }
    
    @Test
    public void checkItemStock() throws SQLException {
    	Database db = Database.getInstance();
    	db.insertWarehouse(10, 100);
    	for(int i = 0; i < 100; i ++) db.insertShelf(i+1000, 10, 0);
    	for(int i = 0; i < 100; i++) db.insertItem(i+1000, ItemType.RED_PAINT);
    	assertEquals(100, db.itemsInStock(ItemType.RED_PAINT));
    	db.deleteWarehouse(10);
    	assertEquals(0, db.itemsInStock(ItemType.RED_PAINT));
    }
    
    public void checkCorrectItem() throws SQLException {
    	Database db = Database.getInstance();
    }
    
    @Test
    public void testItemReorder() throws SQLException, InterruptedException {
    	Database db = Database.getInstance();
    	AGV a = AGV.getInstance();
    	a.startAGV();
    	for(int i = 0; i < 60; i++) {
    		db.insertItem(i, ItemType.SCREW);
    	}
    	assertEquals(60, db.itemsInStock(ItemType.SCREW));
    	for(int i = 0; i < 31; i++) db.deleteItem(i);
    	Thread.sleep(10000);
//    	a.stopAGV();
//        AGV.getSchedulerThread().join();
    	assertEquals(59, db.itemsInStock(ItemType.SCREW));
    }
}
