package AGV;

import shared.Location;
import shared.Task;

public class Forklifter {

    private Status status = Status.available;

    private Location currentLocation;

    private enum Status {
        available,
        outOfOrder,
        inUse;
    }

    public Status getStatus() {
        return status;
    }

    public void execute(Task task) {

    }

    private void driveToLocation() {

    }

    private void setForkHeight() {

    }

    private void load() {

    }

    private void unload() {

    }
}
