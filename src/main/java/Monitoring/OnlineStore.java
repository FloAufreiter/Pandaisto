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

    public void createCustomerOrder(int amount, ItemType itemType, Customer customer, boolean done) {

        if (!Customer.getCustomers().contains(customer)) {
            Customer.getCustomers().add(customer);
        }

        ORDERS.add(new CustomerOrder(amount, itemType, customer, done));
    }

    public int getNumberOfOngoingComponentsOrders() {
        return ORDERS.size();
    }
    
    public ArrayList<CustomerOrder> getCustomerOrders() {
        return (ArrayList<CustomerOrder>) ORDERS.clone();
    }
    
    public CustomerOrder getOldestOngoingOrders() {
    		for(CustomerOrder order : ORDERS) {
    			if(!order.getDone()) {
    				return  order;
    			}
    		}
    		
    		return null;
    }


}
