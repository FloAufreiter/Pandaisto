import AGV.AGV;
import assembly_robot_arms.RobotScheduler;
import org.junit.Test;
import shared.ItemType;
import AGV.Location;
import Monitoring.Monitor;
import AGV.Area;
import warehouse.Database;
import warehouse.ShelfType;
import warehouse.AGVInterface;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class TestAGV {
	
    @Test
    public void testCommunicationAGVWarehouse() throws SQLException, InterruptedException {
        AGV agv = AGV.getInstance();
        Database.getInstance().initTestDB();
        //add enough items so no reorder is triggered
        
        Location l2 = Area.getLocation(Location.LocationType.TOP_SHELF1, 1);
        Location l3 = Area.getLocation(Location.LocationType.FLOOR_SHELF, 33);
        
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
    public void testCommunicationAGVWithRobotArms() throws SQLException, InterruptedException {
        AGV agv = AGV.getInstance();
        RobotScheduler.getInstance().startRobotArms();
        Database.getInstance().initTestDB();	 
        Location l1 = Area.getLocation(Location.LocationType.FLOOR_SHELF, 0);
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
   

//THIS SHOULD BE IN MONITOR BUT DEPENDENCIES ARE FUCKED
public static void main(String[] args) {
	try {
		Database.getInstance().initTestDB();
	} catch (SQLException e1) {
		e1.printStackTrace();
		System.exit(-1); //SHIT
	}
	Monitor.getInstance();
		
		
       
		while(true) {
			
		}
		
	}
}
