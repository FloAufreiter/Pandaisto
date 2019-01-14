import AGV.AGV;
import assembly_robot_arms.RobotScheduler;
import org.junit.Test;
import shared.ItemType;
import AGV.Location;
import Monitoring.Monitor;
import Monitoring.MonitoringGUI;
import AGV.Area;
import warehouse.Database;
import warehouse.ShelfType;
import warehouse.AGVInterface;

import java.awt.EventQueue;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class TestAGV {

	@Test 
	public void testInitDB() throws SQLException, InterruptedException {
		Database db = Database.getInstance();
		db.initTestDB();
		assertEquals(20, db.itemsInStock(ItemType.BLUE_PAINT));
		db.deleteTestDB();
	}
	
    @Test
    public void testCommunicationAGVWarehouse() throws SQLException, InterruptedException {
        AGV agv = AGV.getInstance();
        Database.getInstance().initTestDB();
        //add enough items so no reorder is triggered
        
        Location l1 = Area.getLocation(Location.LocationType.FLOORSHELF, 0);
        Location r1 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 1);
        Location l2 = Area.getLocation(Location.LocationType.TOPSHELF1, 1);
        Location l3 = Area.getLocation(Location.LocationType.FLOORSHELF, 33);
        
        agv.startAGV();

//        agv.getAGVTaskScheduler().createTask(l1, r1, ItemType.SCREW);
        Thread.sleep(1000);
        agv.getAGVTaskScheduler().createTask(l2, l3, ItemType.SCREW);
        Thread.sleep(30000);//sleep to make sure 2 forklifts are started
        assertEquals(5, Database.getInstance().itemsInStock(ItemType.SCREW));
        agv.stopAGV();
        Database.getInstance().deleteWarehouse(0);
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
    public void checkItemStock1() throws SQLException {
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
    
	@Test
    public void testCommunicationAGVWithRobotArms() throws SQLException, InterruptedException {
        AGV agv = AGV.getInstance();
        RobotScheduler.getInstance().startRobotArms();
        Database.getInstance().initTestDB();	 
        Location l1 = Area.getLocation(Location.LocationType.FLOORSHELF, 0);
        Location r0 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 0);
        ShelfType s = AGVInterface.getItemLocation(ItemType.RED_PAINT);
        Location l2 = Area.getLocation(s.getType(), s.getId());
        Location r4 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 4);
        agv.startAGV();
        agv.getAGVTaskScheduler().createTask(l1, r0, ItemType.CAR_BODY);
        agv.getAGVTaskScheduler().createTask(l2, r4, ItemType.RED_PAINT);
        
        RobotScheduler.getInstance().startRobotArms();
        
        Thread.sleep(30000);
        
        //check if Database is consistent
        assertEquals(18, Database.getInstance().itemsInStock(ItemType.CAR_BODY));
        assertEquals(18, Database.getInstance().itemsInStock(ItemType.RED_PAINT));
        Database.getInstance().deleteTestDB();
	}
    public void checkCorrectItem() throws SQLException {
    	Database db = Database.getInstance();
    }
    
    @Test
    public void testItemReorder() throws SQLException, InterruptedException {
    	Database db = Database.getInstance();
    	db.initTestDB();
    	assertEquals(5, db.itemsInStock(ItemType.SCREW));
    	for(int i = 0; i < 3; i++) db.deleteItem(i);
    	Thread.sleep(60000); //wait long enough for forklifts 
//    	a.stopAGV();
//        AGV.getSchedulerThread().join();
    	assertEquals(5, db.itemsInStock(ItemType.SCREW));
    	db.deleteTestDB();
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

    @Test
    public void checkShelf11() throws SQLException {
    	Database db = Database.getInstance();
    	db.initTestDB();
    	db.deleteItem(11);
    	assertEquals(4, db.itemsInStock(ItemType.CAR_BODY));
    	db.deleteTestDB();
    }
//THIS SHOULD BE IN MONITOR BUT DEPENDENCIES ARE FUCKED
public static void main(String[] args) {
	try {
		Database.getInstance().initTestDB();
	} catch (SQLException e1) {
		e1.printStackTrace();
		System.exit(-1); //SHIT
	}
	Monitor.getInstance();
		
		
		//=====STUPID TEST====
		Location l1 = Area.getLocation(Location.LocationType.FLOORSHELF, 0);
		Location r1 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 1);
        Location l2 = Area.getLocation(Location.LocationType.TOPSHELF1, 1);
        Location l3 = Area.getLocation(Location.LocationType.FLOORSHELF, 33);
        
        AGV agv = AGV.getInstance();
//        agv.getAGVTaskScheduler().createTask(l1, r1, ItemType.SCREW);
        try {
			Thread.sleep(1000);
			//agv.getAGVTaskScheduler().createTask(l2, l3, ItemType.SCREW);
	        Thread.sleep(20000);//sleep to make sure 2 forklifts are started
	        
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //====STUPID TEST END===
       
		while(true) {
			
		}
		
	}
}
