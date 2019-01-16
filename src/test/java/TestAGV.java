import AGV.AGV;
import AGV.Area;
import AGV.Location;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import shared.ItemType;
import warehouse.AGVInterface;
import warehouse.Database;
import warehouse.ShelfType;

import java.sql.SQLException;
import java.util.Random;

import static AGV.Location.LocationType.FLOOR_SHELF;
import static AGV.Location.LocationType.PRODUCTION_LINE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * Tests for AGV
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAGV {

    @Before
    public void initDB() throws SQLException {
        TestUtils.initDB();
    }

    @After
    public void deleteDB() {
        TestUtils.deleteDB();
    }


    /**
     * All tests using DB and other systems are aggregated into one, since otherwise race conditions happen, because of the singleton AGV
     *
     * @throws SQLException
     * @throws InterruptedException
     */
    @Test
    public void testAGV() throws SQLException, InterruptedException {
        //check communication with warehouse
        AGV agv = AGV.getInstance();
        ShelfType s = AGVInterface.getItemLocation(ItemType.SCREW);
        Location l1 = Area.getInstance().getLocation(s.getType(), s.getId());
        ShelfType s2 = AGVInterface.getFreeItemLocation();
        Location l2 = Area.getInstance().getLocation(s2.getType(), s2.getId());
        agv.getAGVTaskScheduler().createTask(l1, l2, ItemType.SCREW);
        assertEquals(5, Database.getInstance().itemsInStock(ItemType.SCREW));
        ShelfType s3 = AGVInterface.getItemLocation(ItemType.CAR_BODY);
        assert s3 != null;
        Location l3 = Area.getInstance().getLocation(s3.getType(), s3.getId());
        ShelfType s4 = AGVInterface.getFreeItemLocation();
        assert s4 != null;
        Location l4 = Area.getInstance().getLocation(s4.getType(), s4.getId());
        agv.getAGVTaskScheduler().createTask(l3, l4, ItemType.CAR_BODY);
        agv.startAGV();
        agv.stopAGV();
        agv.getSchedulerThread().join(); //finish the current tasks
        //check if deleted successfully, which indicates that at item was added at this shelf before
        assertEquals(true, Database.getInstance().deleteItem(s2.getId()));
        //check if block scheduling is working, when too many tasks in line
        blockScheduling();

    }

    public void blockScheduling() {
        boolean accepted = true;
        Random random = new Random();
        AGV agv = AGV.getInstance();
        //only add tasks to the TaskScheduler, but don't assign them to forklifts
        for (int i = 0; i < 1000; i++) {
            ShelfType s1 = AGVInterface.getItemLocation(ItemType.SCREW);
            Location l1 = Area.getInstance().getLocation(s1.getType(), s1.getId());
            Location l2 = Area.getInstance().getLocation(PRODUCTION_LINE, random.nextInt(10));
            accepted = AGV.getInstance().getAGVTaskScheduler().createTask(l1, l2, ItemType.SCREW);
            if (!accepted) break;
        }
        agv.stopAGV();
        agv.getAGVTaskScheduler();
        assertFalse(accepted);
    }

    @Test
    public void testGetLocationFromArea() {
        assertEquals(true, Area.getInstance().getLocation(FLOOR_SHELF, 1) != null);
    }

    @Test
    public void testGetRandomLocationFromArea() {
        for (int i = 0; i < 1000; i++) {
            Location l = Area.getInstance().getRandomLocation();
            assertEquals(true, l != null);
        }
    }

    @Test
    public void testStartStopAGV() {
        AGV agv = AGV.getInstance();
        agv.startAGV();
        agv.stopAGV();
        ShelfType s = AGVInterface.getItemLocation(ItemType.SCREW);
        Location l1 = Area.getInstance().getLocation(s.getType(), s.getId());
        ShelfType s2 = AGVInterface.getFreeItemLocation();
        Location l2 = Area.getInstance().getLocation(s2.getType(), s2.getId());
        //if Scheduler is stopped, no Tasks are allowed to be accepted
        boolean accepted = agv.getAGVTaskScheduler().createTask(l1, l2, ItemType.SCREW);
        assertFalse(accepted);
    }
}
