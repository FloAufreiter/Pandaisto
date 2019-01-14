package AGV;
import java.util.List;
import assembly_robot_arms.RobotScheduler;
import shared.ItemType;
import warehouse.AGVInterface;

class Forklift implements Runnable {

    static int IDS;

    public int getId() {
        return id;
    }

    private int id = IDS++;

    private static int FORKHEIGHT_1 = 1;

    private static int FORKHEIGHT_2 = 2;

    private Status status = Status.AVAILABLE;

    private static final int maximumCarriageWeight = 100;

    private int currentCarriageWeight = 0;

    boolean canAcceptTask(Task t) {
        int weight_of_task = t.getItemType().getweight();
        return currentCarriageWeight + weight_of_task <= maximumCarriageWeight;
    }

    Location getCurrentLocation() {
        return currentLocation;
    }

    private Location currentLocation = Area.getRandomLocation();

    private Route loadingRoute = new Route();
    private Route unloadingRoute = new Route();

    boolean isFullyLoaded() {
        return currentCarriageWeight > maximumCarriageWeight - 50;
    }

    enum Status {
        AVAILABLE,
        OUT_OF_ORDER,
        IN_USE
    }

    boolean loadingRouteEmpty () {
        return loadingRoute.getStops().isEmpty();
    }

    @Override
    public void run() {
//        status = Status.IN_USE; //is set in scheduler loop
        AGV.updateGui(this);
        executeRoute(loadingRoute);
        executeRoute(unloadingRoute);
        status = Status.AVAILABLE;
        AGV.updateGui(this);
    }

    private void executeRoute(Route route) {
        while (!route.getStops().isEmpty()) {
            Location nearest = getNextNearestLocation(route);
            driveToLocation(nearest);
            List<Task> tasks = route.getStops().get(nearest);
            for(Task t: tasks) {
	            switch (t.getCurrentState()) {
	                case unprocessed:
	                    loadCommodity(t.getItemType(), nearest);
	                    t.switchState();
	                    //System.out.println("done loading" +t.getId());
	                    break;
	                case loaded:
	                    unloadCommodity(t.getItemType(), nearest);
	                    t.switchState();
	                   // System.out.println("done unloading" + t.getId());
	                    break;
	                case finished:
	                    break;
	            }
            }
            route.getStops().remove(nearest);
            currentLocation = nearest;
            AGV.updateGui(this);
        }
    }

    private Location getNextNearestLocation(Route route) {
        Location nearest = null;
        double min_cost = Double.MAX_VALUE;
        for (Location l : route.getStops().keySet()) {
            Double d = Area.getMinimalCostFrom(this.getCurrentLocation(), l);
            if (d < min_cost) {
                min_cost = d;
                nearest = l;
            }
        }
        return nearest;
    }

    Status getStatus() {
        return status;
    }

    private void driveToLocation(Location location) {
        Double d = Area.getMinimalCostFrom(this.getCurrentLocation(), location);
        try {
            Thread.sleep(Math.round(d * 800));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setForkHeight(int height) {
        try {
            Thread.sleep(height * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void loadCommodity(ItemType it, Location location) {
        //TODO trigger functions of other subsystems
        try {
            switch (location.getType()) {
                case FLOORSHELF:
                	//System.out.println(Thread.currentThread().getId() + " loading floorshelf");
                    AGVInterface.confirmItemRemoval(location.getId());
                    break;
                case TOPSHELF1:
                	//System.out.println(Thread.currentThread().getId() + " loading topshelf1");
                    setForkHeight(FORKHEIGHT_1);
                    AGVInterface.confirmItemRemoval(location.getId());
                    break;
                case TOPSHELF2:
                	//System.out.println(Thread.currentThread().getId() + " loading topshelf2");
                    setForkHeight(FORKHEIGHT_2);
                    AGVInterface.confirmItemRemoval(location.getId());
                    break;
                case PRODUCTION_LINE:
                    RobotScheduler.get(location.getId()).removeElement();
            }
            Thread.sleep(100); //TODO: changed for faster testing!
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unloadCommodity(ItemType it, Location location) {
        //TODO trigger functions of other subsystems
        try {
        	System.out.println(Thread.currentThread().getId() + " unloading");
            Thread.sleep(400); //TODO cahnged for faster testing
            switch(location.getType()) {
                case FLOORSHELF:
                    AGVInterface.confirmItemAdded(location.getId(), it);
                    break;
                case TOPSHELF1:
                    setForkHeight(FORKHEIGHT_1);
                    AGVInterface.confirmItemAdded(location.getId(), it);
                    break;
                case TOPSHELF2:
                    setForkHeight(FORKHEIGHT_2);
                    AGVInterface.confirmItemAdded(location.getId(), it);
                    break;
                case PRODUCTION_LINE:
                   RobotScheduler.get(location.getId()).addElement(it);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void addTask(Task t) {
        loadingRoute.addLocation(t.getLocationA(), t);
        unloadingRoute.addLocation(t.getLocationB(), t);
    }

    private void maintain(long duration) {
        status = Status.OUT_OF_ORDER;
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void setStatus(Status s) {
    	this.status = s;
    }
}
