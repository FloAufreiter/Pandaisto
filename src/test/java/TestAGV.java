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
        //add enough items so no reorder is triggered
        for(int i = 2; i < 20; i ++) Database.getInstance().insertItem(i, ItemType.RED_PAINT);

        for(int i = 22; i < 40; i ++) Database.getInstance().insertItem(i, ItemType.CAR_BODY);
        
        Location l1 = Area.getLocation(Location.LocationType.FLOORSHELF, 0);
        Location r1 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 1);
        Location l2 = Area.getLocation(Location.LocationType.TOPSHELF1, 1);
        Location l3 = Area.getLocation(Location.LocationType.FLOORSHELF, 3);
        
        agv.startAGV();

        agv.getAGVTaskScheduler().createTask(l1, r1, ItemType.CAR_BODY);
        Thread.sleep(1000);
        agv.getAGVTaskScheduler().createTask(l2, l3, ItemType.RED_PAINT);
        Thread.sleep(10000);//sleep to make sure 2 forklifts are started
        assertEquals(18, Database.getInstance().itemsInStock(ItemType.CAR_BODY));
        assertEquals(18, Database.getInstance().itemsInStock(ItemType.RED_PAINT));
        Database.getInstance().deleteWarehouse(0);
        agv.stopAGV();
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
    	db.deleteItem(1000);
    	assertEquals(99, db.itemsInStock(ItemType.RED_PAINT));
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
    	for(int i = 0; i < 10; i++) {
    		db.insertItem(i, ItemType.SCREW);
    	}
    	assertEquals(10, db.itemsInStock(ItemType.SCREW));
    	for(int i = 0; i < 10; i++) db.deleteItem(i);
    	Thread.sleep(60000); //wait long enough for forklifts 
//    	a.stopAGV();
//        AGV.getSchedulerThread().join();
    	assertEquals(10, db.itemsInStock(ItemType.SCREW));
    	db.deleteWarehouse(0);
    	a.stopAGV();
    }
    
    @Test
    public void testItemNotRemovedTwice() throws SQLException {
    	Database db = Database.getInstance();
    	db.insertWarehouse(10, 100);
    	db.insertShelf(1000, 10, 1);
    	db.insertItem(1000, ItemType.BLUE_PAINT);
    	int id1 = db.itemByType(ItemType.BLUE_PAINT.toString());
    	int id2 = db.itemByType(ItemType.BLUE_PAINT.toString());
    	assertEquals(-1, id2); //id for same item cant be given out twice
    	db.deleteWarehouse(10); //cleanup

    }
}
