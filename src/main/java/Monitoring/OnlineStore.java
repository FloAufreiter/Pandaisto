package Monitoring;

import java.util.ArrayList;

import shared.ItemType;

public class OnlineStore {
	
	ArrayList<ItemType> soritment = new ArrayList<ItemType>();
	
	private static ArrayList<CustomerOrder> ongoingOrders = new ArrayList<CustomerOrder>();
	
	
	
	public int checkAvailability(int amount, ItemType type) {
		
		// ask warehouse for stock information
		boolean enoughInStock = true;
		
		if(enoughInStock) {
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
			
			ongoingOrders.add(new CustomerOrder(amount, itemType, customer));			
	}
	

}
