package AGV;

import shared.Commodity;

class Forklift implements Runnable {

    private static int FORKHEIGHT_1 = 1;

    private static int FORKHEIGHT_2 = 2;

    private Status status = Status.available;

    private static final int maximumCarriageWeight = 1000;

    public static int getMaximumCarriageWeight() {
        return maximumCarriageWeight;
    }

    public int getCurrentCarriageWeight() {
        return currentCarriageWeight;
    }

    private int currentCarriageWeight = 0;

    boolean canAcceptTask(Task t) {
        return currentCarriageWeight + t.getCmdty().getWeight() <= maximumCarriageWeight;
    }

    Location getCurrentLocation() {
        return currentLocation;
    }

    private Location currentLocation;

    private Route loadingRoute = new Route();
    private Route unloadingRoute = new Route();

    boolean isFullyLoaded() {
        return currentCarriageWeight > maximumCarriageWeight - 50;
    }

    enum Status {
        available,
        outOfOrder,
        inUse
    }


    void execute() {
        run();
    }

    @Override
    public void run() {
        status = Status.inUse;
        executeRoute(loadingRoute);
        executeRoute(unloadingRoute);
    }

    private void executeRoute(Route route) {
        while (!route.getStops().isEmpty()) {
            Location nearest = getNextNearestLocation(route);
            driveToLocation(nearest);
            Task t = route.getStops().get(nearest);
            switch (t.getCurrentState()) {
                case unprocessed:
                    loadCommodity(route.getStops().get(nearest).getCmdty(), nearest.getType());
                    t.switchState();
                    break;
                case loaded:
                    unloadCommodity(route.getStops().get(nearest).getCmdty());
                    t.switchState();
                    break;
                case finished:
                    return;
            }
            currentLocation = nearest;
        }
    }

    private Location getNextNearestLocation(Route unloadingRoute) {
        Location nearest = null;
        double min_cost = Double.MAX_VALUE;
        for (Location l : unloadingRoute.getStops().keySet()) {
            Double d = Area.getMinimalCostFrom(this.getCurrentLocation(), l);
            if (d < min_cost) {
                min_cost = d;
                nearest = l;
            }
        }
        unloadingRoute.getStops().remove(nearest);
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

    private void loadCommodity(Commodity c, Location.LocationType type) {
        //TODO trigger functions of other subsystems
        try {
            switch (type) {
                case TOPSHELF1:
                    setForkHeight(FORKHEIGHT_1);
                    break;
                case TOPSHELF2:
                    setForkHeight(FORKHEIGHT_2);
                    break;
            }
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void unloadCommodity(Commodity c) {
        //TODO trigger functions of other subsystems
        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void addTask(Task t) {
        loadingRoute.addLocation(t.getLocationA(), t);
        unloadingRoute.addLocation(t.getLocationB(), t);
    }

    private void maintain(long duration) {
        status = Status.outOfOrder;
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
