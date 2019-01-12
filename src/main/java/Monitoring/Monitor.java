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

    private static ArrayList<ItemContainer> ONGOING_ORDERS = new ArrayList<ItemContainer>();

    private final Monitor monitor = new Monitor();

    private static boolean STOP = true;

    private static void createComponentOrder(int amount, ItemType itemType) {
        ONGOING_ORDERS.add(new ItemContainer(amount, itemType));
    }

    private Monitor() {

    }

    // Singleton - because only one Monitor is allowed.
    public Monitor getInstance() {
        return monitor;
    }

    @Override
    public void run() {
        while (!STOP) {
            if (!ONGOING_ORDERS.isEmpty()) {
                order();
            }
        }
    }

    public static void start() {
        STOP = false;
    }

    public static void stop() {
        STOP = true;
    }

    public static void orderComponents(ItemType itemType, int amount) {
        createComponentOrder(amount, itemType);
    }

    private static void order() {
        for (ItemContainer c : ONGOING_ORDERS) {
            OrderRunner r = new OrderRunner(c);
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
                TaskScheduler ts = AGV.getInstance().getAGVTaskScheduler();
                //TODO which Order IF
                boolean task_created = false;
                ShelfType st = null;
                Location l1 = Area.getLocation(Location.LocationType.LOADING_DOCK, 0); //TODO random which loading 0-3
                Location l2 = Area.getLocation(st.getType(), st.getId());
                while(st == null) {
                    //ask Warehouse where to bring
                    st = MonitoringInterface.getFreeItemLocation(order);
                    if(st == null) Thread.sleep(10000);
                    while (!task_created) {
                        for(int i = 0; i < order.getAmount(); i++) {
                            task_created = ts.createTask(l1, l2, order.getItemType());
                        }
                        if (task_created) {
                            ONGOING_ORDERS.remove(order);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
