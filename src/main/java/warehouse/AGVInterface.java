package warehouse;

import shared.ItemContainer;
import shared.ItemType;

import java.sql.SQLException;

/**
 * Interface for AGV Subsystem to work with Database
 * @author Tom Peham
 *
 */
public class AGVInterface extends MessagingInterface {

	/**
	 * Method called by AGV-subsystem to confirm removal of an item
	 * @param shelfPlaceID - the shelfplace where the item is removed
	 * @return boolean stating if item removal was completed
	 */
	public static boolean confirmItemRemoval(int shelfPlaceID) {
		try {
			System.out.println("REMOVING");
			Database.getInstance().deleteItem(shelfPlaceID);
			System.out.println("removed");
			return true;
		} catch (SQLException e) {
			// TODO properly handle exception
			e.printStackTrace();
			return false;
		}
	}

	public static void confirmItemAdded(int shelfPlaceID, ItemType type) {
		try {
			Database.getInstance().insertItem(shelfPlaceID, type);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
