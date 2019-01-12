package shared;

import Monitoring.Order;

public class ItemContainer extends Order {
	
	int amount;

	public int getAmount() {
		return amount;
	}

	public ItemType getItemType() {
		return itemType;
	}
	ItemType itemType;

	public ItemContainer(int amount, ItemType itemType) {
		this.amount = amount;
		this.itemType = itemType;
	}
	


}
