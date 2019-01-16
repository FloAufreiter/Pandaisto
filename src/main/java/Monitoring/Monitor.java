package Monitoring;

import AGV.AGV;
import AGV.Area;
import AGV.Location;
import AGV.TaskScheduler;
import assembly_robot_arms.RobotScheduler;
import shared.ItemContainer;
import shared.ItemType;
import warehouse.Database;
import warehouse.MonitoringInterface;
import warehouse.ShelfType;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class Monitor implements Runnable {

    private static ArrayList<ComponentOrder> ONGOING_ORDERS = new ArrayList<ComponentOrder>();
    private static ArrayList<Supplier> SUPPLIER = new ArrayList<Supplier>();
    
    MonitoringGUI gui = new MonitoringGUI();
    
    

    private static OnlineStore onlineStore = OnlineStore.getInstance();

    private static Monitor monitor;

    private static Database warehouse;
    private static AGV agv;
    private static RobotScheduler rob;

    private boolean STOP = true;
    
    public OnlineStore getOnlineStore() {
    		return onlineStore;
    }
    
    public Database getWarehouse() {
		return warehouse;
    }
    
    public AGV getAGV() {
		return agv;
    }
    
    public RobotScheduler getRobotScheduler() {
		return rob;
    }
    
    public ArrayList<ComponentOrder> getComponentsOrders() {
        return (ArrayList<ComponentOrder>) ONGOING_ORDERS.clone();
    }

    private void createComponentOrder(int amount, ItemType itemType) {

        Supplier supplier;
        switch (itemType) {
            case SCREW: {
                supplier = SUPPLIER.get(1);
                break;
            }
            case RED_PAINT: {
                supplier = SUPPLIER.get(0);
                break;
            }
            case BLUE_PAINT: {
                supplier = SUPPLIER.get(0);
                break;
            }
            case CAR_BODY: {
                supplier = SUPPLIER.get(4);
                break;
            }
            case REMOTE: {
                supplier = SUPPLIER.get(2);
                break;
            }
            case WHEEL: {
                supplier = SUPPLIER.get(3);
                break;
            }
            default:
                supplier = SUPPLIER.get(4);


        }

        ONGOING_ORDERS.add(new ComponentOrder(amount, itemType, supplier));
        gui.Refresh();
        
        // forward order to the regarding subsystems
        orderComponents();
    }
    
    public void checkOngoingOrders() {
    	
    		System.out.println("CHECK ONGOING ORDERS");
    		CustomerOrder oldestOngoingOrder = onlineStore.getOldestOngoingOrders();
    		
    		if(oldestOngoingOrder != null) {
				if(warehouse.itemsInStock(oldestOngoingOrder.getContainer().getItemType()) >= oldestOngoingOrder.getContainer().getAmount()){
					int shelfId = warehouse.itemByType(oldestOngoingOrder.getContainer().getItemType().toString());
					warehouse.deleteItem(shelfId);

					oldestOngoingOrder.setDone(true);
					System.out.println("ORDER DONE!");

				}
				else {
					System.out.println("NOT ENOUGH PRODUCED CARS: " + warehouse.itemsInStock(oldestOngoingOrder.getContainer().getItemType()) + " " + oldestOngoingOrder.getContainer().getItemType().toString());
				}

    		}
    		
    		gui.Refresh();
    }

    public void createCustomerOrder(int amount, ItemType itemType, Customer customer) {

    		// start production (if it isn't already running)
        rob.startRobotArms();

        if(onlineStore.checkAvailability(100, itemType) <= 5) {
        		int shelfId = -1;
				shelfId = warehouse.itemByType(itemType.toString());
				
        		if(shelfId == -1) {
        			// wait
        			onlineStore.createCustomerOrder(amount, itemType, customer, false);
        		}
        		else {
        			onlineStore.createCustomerOrder(amount, itemType, customer, true);
        			warehouse.deleteItem(shelfId);
        		}
        }
        
        else {
        		onlineStore.createCustomerOrder(amount, itemType, customer, false);
        }
    }

    private Monitor() {

        SUPPLIER.add(new Supplier("Paint-Rain GmbH"));
        SUPPLIER.add(new Supplier("We screw you GmbH"));
        SUPPLIER.add(new Supplier("We make Remote GmbH"));
        SUPPLIER.add(new Supplier("Wheels and Eals GmbH"));
        SUPPLIER.add(new Supplier("Some Body to Love AG"));
        SUPPLIER.add(new Supplier("Wholesaler"));

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    gui.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Singleton - because only one Monitor is allowed.
    public static Monitor getInstance() {
        if (monitor == null) {
            monitor = new Monitor();
            try {
                warehouse = Database.getInstance();
                // Starting Warehouse GUI 
                MonitoringInterface.startGUI();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            // start AGV
            agv = AGV.getInstance();
            agv.startAGV();
            
            // get RobotScheduler isntance
            rob = RobotScheduler.getInstance();
        }
        return monitor;

    }

    public void requestItemForProductionLine(int locID, ItemType item) {
        Location prodLine = Area.getInstance().getLocation(Location.LocationType.PRODUCTION_LINE, locID);

        gui.Refresh();
        TaskScheduler ts = AGV.getInstance().getAGVTaskScheduler();
        boolean task_created = false;
        ShelfType st = null;
        Location shelfLoc;
        while (st == null) {
            //ask Warehouse where to bring
            st = MonitoringInterface.getItemLocation(item);
            if (st == null)
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                   
                    e.printStackTrace();
                }
            else {
                shelfLoc = Area.getInstance().getLocation(st.getType(), st.getId());
                while (!task_created) {
                    task_created = ts.createTask(shelfLoc, prodLine, item);
                }
            }
        }
        gui.Refresh();
        
        
        
    }

    public void removeItemForProductionLine(int locID, int amount, ItemType item) {
        Location prodLine = Area.getInstance().getLocation(Location.LocationType.PRODUCTION_LINE, locID);
        TaskScheduler ts = AGV.getInstance().getAGVTaskScheduler();

        boolean task_created = false;
        ShelfType st = null;
        Location l2;
        for (int i = 0; i < amount; i++) {
            while (st == null) {
                //ask Warehouse where to bring
                st = MonitoringInterface.getFreeItemLocation();
                if (st == null)
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        
                        e.printStackTrace();
                    }
                else {
                    l2 = Area.getInstance().getLocation(st.getType(), st.getId());
                    while (!task_created) {
                        task_created = ts.createTask(prodLine, l2, item);
                    }
                }
            }
            if (task_created) {
                st = null;
                task_created = false;
            }
        }
        
        checkOngoingOrders();
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
    
    public ArrayList<CustomerOrder> getCustomerOrders(){
    		return onlineStore.getCustomerOrders();
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
    		gui.Refresh();
    		// run throw orders
        for (ComponentOrder c : ONGOING_ORDERS) {
            OrderRunner r = new OrderRunner(c.container);
            r.run();
        }
    }

    // class to simulate Components Delivery
    private static class OrderRunner implements Runnable {
    	   		
        ItemContainer order;

        OrderRunner(ItemContainer order) {
            this.order = order;
        }

        @Override
        public void run() {
            try {
                TaskScheduler ts = AGV.getInstance().getAGVTaskScheduler();
                boolean task_created = false;
                ShelfType st = null;
                Random rand = new Random();
                Location l1 = Area.getInstance().getLocation(Location.LocationType.LOADING_DOCK, rand.nextInt(3)); //TODO random which loading 0-3
                Location l2;
                for (int i = 0; i < order.getAmount(); i++) {
                    while (st == null) {
                        // ask Warehouse where to bring
                        st = MonitoringInterface.getFreeItemLocation();
                        if (st == null) Thread.sleep(1000);
                        else {
                            l2 = Area.getInstance().getLocation(st.getType(), st.getId());

                            while (!task_created) {
                                task_created = ts.createTask(l1, l2, order.getItemType());
                            }
                        }
                    }
                    if (task_created) {
                        st = null;
                        task_created = false;
                    }
                }
                
                ONGOING_ORDERS.remove(order);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
