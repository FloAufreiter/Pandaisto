package Monitoring;
import shared.ItemContainer;
import shared.ItemType;

public class CustomerOrder {
	Customer customer;
	
	ItemContainer container;
	
	public CustomerOrder(int amount, ItemType itemType, Customer customer) {
		
		this.container = new ItemContainer(amount, itemType);
		this.customer = customer;
	}
	

}
