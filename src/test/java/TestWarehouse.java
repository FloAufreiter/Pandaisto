import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import shared.ItemType;
import warehouse.BackupManager;
import warehouse.Database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class TestWarehouse {

	@Before
	public void initDB() throws SQLException {
		try {
			Database.getInstance().initTestDB();
		} catch (SQLException e) {
			try {
				Database.getInstance().deleteTestDB();
				Database.getInstance().initTestDB();
			} catch (SQLException e1) {
				throw e1;
			}
		}
	}

	@After
	public void deleteDB() {
		try {
			Database.getInstance().deleteTestDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test 
	public void testInitDB() throws SQLException, InterruptedException {
		Database db = Database.getInstance();
		assertEquals(5, db.itemsInStock(ItemType.BLUE_PAINT));
	}
	

    @Test
    public void addSomethingToDB() throws SQLException {
    	
        Database.getInstance().insertWarehouse(4, 100);
        Database.getInstance().insertShelf(50000, 4, 0);
        Database.getInstance().insertShelf(50001, 4, 0);
        Database.getInstance().insertShelf(50002, 4, 0);
        Database.getInstance().insertShelf(50003, 4, 0);
        Database.getInstance().insertShelf(50004, 4, 0);
        Database.getInstance().insertItem(50000, ItemType.CAR_BODY);
        Database.getInstance().insertItem(50001, ItemType.CAR_BODY);
        Database.getInstance().insertItem(50002, ItemType.CAR_BODY);
        Database.getInstance().insertItem(50003, ItemType.CAR_BODY);
        Database.getInstance().insertItem(50004, ItemType.CAR_BODY);

        assertEquals(5, Database.getInstance().itemsInWarehouse(4));

        Database.getInstance().deleteWarehouse(4);
    }
    

    @Test
    public void testItemNotRemovedTwice() throws SQLException {
    	Database db = Database.getInstance();
    	db.deleteWarehouse(10);
    	db.deleteTestDB(); // warehouse needs to be empty for test to work
    	db.insertWarehouse(10, 100);
    	db.insertShelf(1000, 10, 1);
    	db.insertItem(1000, ItemType.BLUE_PAINT);
    	int id1 = db.itemByType(ItemType.BLUE_PAINT.toString());
    	int id2 = db.itemByType(ItemType.BLUE_PAINT.toString());
    	assertEquals(-1, id2); //id for same item cant be given out twice
    	db.deleteWarehouse(10); //cleanup

    }

    @Test
    public void checkShelfTest() throws SQLException {
    	Database db = Database.getInstance();
    	db.deleteItem(11);
    	assertEquals(4, db.itemsInStock(ItemType.CAR_BODY));
    }

    @Test
	public void checkItemStockTest() throws SQLException {
		Database db = Database.getInstance();
		db.deleteWarehouse(10); //delete in case left over from other test or created by someone else
		db.insertWarehouse(10, 100);
		for(int i = 0; i < 100; i ++) db.insertShelf(i+1000, 10, 0);
		for(int i = 0; i < 100; i++) db.insertItem(i+1000, ItemType.RED_PAINT);
		assertEquals(100, db.itemsInWarehouse(10));
		if(! db.deleteItem(1005)) throw new SQLException();
		assertEquals(99, db.itemsInWarehouse(10));
		db.deleteWarehouse(10);
		assertEquals(0, db.itemsInWarehouse(10));
	}
    
    @Test 
    public void testBackup() throws InterruptedException {
    	boolean fileExists = Files.exists(Paths.get("./DBBACKUP")); //backup should already have been done at startup
    	assertEquals(true, fileExists);
    }
}
