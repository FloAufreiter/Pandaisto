package Monitoring;

import java.util.ArrayList;

import shared.ItemType;
import warehouse.MonitoringInterface;

public class OnlineStore {
	
	ArrayList<ItemType> soritment = new ArrayList<ItemType>();
	
	private static ArrayList<CustomerOrder> ONGOING_ORDERS = new ArrayList<CustomerOrder>();
	
	private static OnlineStore onlineStore = new OnlineStore();
	
	private OnlineStore() {
		
	}
	
	public static OnlineStore getInstance() {
		
		return onlineStore;
		
	}
	
	public int checkAvailability(int amount, ItemType type) {
		
		// ask warehouse for stock information
		MonitoringInterface mi = new MonitoringInterface();
		
		//int amountInStock = mi.getItemStock(type);
		
		int amountInStock = 1;
		
		
		if(amountInStock > amount) {
			return 5;
		}
		
		else {
			
			// we can produce 2000 cars a day
			int estimatedDelivery = 5 + amount/2000;
			
			return estimatedDelivery;
		}	
	}
	
	public void createCustomerOrder(int amount, ItemType itemType, Customer customer) {
		
		if(!Customer.customers.contains(customer)) {
			Customer.customers.add(customer);
		}
			
		ONGOING_ORDERS.add(new CustomerOrder(amount, itemType, customer));			
	}
	
	public int getNumberOfOngoingComponentsOrders() {
		return ONGOING_ORDERS.size();
	}
	

}
