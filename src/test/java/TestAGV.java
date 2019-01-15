import AGV.AGV;
import AGV.Area;
import AGV.Location;
import Monitoring.Monitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import shared.ItemType;
import warehouse.AGVInterface;
import warehouse.Database;
import warehouse.ShelfType;

import java.sql.SQLException;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
    public void testCommunicationAGVWithRobotArms() throws SQLException, InterruptedException {
        Monitor.getInstance();
        AGV agv = AGV.getInstance();
        ShelfType s = AGVInterface.getItemLocation(ItemType.CAR_BODY);
        assert s != null;
        Location l1 = Area.getInstance().getLocation(s.getType(), s.getId());
        Location r0 = Area.getInstance().getLocation(Location.LocationType.PRODUCTION_LINE, 0);
        agv.getAGVTaskScheduler().createTask(l1, r0, ItemType.CAR_BODY);
        ShelfType s2 = AGVInterface.getItemLocation(ItemType.RED_PAINT);
        assert s2 != null;
        Location l2 = Area.getInstance().getLocation(s2.getType(), s2.getId());
        Location r4 = Area.getInstance().getLocation(Location.LocationType.PRODUCTION_LINE, 5);
        agv.getAGVTaskScheduler().createTask(l2, r4, ItemType.RED_PAINT);
        agv.stopAGV();
        agv.getSchedulerThread().join();
        //check if Database is consistent
        assertEquals(4, Database.getInstance().itemsInStock(ItemType.CAR_BODY));
        assertEquals(4, Database.getInstance().itemsInStock(ItemType.RED_PAINT));
        Database.getInstance().deleteTestDB();
    }
}
