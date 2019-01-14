package shared;

public class ItemContainer {

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
