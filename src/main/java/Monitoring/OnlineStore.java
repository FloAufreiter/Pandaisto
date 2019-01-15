package Monitoring;

import java.util.ArrayList;

import shared.ItemType;
import warehouse.MonitoringInterface;

public class OnlineStore {

    ArrayList<ItemType> soritment = new ArrayList<ItemType>();

    private static ArrayList<CustomerOrder> ORDERS = new ArrayList<CustomerOrder>();

    private static OnlineStore onlineStore = new OnlineStore();
    

    private OnlineStore() {

    }

    public static OnlineStore getInstance() {

        return onlineStore;

    }

    public int checkAvailability(int amount, ItemType type) {

        // ask warehouse for stock information
        MonitoringInterface mi = new MonitoringInterface();

        int amountInStock = mi.getItemStock(type);

        if (amountInStock > amount) {
            return 5;
        } else {
            // we can produce 2000 cars a day
            int estimatedDelivery = 5 + (int) Math.ceil(amount / 2000d);

            return estimatedDelivery;
        }
    }

    // add Customer Order to System.
    public void createCustomerOrder(int amount, ItemType itemType, Customer customer, boolean done) {

        if (!Customer.getCustomers().contains(customer)) {
            Customer.getCustomers().add(customer);
        }

        ORDERS.add(new CustomerOrder(amount, itemType, customer, done));
    }

    public int getNumberOfOngoingComponentsOrders() {
    	
    		int counter = 0;
    		for(CustomerOrder order : ORDERS) {
    			if(!order.getDone()) {
    				counter++;
    			}
    		}
    		
        return counter;
    }
    
    public ArrayList<CustomerOrder> getCustomerOrders() {
        return (ArrayList<CustomerOrder>) ORDERS.clone();
    }
    
    // older Orders are priorized - so the FIFO principle is used here 
    // the first found not done order is returned
    // if there is no not done order - null is returned
    public CustomerOrder getOldestOngoingOrders() {
    		for(CustomerOrder order : ORDERS) {
    			if(!order.getDone()) {
    				return  order;
    			}
    		}
    		
    		return null;
    }


}
