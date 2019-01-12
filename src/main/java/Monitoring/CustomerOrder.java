package Monitoring;

import java.util.ArrayList;

import shared.ItemType;

public class CustomerOrder extends Order{
	
	int amount;
	ItemType itemType;
	Customer customer;
	
	
	
	
	public CustomerOrder(int amount, ItemType itemType, Customer customer) {
		
		this.amount = amount;
		this.itemType = itemType;
		this.customer = customer;
	}
	

}
