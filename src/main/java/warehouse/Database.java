package warehouse;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;

import AGV.Location;
import shared.ItemType;

/**
 * Singleton Class handling Database connection
 *
 * @author Tom Peham
 */
public class Database {

    /**
     * Static block of code for initializing the Database
     */
    static {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            getInstance();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error connecting to DB");
            e.printStackTrace();
        }
    }

    private static final String DBURL = "jdbc:derby:./warehouseDB";

    private static Database INSTANCE;

    /**
     * Getter for the singleton instance
     *
     * @return handle for database class
     * @throws SQLException
     */
    public synchronized static Database getInstance() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    /**
     * SQL Queries are prepared, so that execution during runtime is faster
     */
    private Connection conn;
    private final PreparedStatement insertWarehouseStmt;
    private final PreparedStatement deleteWarehouseStmt;
    private final PreparedStatement insertShelfStmt;
    private final PreparedStatement deleteShelfStmt;
    private final PreparedStatement numUsedShelvesStmt;
    private final PreparedStatement numShelvesStmt;
    private final PreparedStatement deleteShelvesStmt;
    private final PreparedStatement insertItemStmt;
    private final PreparedStatement deleteItemStmt;
    private final PreparedStatement freeShelfStmt;
    private final PreparedStatement itemByIDStmt;
    private final PreparedStatement idByItemTypeStmt;
    private final PreparedStatement itemStockStmt;
    private final PreparedStatement getLevelStmt;
    private final PreparedStatement itemStockWHStmt;
    private final PreparedStatement getItemAtStmt;
    
    //keep track of what id'S have been already been given out
    private final HashSet<Integer> itemsToBeRemoved = new HashSet<>();
    private final HashSet<Integer> shelvesToBeFilled = new HashSet<>();

    //list of listeners that act on changes to database
    private final ArrayBlockingQueue<DBListener> listeners;
    /**
     * Private Constructor
     *
     * @throws SQLException
     */
    private Database() throws SQLException {
        try {
            //if database exists, connect
            conn = DriverManager.getConnection(DBURL);
        } catch (Exception e) {
            //if database doesn't exist already, create
            conn = DriverManager.getConnection(DBURL + ";create=true");
            createTables(conn);
        }
        //list of sql statements needed for working with the database
        insertWarehouseStmt = conn.prepareStatement("insert into warehouses values(?,?)");
        deleteWarehouseStmt = conn.prepareStatement("delete from warehouses where warehouseID=?");
        numUsedShelvesStmt = conn.prepareStatement("select count(*) as total from shelves where warehouseID=?");
        numShelvesStmt = conn.prepareStatement("select shelfCapacity from warehouses where warehouseID=?");
        insertShelfStmt = conn.prepareStatement("insert into shelves (shelfid, warehouseID, level) values(?,?,?)");
        deleteShelfStmt = conn.prepareStatement("delete from shelves where shelfID=?");
        deleteShelvesStmt = conn.prepareStatement("delete from shelves where warehouseID=?");
        insertItemStmt = conn.prepareStatement("update shelves set type=? where shelfID=?");
        deleteItemStmt = conn.prepareStatement("update shelves set type=null where shelfID=?");
        freeShelfStmt = conn.prepareStatement("select shelfID from shelves where type is null");
        itemByIDStmt = conn.prepareStatement("select type from shelves where shelfID=?");
        idByItemTypeStmt = conn.prepareStatement("select shelfID from shelves where type=?");
        itemStockStmt = conn.prepareStatement("Select count(*) as cnt from shelves where type=?");
        itemStockWHStmt = conn.prepareStatement("Select count(*) as cnt from shelves where warehouseID=? and type is not null");
        getLevelStmt = conn.prepareStatement("Select level from shelves where shelfID=?");
        getItemAtStmt = conn.prepareStatement("Select type from shelves where shelfID=?");
        
        listeners = new ArrayBlockingQueue<DBListener>(3);
        this.addListener(new StockManager());
        new BackupManager(1).start(); //create backupmanager that makes backup every day
        
    }

    /**
     * Method for adding new warehouse to subsystem
     *
     * @param id
     * @param shelfCapacity
     * @throws SQLException
     */
    public void insertWarehouse(int id, int shelfCapacity) throws SQLException {
        synchronized (insertWarehouseStmt) {
            insertWarehouseStmt.setInt(1, id);
            insertWarehouseStmt.setInt(2, shelfCapacity);
            insertWarehouseStmt.executeUpdate();
        }
    }

    /**
     * Get item at shelf location
     * @param shelfID
     * @return ItemType of item at shelf with id shelfID or null if no item was there
     */
    ItemType getItemAt(int shelfID) {
    	synchronized (getItemAtStmt) {
            try {
                getItemAtStmt.setInt(1, shelfID);

                ResultSet res = getItemAtStmt.executeQuery();
                String ret = "";
                if (res.next()) { 
                    ret = res.getString("type");
                }
                if(ret == null) return null;
                return stringToItem(ret);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        
		}
    }
    /**
     * Method for removing warehouse from subsystem
     *
     * @param id
     * @throws SQLException
     */
    public void deleteWarehouse(int id) throws SQLException {
        synchronized (insertWarehouseStmt) {
            deleteShelvesStmt.setInt(1, id);
            deleteShelvesStmt.executeUpdate();
            deleteWarehouseStmt.setInt(1, id);
            deleteWarehouseStmt.executeUpdate();
        }
    }

    /**
     * Method for adding shelves to existing warehouse
     *
     * @param shelfID
     * @param warehouseID
     * @return
     */
    public boolean insertShelf(int shelfID, int warehouseID, int level) {
        synchronized (insertShelfStmt) {
            try {
                int freeShelves = numFreeShelves(warehouseID);
                if (freeShelves <= 0) return false;
                insertShelfStmt.setInt(1, shelfID);
                insertShelfStmt.setInt(2, warehouseID);
                insertShelfStmt.setInt(3, level);
                insertShelfStmt.executeUpdate();
            } catch (SQLException e) {
                return !deleteShelf(shelfID);
            }


            return true;
        }
    }
    
    /**
     * Get height of shelf in warehouse
     * @param shelfID
     * @return LocationType of the shelf
     */
    public Location.LocationType getLevel(int shelfID) {
        synchronized (getLevelStmt) {
            try {
                getLevelStmt.setInt(1, shelfID);

                ResultSet res = getLevelStmt.executeQuery();
                int ret = 0;
                if (res.next()) { //this is going to be optimized later on to return item with least distance
                    ret = res.getInt("level");
                }
                return levelToLoc(ret);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Utility function to transform a level (0, 1 or 2) in database to Location
     * @param level
     * @return
     */
    private Location.LocationType levelToLoc(int level) {
        switch (level) {
            case 0:
                return Location.LocationType.FLOOR_SHELF;
            case 1:
                return Location.LocationType.TOP_SHELF1;
            case 2:
                return Location.LocationType.TOP_SHELF2;
        }
        return null;
    }

    /**
     * Method for deleting shelves from warehouse
     *
     * @param shelfID
     * @return
     */
    boolean deleteShelf(int shelfID) {
        synchronized (deleteShelfStmt) {
            try {
                deleteShelfStmt.setInt(1, shelfID);
                deleteShelfStmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    /**
     * insert item into some existing shelfspace
     * @param type - ItemType to be added
     * @return boolean indicating wheter adding succeeded
     */
    public boolean insertItem(int shelfID, ItemType type) {
        synchronized (insertItemStmt) {
            try {
                insertItemStmt.setString(1, type.toString());
                insertItemStmt.setInt(2, shelfID);
                insertItemStmt.executeUpdate();
                shelvesToBeFilled.remove(shelfID);
                warehousePrint(type.toString() + " ADDED TO SHELF " + shelfID);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            fireEvent(EventType.ItemAdded, type, shelfID);
            return true;
        }
    }

    /**
     * Method for registering listeners to database changes
     *
     * @param l - DBListener to add
     */
    void addListener(DBListener l) {
		listeners.add(l);
    }

    /**
     * Method for deleting item from warehouse
     * @return - boolean indicating if deletion was successful
     */
    public boolean deleteItem(int shelfID) {
        synchronized (deleteItemStmt) {

            String itemType = "";
            try {
                itemByIDStmt.setInt(1, shelfID);
                ResultSet res = itemByIDStmt.executeQuery();
                if (res.next()) {
                    itemType = res.getString("type");
                }
                deleteItemStmt.setInt(1, shelfID);
                deleteItemStmt.executeUpdate();
                warehousePrint(itemType + " DELETED FROM SHELF " + shelfID);

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            if (itemsToBeRemoved.contains(shelfID)) itemsToBeRemoved.remove(shelfID);

            fireEvent(EventType.ItemRemoved, stringToItem(itemType), shelfID);
            return true;
        }
    }

    /**
     * Method for finding the id of the location of an item with type type
     *
     * @param type the type of item to search for
     * @return id of shelfplace if successful, -1 otherwise
     * @throws SQLException
     */
    public int itemByType(String type) {
        synchronized (idByItemTypeStmt) {
        	try {
	            idByItemTypeStmt.setString(1, type);
	            ResultSet res = idByItemTypeStmt.executeQuery();
	            int ret = -1;
	            while (res.next()) {
	                ret = res.getInt("shelfid");
	                if (!itemsToBeRemoved.contains(ret)) {
	                    itemsToBeRemoved.add(ret); //items are always queried before removal
	                    return ret; //only items that haven't been queried get removed
	                }
	            }
	            return ret;
        	}catch (SQLException e) {
        		return -1;
        	}
        }
    }

    /**
     * Method to get number of items in warehouse that are of type type
     *
     * @param type - type of item to count
     * @return the number of items in stock of this particular item type, -1 if error
     */
    public int itemsInStock(ItemType type) {
        synchronized (itemStockStmt) {
            try {
				itemStockStmt.setString(1, type.toString());
	            ResultSet res = itemStockStmt.executeQuery();
	            int i = 0;
	            if (res.next()) i = res.getInt("cnt");
	            return i;
			} catch (SQLException e) {
				return -1;
			}
        }
    }
    
    /**
     * Method to get number of items in specific warehouse
     * @param warehouseID - id of warehouse to query
     * @return number of items in given warehouse, -1 if error occured
     * @throws SQLException
     */
    public int itemsInWarehouse(int warehouseID) {
    	synchronized (itemStockWHStmt) {
            try {
				itemStockWHStmt.setInt(1, warehouseID);
	            ResultSet res = itemStockWHStmt.executeQuery();
	            int i = 0;
	            if (res.next()) i = res.getInt("cnt");
	            return i;
			} catch (SQLException e) {
				return -1;
			}
        }
    }

    /**
     * Create tables of Database
     * @param conn - connection to database
     */
    private static void createTables(Connection conn) {
        String createWarehouseTable = "create table Warehouses (warehouseID integer primary key not Null," +
                "shelfCapacity integer)";
        String createShelfTable = "create table Shelves (shelfID integer primary key not Null," +
                "warehouseID integer references warehouses(warehouseID) not Null, type varchar(32), level integer)"; //level can be 0 1 or 2
        try {
            PreparedStatement createWarehouseTableStmt = conn.prepareStatement(createWarehouseTable);
            createWarehouseTableStmt.executeUpdate();

            PreparedStatement createShelfTableStmt = conn.prepareStatement(createShelfTable);
            createShelfTableStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Utility function for TESTING
     * @throws SQLException
     */
    public void initTestDB() throws SQLException {
    	Database db = this;
    	db.insertWarehouse(0, 1000);
		for(int i = 0; i < 60;) {
			db.insertShelf(i++, 0, 0); //Floor shelves
			db.insertShelf(i++, 0, 1); // middle shelves
			db.insertShelf(i++, 0, 2); //top shelf
		}
		int n = 5;
		for(int i = 0; i < n; i++) {
			db.insertItem(i, ItemType.SCREW);
			db.insertItem(i+n, ItemType.BLUE_PAINT);
			db.insertItem(i+n*2, ItemType.CAR_BODY);
			db.insertItem(i+n*3, ItemType.RED_PAINT);
			db.insertItem(i+n*4, ItemType.REMOTE);
			db.insertItem(i+n*5, ItemType.WHEEL);
		}
    }

    /**
     * Delete utility Database
     * @throws SQLException
     */
    public void deleteTestDB() throws SQLException {
        this.deleteWarehouse(0);
    }

    /**
     * Notify listeners of changes
     * @param eType - type of event
     * @param itemType
     * @param id - id of shelf where event occured
     */
    private void fireEvent(EventType eType, ItemType itemType, int id) {
        DBEvent e = new DBEvent(eType, itemType, id);
    	for (DBListener l : listeners) l.notifyEvent(e);
    }

    /**
     * find location of a free shelf
     * @return id of shelf to be queried, -1 if error or warehouse full
     * @throws SQLException
     */
    int getFreeShelf() {
        synchronized (freeShelfStmt) {
            ResultSet res;
			try {
				res = freeShelfStmt.executeQuery();
	            while (res.next()) {
	                int ret = res.getInt("shelfid");
	                if (!shelvesToBeFilled.contains(ret)) {
	                    shelvesToBeFilled.add(ret);
	                    return ret;
	                }
	            }
	            return -1;
			} catch (SQLException e) {
				return -1;
			}
        }
    }

    /**
     * Get number of total free shelves in warehouse
     * @param warehouseID - id of warehouse to be searched in
     * @return number of free shelves, -1 if error or warehouse doesn't exist
     * @throws SQLException
     */
    private int numFreeShelves(int warehouseID) {
        synchronized (numShelvesStmt) {
            try {
				numShelvesStmt.setInt(1, warehouseID);
	            numUsedShelvesStmt.setInt(1, warehouseID);
	            ResultSet res1 = numUsedShelvesStmt.executeQuery();
	            int numUsed = 0;
	            if (res1.next()) {
	                numUsed = res1.getInt("total");
	            }
	            res1.close();
	            ResultSet res2 = numShelvesStmt.executeQuery();
	            int numTotal = 0;
	            if (res2.next()) numTotal = res2.getInt(1);
	            return numTotal - numUsed;
			} catch (SQLException e) {
				return -1;
			}
        }
    }

    /**
     * Utility function for converting a string to Object of class ItemType
     * This needs to be done because items are stored as strings in db
     * @param s - string to be converted
     * @return ItemType associated with String s, null if no match
     */
    private ItemType stringToItem(String s) {
        switch (s) {
            case "SCREW":
                return ItemType.SCREW;
            case "WHEEL":
                return ItemType.WHEEL;
            case "BLUE_PAINT":
                return ItemType.BLUE_PAINT;
            case "CAR_BODY":
                return ItemType.CAR_BODY;
            case "FINISHED_BLUE_CAR":
                return ItemType.FINISHED_BLUE_CAR;
            case "FINISHED_RED_CAR":
                return ItemType.FINISHED_RED_CAR;
            case "RED_PAINT":
                return ItemType.RED_PAINT;
            case "REMOTE":
                return ItemType.REMOTE;
        }
        return null;
    }
/**
     * Utility function for writing status messages to stdout
     * @param msg - Message to be printed
     */
    static void warehousePrint(String msg) {
        System.out.println("\n------WAREHOUSE MESSAGE BEGIN------\n" + msg + "\n------WAREHOUSE MESSAGE END------\n");
    }
}
