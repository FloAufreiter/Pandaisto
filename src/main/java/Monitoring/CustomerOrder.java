package Monitoring;

import java.util.ArrayList;

import shared.ItemContainer;
import shared.ItemType;

public class CustomerOrder extends Order{
	
	
	Customer customer;
	
	ItemContainer container;
	
	
	
	public CustomerOrder(int amount, ItemType itemType, Customer customer) {
		
		this.container = new ItemContainer(amount, itemType);
		this.customer = customer;
	}
	

}
