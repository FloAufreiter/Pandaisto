package warehouse;

import java.sql.SQLException;
import shared.ItemType;

/**
 * Abstract Class for other systems to send messages to warehouse subsystem
 * @author Tom Peham
 *
 */
public abstract class MessagingInterface {

	/**
	 * Method for getting item from database
	 * @param type the type of item to be searched for
	 * @return int representing item location in warehouse, -1 otherwise
	 */
	public int getItemLocation(ItemType type) {
		try {
			return Database.getInstance().itemByType(type.toString());
		} catch (SQLException e) {
			// TODO properly handle this
			e.printStackTrace();
		}
		return -1;
	}
	
}
