package warehouse;

import Monitoring.Monitor;
import shared.ItemType;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Class handling warehouse stock. This class is responsible for issuing new orders 
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
			criticalStock.put(t, 3);
			reorderAmount.put(t, 3);
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
						!deliveryPending.get(e.itemType) && ! (e.itemType.equals(ItemType.FINISHED_BLUE_CAR) || e.itemType.equals(ItemType.FINISHED_RED_CAR))) {
					deliveryPending.put(e.itemType, true);
					requestNewOrder(e.itemType);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else if(e.eType.equals(EventType.ItemAdded)) {
			try {
				if(Database.getInstance().itemsInStock(e.itemType) >= criticalStock.get(e.itemType)) {
					deliveryPending.put(e.itemType, false);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	} 
}
