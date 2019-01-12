package warehouse;

import java.sql.*;
import java.util.ArrayList;

import AGV.Location;
import shared.ItemType;

/**
 * Singleton Class handling Database connection
 * @author Tom Peham
 *
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
	 * @return handle for database class
	 * @throws SQLException
	 */
	public synchronized static Database getInstance() throws SQLException {
		if(INSTANCE == null) {
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
    private final PreparedStatement addItemToDelivStmt;
    private final PreparedStatement checkShelfEmptyStmt;
    private final PreparedStatement insertShelfSpaceStmt;
    private final PreparedStatement freeShelfSpaceStmt;
    private final PreparedStatement itemByIDStmt;
    private final PreparedStatement idByItemTypeStmt;
    private final PreparedStatement itemStockStmt;
    private final PreparedStatement getLevelStmt;
    
    //list of listeners that act on changes to database
    private final ArrayList<DBListener> listeners;
	PathOrganizer p;
    /**
     * Private Constructor
     * @throws SQLException
     */
    private Database() throws SQLException {
    	try {
    		//if database exists, connect
    		conn = DriverManager.getConnection(DBURL);
    	} catch(Exception e) {
    		//if database doesn't exist already, create
    		conn = DriverManager.getConnection(DBURL + ";create=true");
    		createTables(conn);
    	}
    	//list of sql statements needed for working with the database
		insertWarehouseStmt = conn.prepareStatement("insert into warehouses values(?,?)");
		deleteWarehouseStmt = conn.prepareStatement("delete from warehouses where warehouseID=?");
		numUsedShelvesStmt = conn.prepareStatement("select count(*) as total from shelves where warehouseID=?");
		numShelvesStmt = conn.prepareStatement("select shelfCapacity from warehouses where warehouseID=?");
		insertShelfStmt = conn.prepareStatement("insert into shelves values(?,?,?,?,?)");
		deleteShelfStmt = conn.prepareStatement("delete from shelves where shelfID=?");
    	deleteShelvesStmt = conn.prepareStatement("delete from shelves where warehouseID=?");
    	insertItemStmt = conn.prepareStatement("update shelfspaces set type=? where shelfspaceID=?");
    	deleteItemStmt = conn.prepareStatement("update shelfspaces set type=null where shelfspaceID=?");
    	addItemToDelivStmt = conn.prepareStatement("update shelfspaces set deliveryID=? where shelfspaceID=?");
    	insertShelfSpaceStmt = conn.prepareStatement("insert into shelfspaces (shelfSpaceID, shelfID) values(?,?)");
    	checkShelfEmptyStmt = conn.prepareStatement("select count(*) as cnt from shelfspaces where shelfID=? and type is not null");
    	freeShelfSpaceStmt = conn.prepareStatement("select shelfspaceID from shelfspaces where type is null");
    	itemByIDStmt = conn.prepareStatement("select type from shelfspaces where shelfID=?");
    	idByItemTypeStmt = conn.prepareStatement("select shelfspaceID from shelfspaces where type=?");
    	itemStockStmt = conn.prepareStatement("Select count(*) as cnt from shelfspaces where type=?");
		getLevelStmt = conn.prepareStatement("Select level from shelves where shelfID=?");

    	listeners = new ArrayList<>();
    	p = new PathOrganizer();
    	this.addListener(p);
    	new BackupManager(1, System.getProperty("user.dir"), "/home//tom/eclipse-workspace/sweng").start();
    	
    }
    
    /**
     * Method for adding new warehouse to subsystem
     * @param id
     * @param shelfCapacity
     * @throws SQLException
     */
    public void insertWarehouse(int id, int shelfCapacity) throws SQLException {
		synchronized(insertWarehouseStmt) {
			insertWarehouseStmt.setInt(1, id);
			insertWarehouseStmt.setInt(2, shelfCapacity);
			insertWarehouseStmt.executeUpdate();
		}
    }
    
    /**
     * Method for removing warehouse from subsystem
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
     * @param shelfID
     * @param warehouseID
     * @return
     */
    public boolean insertShelf(int shelfID, int warehouseID, int level) {
    	synchronized (insertShelfStmt) {
    		try {
				int freeShelves = numFreeShelves(warehouseID);
				if(freeShelves <= 0) return false;
				insertShelfStmt.setInt(1, shelfID);
				insertShelfStmt.setInt(2, warehouseID);
				insertShelfStmt.setInt(3, 1000);
				insertShelfStmt.setInt(4, 1000);
				insertShelfStmt.setInt(5, level);
				insertShelfStmt.executeUpdate();

    		} catch(SQLException e) {
				return ! deleteShelf(shelfID);
    		}
    		
			
			return true;
		}
    }

    public Location.LocationType getLevel(int shelfID) {
    	synchronized (getLevelStmt) {
    		try{
    			getLevelStmt.setInt(1, shelfID);

				ResultSet res = getLevelStmt.executeQuery();
				int ret = 0;
				if(res.next()) { //this is going to be optimized later on to return item with least distance
					ret = res.getInt("level");
				}
				return levelToLoc(ret);
			} catch(SQLException e) {
    			e.printStackTrace();
			}
			return null;
		}
	}

	private Location.LocationType levelToLoc(int level) {
    	switch(level) {
			case 0: return Location.LocationType.FLOORSHELF;
			case 1: return Location.LocationType.TOPSHELF1;
			case 2: return Location.LocationType.TOPSHELF2;
		}
		return null;
	}

    public boolean addShelfSpace(int shelfID, int shelfSpaceID) {
    	synchronized (insertShelfSpaceStmt) {
			try {
				insertShelfSpaceStmt.setInt(1, shelfSpaceID);
				insertShelfSpaceStmt.setInt(2, shelfID);
				insertShelfSpaceStmt.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
    
    /**
     * Method for deleting shelves from warehouse
     * @param shelfID
     * @return
     */
    public boolean deleteShelf(int shelfID) {
    	synchronized (deleteShelfStmt) {
    		try {
    			if(shelfEmpty(shelfID)) {
		    		deleteShelfStmt.setInt(1, shelfID);
		    		deleteShelfStmt.executeUpdate();
    			} else return false;
    		} catch(SQLException e) {
    			return false;
    		} 
    		return true;
		}
    }
    
    /**
     * insert item into some existing shelfspace
     * TODO: Organizer classes will handle arrangement of items in efficient way
     * @param type
     * @return
     */
    public boolean insertItem(int shelfSpaceID, ItemType type) {
    	try {
    		System.out.println("ShelfID:"+shelfSpaceID);
    		if(shelfSpaceID < 0) return false; //TODO: alarm delivery organizer (in general introduce events)
    		insertItemStmt.setString(1, type.toString());
	    	insertItemStmt.setInt(2, shelfSpaceID);
	    	insertItemStmt.executeUpdate();
    	} catch(SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    	fireEvent(EventType.ItemAdded, type);
    	return true;
    }
    
    /**
     * Method for registering listeners to database changes
     * @param l
     */
    public void addListener(DBListener l) {
    	listeners.add(l);
    }
    
    /**
     * Method for deleting item from warehouse
     * @param shelfSpaceID
     * @return
     */
    public boolean deleteItem(int shelfSpaceID) {
		synchronized (deleteItemStmt) {
			String itemType = "";
	    	try {
	    		itemByIDStmt.setInt(1, shelfSpaceID);
	    		ResultSet res = itemByIDStmt.executeQuery();
	    		if(res.next()) {
	    			itemType = res.getString("type");
	    		}
	    		deleteItemStmt.setInt(1, shelfSpaceID);
	    		deleteItemStmt.executeUpdate();
	    	} catch(SQLException e) {
	    		return false;
	    	}
	    	fireEvent(EventType.ItemRemoved, stringToItem(itemType));
	    	return true;
		}
    }
    
    /**
     * Method for adding items to delivery
     * TODO: Needs to be revamped once the deliveryorganizer class is being implemented
     * @param deliveryID
     * @param type
     * @return
     */
    public boolean addItemToDelivery(int deliveryID, ItemType type) {
    	try {
    		addItemToDelivStmt.setInt(1, deliveryID);
//    		addItemToDelivStmt.setInt(2, itemID);
//    		addItemToDelivStmt.executeUpdate();
    	} catch(SQLException e) {
    		return false;
    	}
    	return true;
    }
	
    
    /**
     * Method for finding the id of the location of an item with type type
     * @param type the type of item to search for
     * @return id of shelfplace if successful, -1 otherwise
     * @throws SQLException 
     */
    public int itemByType(String type) throws SQLException {
    	synchronized (idByItemTypeStmt) {
    		idByItemTypeStmt.setString(1, type);
			ResultSet res = idByItemTypeStmt.executeQuery();
			int ret = -1;
			if(res.next()) { //this is going to be optimized later on to return item with least distance
				ret = res.getInt("shelfplaceid");
			}
			return ret;
		}
    }
    
    /**
     * Method to get number of items in warehouse that are of type type
     * @param type - type of item to count
     * @return the number of items in stock of this particular item type
     * @throws SQLException
     */
    public int itemsInStock(ItemType type) throws SQLException {
    	synchronized (itemStockStmt) {
    		itemStockStmt.setString(1, type.toString());
    		ResultSet res = itemStockStmt.executeQuery();
    		int i = 0;
    		if(res.next()) i = res.getInt("cnt");
    		return i;
    	}
    }
 
    private static void createTables(Connection conn) {
    	String createWarehouseTable = "create table Warehouses (warehouseID integer primary key not Null," + 
    			"shelfCapacity integer)";
    	String createShelfTable ="create table Shelves (shelfID integer primary key not Null," +
    			"warehouseID integer references warehouses(warehouseID) not Null, itemCapacity integer, distance integer, level integer)"; //level can be 0 1 or 2
    	String createShelfSpaceTable = "create table shelfSpaces (shelfSpaceID integer primary key not null,"+
    			"shelfID integer references shelves(shelfID) not null, type varchar(32) default null, deliveryID integer references deliveries(deliveryID))";
    	String createDeliveryTable = "create table Deliveries (deliveryID integer primary key not null,"+
    			"loadingDockID integer references loadingdocks(loadingdockid) not null, expectedDeliveryDate Date)";
    	String createLoadingDockTable = "create table LoadingDocks (loadingDockID integer primary key not null)";
    	String createDistancesTable = "create table LoadingDockDistances (loadingDockID integer references LoadingDocks(loadingDockID),"+
    	"shelfID integer references shelves(shelfID), distance integer)";
    	try {
    		PreparedStatement createWarehouseTableStmt = conn.prepareStatement(createWarehouseTable);
    		createWarehouseTableStmt.executeUpdate();
    		
    		PreparedStatement createShelfTableStmt = conn.prepareStatement(createShelfTable);
    		createShelfTableStmt.executeUpdate();
    		
    		PreparedStatement createLoadingDockTableStmt = conn.prepareStatement(createLoadingDockTable);
    		createLoadingDockTableStmt.executeUpdate();
    		
    		PreparedStatement createDeliveryTableStmt = conn.prepareStatement(createDeliveryTable);
    		createDeliveryTableStmt.executeUpdate();

    		PreparedStatement createShelfSpaceTableStmt = conn.prepareStatement(createShelfSpaceTable);
    		createShelfSpaceTableStmt.executeUpdate();
    		
    		PreparedStatement createDistancesStmt = conn.prepareStatement(createDistancesTable);
    		createDistancesStmt.executeUpdate();
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
    	
    	}

    private void fireEvent(EventType eType, ItemType itemType) {
    	
    	DBEvent e = new DBEvent(eType, itemType);
    	for(DBListener l: listeners) l.notifyEvent(e);
    }
    
    int getFreeShelfSpace() throws SQLException {
		synchronized (freeShelfSpaceStmt) {
			ResultSet res = freeShelfSpaceStmt.executeQuery();
			if(res.next()) {
				return res.getInt("shelfspaceid");
			}
		}
		return -1;
	}

    
    private int numFreeShelves(int warehouseID) throws SQLException {
    	synchronized (numShelvesStmt) {
    		numShelvesStmt.setInt(1, warehouseID);
    		numUsedShelvesStmt.setInt(1, warehouseID);
    		ResultSet res1 = numUsedShelvesStmt.executeQuery();
    		int numUsed = 0;
    		if(res1.next()) {
    			numUsed = res1.getInt("total");
    		}
    		res1.close();
    		ResultSet res2 = numShelvesStmt.executeQuery();
    		int numTotal = 0;
    		if(res2.next()) numTotal = res2.getInt(1);
    		return numTotal-numUsed;
		}
    }
    
    private boolean shelfEmpty(int shelfID) throws SQLException {
    	synchronized (checkShelfEmptyStmt) {
    		checkShelfEmptyStmt.setInt(1, shelfID);
    		ResultSet res = checkShelfEmptyStmt.executeQuery();
    		if(res.next()) {
    			return res.getInt("cnt") <= 0;
    		}
		}
		return false;
    }
    
    private ItemType stringToItem(String s) {
    	switch(s) {
    	case "SCREW": return ItemType.SCREW;
    	case "WHEEL": return ItemType.WHEEL; 
    	case "BLUE_PAINT": return ItemType.BLUE_PAINT; 
    	case "CAR_BODY": return ItemType.CAR_BODY; 
    	case "FINISHED_BLUE_CAR": return ItemType.FINISHED_BLUE_CAR; 
    	case "FINISHED_RED_CAR": return ItemType.FINISHED_RED_CAR; 
    	case "RED_PAINT": return ItemType.RED_PAINT; 
    	case "REMOTE": return ItemType.REMOTE;
    	}
    	return null;
    }
    	
//    public static void main(String[] Args) {
//    	Database db = null;
//    	try {
//    		db = Database.getInstance();
//    	} catch(Exception e) {
//    		e.printStackTrace();
//    	}
//    	db.addListener(e -> {
//    		if(e.eType == EventType.ItemAdded) System.out.println(e.itemType+" added");
//    		if(e.eType == EventType.ItemRemoved) System.out.println(e.itemType+ " removed");
//    	});
//    	try {
//			db.insertWarehouse(0, 10000);
//			db.insertShelf(0, 0, 100,10);
//			db.insertShelf(1,0,10,30);
//			db.insertItem(ItemType.SCREW);
//			db.insertItem(ItemType.SCREW);
//			db.insertItem(ItemType.WHEEL);
//			db.deleteItem(0);
////			db.deleteWarehouse(0);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
}
