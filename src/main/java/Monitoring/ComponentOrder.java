package Monitoring;

import shared.ItemContainer;
import shared.ItemType;

public class ComponentOrder extends Order{

	Supplier supplier;
	
	ItemContainer container;
	
	public ComponentOrder(int amount, ItemType itemType, Supplier supplier) {
		
		this.container = new ItemContainer(amount, itemType);
		this.supplier = supplier;
	}

}
