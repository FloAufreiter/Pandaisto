package warehouse;

import java.sql.SQLException;
import shared.ItemType;

/**
 * Interface for Monitoring Subsystem to get information like status report from the subsystem
 * @author tom
 *
 */
public class MonitoringInterface extends MessagingInterface {
	
	/**
	 * Method to get number of items in stock of type type
	 * @param type - type of item to count
	 * @return number of items in stock, -1 in case of failure
	 */
	public int getItemStock(ItemType type) {
		try {
			return Database.getInstance().itemsInStock(type);
		} catch (SQLException e) {
			// TODO properly handle this
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Method needed by monitoring system 
	 * TODO: Monitoring system still needs to clarify what it needs as status report
	 * @return String representing status report
	 */
	public String getStatus() {
		return "";
	}
}
