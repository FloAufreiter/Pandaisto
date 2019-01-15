package warehouse;

import shared.ItemType;

import java.sql.SQLException;

/**
 * Interface for Monitoring Subsystem to get information like status report from the subsystem
 *
 * @author tom
 */
public class MonitoringInterface extends MessagingInterface {
	
    /**
     * Method to get number of items in stock of type type
     *
     * @param type - type of item to count
     * @return number of items in stock, -1 in case of failure
     */
    public static int getItemStock(ItemType type) {
        try {
            return Database.getInstance().itemsInStock(type);
        } catch (SQLException e) {
            // TODO properly handle this
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Method for monitor to start display of items
     * @return
     */
    public static void startGUI() {
    	new WarehouseGUI().openGui();
    }
}
