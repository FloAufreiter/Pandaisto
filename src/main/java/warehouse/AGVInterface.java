package warehouse;

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
	public boolean confirmItemRemoval(int shelfPlaceID) {
		try {
			Database.getInstance().deleteItem(shelfPlaceID);
			return true;
		} catch (SQLException e) {
			// TODO properly handle exception
			e.printStackTrace();
			return false;
		}
	}
}
