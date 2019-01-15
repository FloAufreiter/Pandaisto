import static org.junit.Assert.*;

import org.junit.Test;

import Monitoring.Customer;
import Monitoring.Monitor;
import shared.ItemType;

public class TestMonitoring {

	@Test
	public void testConnectionToOtherSytems() {
		Monitor monitor = Monitor.getInstance();
		
		assertNotNull(monitor.getAGV());
		assertNotNull(monitor.getOnlineStore());
		assertNotNull(monitor.getRobotScheduler());
		assertNotNull(monitor.getWarehouse());
	}
	
	@Test
	public void testCheckAvailabilityNotEnoughInStock() {
		
		Monitor monitor = Monitor.getInstance();
		
		int days = monitor.getDeliveryDays(100, ItemType.FINISHED_BLUE_CAR_REMOTE);
		
		assertEquals(5 + (int) Math.ceil(100 / 2000d), days);
	}
	
	@Test
	public void testAddCustomerOrder() {
		Monitor monitor = Monitor.getInstance();
		
		monitor.createCustomerOrder(100, ItemType.FINISHED_RED_CAR_REMOTE, new Customer("Testeroni", "test*+", "Teststreet 101"));
		assertEquals(1, monitor.getNumberOfOngoingCustomerOrders());
		// there should be only one CustomerOrder at this point
		assertTrue(monitor.getCustomerOrders().get(0).getContainer().getItemType().equals(ItemType.FINISHED_RED_CAR_REMOTE));
		assertEquals(100, monitor.getCustomerOrders().get(0).getContainer().getAmount());
		assertEquals("Testeroni", monitor.getCustomerOrders().get(0).getCustomer().getName());
		assertEquals("test*+", monitor.getCustomerOrders().get(0).getCustomer().getPassword());
		assertEquals("Teststreet 101", monitor.getCustomerOrders().get(0).getCustomer().getAddress());
		assertEquals(false, monitor.getCustomerOrders().get(0).getDone());
		
	}
	
	//@Test
	/*public void testAddComponentsOrer() {
		Monitor monitor = Monitor.getInstance();
		
		monitor.orderComponents(ItemType.SCREW, 100);
		assertEquals(1, monitor.getNumberOfOngoingComponentsOrders());
		
	}*/

}
