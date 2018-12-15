package AGV;

import shared.Task;

public class Forklifter {

    private Status status = Status.available;

    private enum Status {
        available,
        outOfOrder,
        inUse;
    }

    public void execute(Task task) {

    }

    private void driveToLocation() {

    }




}
