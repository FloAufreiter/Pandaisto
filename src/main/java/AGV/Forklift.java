package AGV;

import assembly_robot_arms.RobotScheduler;
import shared.ItemType;
import warehouse.AGVInterface;

import java.util.List;

class Forklift implements Runnable {

    private static int IDS;

    public int getId() {
        return id;
    }

    private int id = IDS++;

    private static int FORK_HEIGHT_1 = 1;

    private static int FORK_HEIGHT_2 = 2;

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

    private Location currentLocation = Area.getInstance().getRandomLocation();

    private Route loadingRoute = new Route();
    private Route unloadingRoute = new Route();

    boolean isFullyLoaded() {
        return currentCarriageWeight > maximumCarriageWeight - 50;
    }

    enum Status {
        AVAILABLE,
        //OUT_OF_ORDER, //maintenance currently not considered
        IN_USE
    }

    boolean loadingRouteEmpty() {
        return loadingRoute.getStops().isEmpty();
    }

    @Override
    public void run() {
        AGV.getInstance().updateGui(this);
        executeRoute(loadingRoute);
        executeRoute(unloadingRoute);
        status = Status.AVAILABLE;
        AGV.getInstance().updateGui(this);
    }

    /**
     * executes the loading or unloading route of this forklift
     * @param route
     */
    private void executeRoute(Route route) {
        while (!route.getStops().isEmpty()) {
            Location nearest = getNextNearestLocation(route);
            driveToLocation(nearest);
            List<Task> tasks = route.getStops().get(nearest);
            for (Task t : tasks) {
                switch (t.getCurrentState()) {
                    case unprocessed:
                        loadCommodity(nearest);
                        t.switchState();
                        break;
                    case loaded:
                        unloadCommodity(t.getItemType(), nearest);
                        t.switchState();
                        break;
                    case finished:
                        break;
                }
            }
            route.getStops().remove(nearest);
            currentLocation = nearest;
            AGV.getInstance().updateGui(this);
        }
    }

    /**
     * returns the next nearest location on a given route
     * @param route
     * @return
     */
    private Location getNextNearestLocation(Route route) {
        Location nearest = null;
        double min_cost = Double.MAX_VALUE;
        for (Location l : route.getStops().keySet()) {
            Double d = Area.getInstance().getMinimalCostFrom(this.getCurrentLocation(), l);
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
        Double d = Area.getInstance().getMinimalCostFrom(this.getCurrentLocation(), location);
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

    private void loadCommodity(Location location) {
        System.out.println("Forklift " + this.getId() + " loading");
        try {
            switch (location.getType()) {
                case FLOOR_SHELF:
                    AGVInterface.confirmItemRemoval(location.getId());
                    break;
                case TOP_SHELF1:
                    setForkHeight(FORK_HEIGHT_1);
                    AGVInterface.confirmItemRemoval(location.getId());
                    break;
                case TOP_SHELF2:
                    setForkHeight(FORK_HEIGHT_2);
                    AGVInterface.confirmItemRemoval(location.getId());
                    break;
                case PRODUCTION_LINE:
                    RobotScheduler.get(location.getId()).removeElement();
            }
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unloadCommodity(ItemType it, Location location) {
        try {
            System.out.println("Forklift " + this.getId() + " unloading");
            Thread.sleep(2000);
            switch (location.getType()) {
                case FLOOR_SHELF:
                    AGVInterface.confirmItemAdded(location.getId(), it);
                    break;
                case TOP_SHELF1:
                    setForkHeight(FORK_HEIGHT_1);
                    AGVInterface.confirmItemAdded(location.getId(), it);
                    break;
                case TOP_SHELF2:
                    setForkHeight(FORK_HEIGHT_2);
                    AGVInterface.confirmItemAdded(location.getId(), it);
                    break;
                case PRODUCTION_LINE:
                    RobotScheduler.getInstance().get(location.getId()).addElement(it);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void addTask(Task t) {
        loadingRoute.addLocation(t.getLocationA(), t);
        unloadingRoute.addLocation(t.getLocationB(), t);
    }

    void setStatus(Status s) {
        this.status = s;
    }
}
