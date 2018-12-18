package AGV;

import shared.Location;
import shared.Task;

import java.util.ArrayList;

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
    }

    private void driveToLocation(Location location) {
        //TODO compute und wait drive-time
    }

    private void setForkHeight() {

    }

    private void load() {

    }

    private void unload() {

    }
}
