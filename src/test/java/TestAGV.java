import AGV.AGV;
import AGV.Area;
import AGV.Location;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import shared.ItemType;
import warehouse.AGVInterface;
import warehouse.Database;
import warehouse.ShelfType;

import java.sql.SQLException;
import java.util.Random;

import static AGV.Location.LocationType.FLOOR_SHELF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * Tests for AGV
 * REMARK: Tests need to be executed on their own,
 * because of all threads need to finish and the interaction with the database,
 * which needs to be reinitialized and deleted after each test
 */
public class TestAGV {

    @Before
    public void initDB() throws SQLException {
        TestUtils.initDB();
    }

    @After
    public void deleteDB() {
        TestUtils.deleteDB();
    }

    @Test
    public void testCommunicationAGVWarehouse() throws SQLException, InterruptedException {
        AGV agv = AGV.getInstance();
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
    public void blockScheduling() {
        boolean excepted = true;
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            Location l1 = Area.getInstance().getRandomLocation();
            Location l2 = Area.getInstance().getRandomLocation();
            ItemType[] values = ItemType.values();
            excepted = AGV.getInstance().getAGVTaskScheduler().createTask(l1, l2, values[r.nextInt(values.length)]);
            if (!excepted) break;
        }
        assertFalse(excepted);
    }

    @Test
    public void testReassembleWarehouse() throws SQLException, InterruptedException {
        AGV agv = AGV.getInstance();
        agv.startAGV();
        ShelfType s = AGVInterface.getItemLocation(ItemType.CAR_BODY);
        assert s != null;
        Location l1 = Area.getInstance().getLocation(s.getType(), s.getId());
        ShelfType s2 = AGVInterface.getFreeItemLocation();
        assert s2 != null;
        Location l2 = Area.getInstance().getLocation(s2.getType(), s2.getId());
        agv.getAGVTaskScheduler().createTask(l1, l2, ItemType.CAR_BODY);
        agv.stopAGV();
        agv.getSchedulerThread().join();

        //check if deleted successfully, which indicates that at item was added at this shelf before
        assertEquals(true, Database.getInstance().deleteItem(s2.getId()));
    }

    @Test
    public void testGetLocationFromArea() {
        assertEquals(true, Area.getInstance().getLocation(FLOOR_SHELF, 1) != null);
    }
}
