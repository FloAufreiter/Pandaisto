package warehouse;

import java.sql.SQLException;

import AGV.Location;
import shared.ItemContainer;
import shared.ItemType;

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
	public static ShelfType getFreeItemLocation(ItemContainer container) {
		try {
			int weight = container.getAmount() * container.getItemType().getweight();

			int id = Database.getInstance().getFreeShelf();
			Location.LocationType level = Database.getInstance().getLevel(id);
			return new ShelfType(id, level);
		} catch (SQLException e) {
			// TODO properly handle this
			e.printStackTrace();
		}
		return null;
	}

	public static ShelfType getItemLocation(ItemType type) {
		try {
			int id = Database.getInstance().itemByType(type.toString());
			Location.LocationType level = Database.getInstance().getLevel(id);
			return new ShelfType(id, level);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
