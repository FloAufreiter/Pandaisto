package warehouse;

import AGV.Location;
import shared.ItemType;

import java.sql.SQLException;

/**
 * Abstract Class for other systems to send messages to warehouse subsystem
 * @author Tom Peham
 *
 */
public abstract class MessagingInterface {

	/**
	 * Method for getting item from database
	 * @return int representing item location in warehouse, -1 otherwise
	 */
	public static ShelfType getFreeItemLocation() {
		try {
			int id = Database.getInstance().getFreeShelf();
			if(id < 0) return null;
			Location.LocationType level = Database.getInstance().getLevel(id);
			ShelfType s = new ShelfType(id, level);
			return s;
		} catch (SQLException e) {
			
		}
		return null;
	}

	public static ShelfType getItemLocation(ItemType type) {
		try {
			int id = Database.getInstance().itemByType(type.toString());
			if(id < 0) return null;
			Location.LocationType level = Database.getInstance().getLevel(id);
			return new ShelfType(id, level);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
