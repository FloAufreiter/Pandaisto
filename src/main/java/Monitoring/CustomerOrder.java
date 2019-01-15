package Monitoring;
import shared.ItemContainer;
import shared.ItemType;

public class CustomerOrder {
	
	private Customer customer;
	private ItemContainer container;
	private boolean done;

	public CustomerOrder(int amount, ItemType itemType, Customer customer, boolean done) {
		
		this.setContainer(new ItemContainer(amount, itemType));
		this.setCustomer(customer);
		this.setDone(done);
	}
	
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public ItemContainer getContainer() {
		return container;
	}

	public void setContainer(ItemContainer container) {
		this.container = container;
	}

	public boolean getDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
	

}
