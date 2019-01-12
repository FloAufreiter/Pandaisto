package Monitoring;

import shared.ItemType;

public class ComponentOrder extends Order{
	
	int amount;
	ItemType itemType;
	
	
	
	
	
	public ComponentOrder(int amount, ItemType itemType) {
		
		this.amount = amount;
		this.itemType = itemType;
	}
	


}
