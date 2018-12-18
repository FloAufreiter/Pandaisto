package AGV;

import shared.Location;

class Forklifter implements Runnable {

    enum Action {
        LOAD,
        UNLOAD,
    }

    private Status status = Status.available;

    private final int maximumCarriageWeight = 1000;

    private final int maximumCarriageHeight = 5;

    private final int maximumCarriageWidth = 3;

    private Location currentLocation;

    public void setCurrentRoute(Route currentRoute) {
        this.currentRoute = currentRoute;
    }

    private Route currentRoute;

    @Override
    public void run() {
        this.status = Status.inUse;
        execute();
    }

    private enum Status {
        available,
        outOfOrder,
        inUse;
    }

    public Status getStatus() {
        return status;
    }

    void execute() {
        for(Location l: currentRoute.getRouteActions().keySet()) {
            driveToLocation(l);
            switch (currentRoute.getRouteActions().get(l)) {
                case LOAD: load();
                case UNLOAD: unload();
            }
        }
    }


    private void driveToLocation(Location location) {
        //TODO compute und wait drive-time
    }

    private void setForkHeight(int height) {

    }

    private void load() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void unload() {
        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
