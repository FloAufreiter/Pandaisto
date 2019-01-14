import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

import shared.ItemType;
import warehouse.Database;

public class TestWarehouse {


	@Test 
	public void testInitDB() throws SQLException, InterruptedException {
		Database db = Database.getInstance();
		db.initTestDB();
		assertEquals(5, db.itemsInStock(ItemType.BLUE_PAINT));
		db.deleteTestDB();
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
        assertEquals(5, Database.getInstance().itemsInStock(ItemType.CAR_BODY));
        Database.getInstance().deleteItem(50000);
        assertEquals(4, Database.getInstance().itemsInStock(ItemType.CAR_BODY));
        Database.getInstance().deleteWarehouse(4);
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
    public void checkShelf11() throws SQLException {
    	Database db = Database.getInstance();
    	db.initTestDB();
    	db.deleteItem(11);
    	assertEquals(4, db.itemsInStock(ItemType.CAR_BODY));
    	db.deleteTestDB();
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
}
