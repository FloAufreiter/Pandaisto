package Monitoring;

import java.util.ArrayList;

import AGV.AGV;
import AGV.Area;
import AGV.Location;
import AGV.TaskScheduler;
import shared.ItemContainer;
import shared.ItemType;
import warehouse.MonitoringInterface;
import warehouse.ShelfType;

public class Monitor implements Runnable {

    private static ArrayList<ComponentOrder> ONGOING_ORDERS = new ArrayList<ComponentOrder>();
    private static ArrayList<Supplier> SUPPLIER = new ArrayList<Supplier>();
    
    private static OnlineStore onlineStore = OnlineStore.getInstance();

    private static final Monitor monitor = new Monitor();

    private boolean STOP = true;

    private void createComponentOrder(int amount, ItemType itemType) {
    	
    		Supplier supplier;
    		switch (itemType) {
    		case SCREW: {supplier = SUPPLIER.get(1);
    				break;}
    		case RED_PAINT: {supplier = SUPPLIER.get(0);
    				break;}
    		case BLUE_PAINT: {supplier = SUPPLIER.get(0);
			break;}
    		case CAR_BODY: {supplier = SUPPLIER.get(4);
			break;}
    		case REMOTE: {supplier = SUPPLIER.get(2);
			break;}
    		case WHEEL: {supplier = SUPPLIER.get(3);
			break;}
			default:
				supplier = SUPPLIER.get(4);
    		
    		
    		}
    	
    	System.out.println("duh");
    	
        ONGOING_ORDERS.add(new ComponentOrder(amount, itemType, supplier));
        orderComponents();
    }
    
    public void createCustomerOrder(int amount, ItemType itemType, Customer customer) {
        onlineStore.createCustomerOrder(amount, itemType, customer);
    }

    private Monitor() {
    	
    		SUPPLIER.add(new Supplier("Paint-Rain GmbH"));
    		SUPPLIER.add(new Supplier("We screw you GmbH"));
    		SUPPLIER.add(new Supplier("We make Remote GmbH"));
    		SUPPLIER.add(new Supplier("Wheels and Eals GmbH"));
    		SUPPLIER.add(new Supplier("Some Body to Love AG"));
    		SUPPLIER.add(new Supplier("Wholesaler"));

    }

    // Singleton - because only one Monitor is allowed.
    public static Monitor getInstance() {
        return monitor;
    }
    
    public int getNumberOfOngoingComponentsOrders() {
		return ONGOING_ORDERS.size();
	}
    
    public int getDeliveryDays(int amount, ItemType type) {
    		return onlineStore.checkAvailability(amount, type);
    }
    
    public int getNumberOfOngoingCustomerOrders() {
    		return onlineStore.getNumberOfOngoingComponentsOrders();
    }

    @Override
    public void run() {
        while (!STOP) {
            if (!ONGOING_ORDERS.isEmpty()) {
                orderComponents();
            }
        }
    }

    public void start() {
        STOP = false;
    }

    public void stop() {
        STOP = true;
    }

    public void orderComponents(ItemType itemType, int amount) {
        createComponentOrder(amount, itemType);
    }

    private void orderComponents() {
        for (ComponentOrder c : ONGOING_ORDERS) {
            OrderRunner r = new OrderRunner(c.container);
            r.run();
        }
    }
    
   

    private static class OrderRunner implements Runnable {
        ItemContainer order; //TODE stopfe in andere Klasse

        OrderRunner(ItemContainer order) {
            this.order = order;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(10000);
                System.out.println("runs");
                TaskScheduler ts = AGV.getInstance().getAGVTaskScheduler();
                //TODO which Order IF
                boolean task_created = false;
                ShelfType st = null;
                Location l1 = Area.getLocation(Location.LocationType.LOADING_DOCK, 0); //TODO random which loading 0-3
                Location l2;
                while(st == null) {
                    //ask Warehouse where to bring
                    st = MonitoringInterface.getFreeItemLocation(order);
                    if(st == null) Thread.sleep(10000);
                    else {
                    	System.out.println(st);
                    	l2 = Area.getLocation(st.getType(), st.getId());
                    
	                    while (!task_created) {
	                        for(int i = 0; i < order.getAmount(); i++) {
	                            task_created = ts.createTask(l1, l2, order.getItemType());
	                        }
	                        if (task_created) {
	                            ONGOING_ORDERS.remove(order);
	                        }
	                    }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    
}
