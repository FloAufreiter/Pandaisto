package AGV;

import shared.ItemContainer;
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

    private static final int maximumCarriageWeight = 1000;

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


    void execute() {
        if(loadingRoute.getStops().isEmpty()) {
            return;
        }
        new Thread(this, String.valueOf(id)).run();
    }

    @Override
    public void run() {
        status = Status.IN_USE;
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
            Task t = route.getStops().get(nearest);
            switch (t.getCurrentState()) {
                case unprocessed:
                    loadCommodity(route.getStops().get(nearest).getItemType(), nearest);
                    t.switchState();
                    break;
                case loaded:
                    unloadCommodity(route.getStops().get(nearest).getItemType(), nearest);
                    t.switchState();
                    System.out.println("done " + t.getId());
                    break;
                case finished:
                    return;
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
            Thread.sleep(Math.round(d * 1000));
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
                    AGVInterface.confirmItemRemoval(location.getId());
                    break;
                case TOPSHELF1:
                    setForkHeight(FORKHEIGHT_1);
                    AGVInterface.confirmItemRemoval(location.getId());
                    break;
                case TOPSHELF2:
                    setForkHeight(FORKHEIGHT_2);
                    AGVInterface.confirmItemRemoval(location.getId());
                    break;
                case PRODUCTION_LINE:
            }
            Thread.sleep(6000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unloadCommodity(ItemType it, Location location) {
        //TODO trigger functions of other subsystems
        try {
            Thread.sleep(4000);
            switch(location.getType()) {
                case FLOORSHELF:
                    AGVInterface.confirmItemAdded(location.getId(), it);
                case TOPSHELF1:
                    setForkHeight(FORKHEIGHT_1);
                    AGVInterface.confirmItemAdded(location.getId(), it);
                case TOPSHELF2:
                    setForkHeight(FORKHEIGHT_2);
                    AGVInterface.confirmItemAdded(location.getId(), it);
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

}
