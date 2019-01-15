import AGV.AGV;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import shared.ItemType;
import warehouse.Database;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class TestSystem {

    @Before
    public void initDB() throws SQLException {
        TestUtils.initDB();
    }

    @After
    public void deleteDB() {
        TestUtils.deleteDB();
    }

    @Test
    public void testItemReorder() throws SQLException, InterruptedException {
        Database db = Database.getInstance();
        assertEquals(5, db.itemsInStock(ItemType.SCREW));
        for(int i = 0; i < 3; i++) db.deleteItem(i);
        AGV.getInstance().stopAGV();
        AGV.getInstance().getSchedulerThread().join();
        assertEquals(5, db.itemsInStock(ItemType.SCREW));
        db.deleteTestDB();
    }
}
