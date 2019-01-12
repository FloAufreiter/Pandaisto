package warehouse;

import java.sql.SQLException;

/**
 * Class handling warehouse stock. This class is responsible for issuing new orders 
 * and notifying monitoring subsystem if warehouse gets full
 * @author tom
 *
 */
public class StockManager implements DBListener{
	
	StockManager() throws SQLException {
		Database.getInstance().addListener(this);
	}
	public void requestNewOrder() {
		
	}
	
	public void signalWarehouseFull() {
		//TODO
	}

	@Override
	public void notifyEvent(DBEvent e) {
		if(e.eType.equals(EventType.ItemRemoved)) {
			try {
				if(Database.getInstance().itemsInStock(e.itemType) == 0) {
					//TODO: order item from monitor
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
