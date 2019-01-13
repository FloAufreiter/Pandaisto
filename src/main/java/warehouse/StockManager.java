package warehouse;

import java.sql.SQLException;
import java.util.HashMap;

import Monitoring.Monitor;
import shared.ItemType;

/**
 * Class handling warehouse stock. This class is responsible for issuing new orders 
 * and notifying monitoring subsystem if warehouse gets full
 * @author tom
 *
 */
public class StockManager implements DBListener{
	
	//In actual project this would be specified by customer
	private final HashMap <ItemType, Integer> criticalStock = new HashMap<>();
	private final HashMap <ItemType, Integer> reorderAmount = new HashMap<>();
	private final HashMap <ItemType, Boolean> deliveryPending = new HashMap<>();
	
	StockManager() {
		for(ItemType t: ItemType.values()) {
			criticalStock.put(t, 10);
			reorderAmount.put(t, 10);
			deliveryPending.put(t, false);
		}
	}
	
	public void requestNewOrder(ItemType type) {
		Database.warehousePrint(reorderAmount.get(type) + " " +type + " REORDERED");
		Monitor.getInstance().orderComponents(type, reorderAmount.get(type));	
	}

	@Override
	public void notifyEvent(DBEvent e) {
		if(e.eType.equals(EventType.ItemRemoved)) {
			try {
				System.out.println(Database.getInstance().itemsInStock(e.itemType));
				if(Database.getInstance().itemsInStock(e.itemType) < criticalStock.get(e.itemType) &&
						!deliveryPending.get(e.itemType)) {
					deliveryPending.put(e.itemType, true);
					requestNewOrder(e.itemType);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if(e.eType.equals(EventType.ItemAdded)) {
			try {
				if(Database.getInstance().itemsInStock(e.itemType) >= criticalStock.get(e.itemType)) {
					deliveryPending.put(e.itemType, false);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	} 
}
