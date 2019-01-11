package AGV;

import shared.Commodity;

class Task {

    Location getLocationA() {
        return locationA;
    }

    Location getLocationB() {
        return locationB;
    }

    Commodity getCmdty() {
        return cmdty;
    }

    private Location locationA;
    private Location locationB;
    private Commodity cmdty;

    Task(Location locationA, Location locationB, Commodity cmdty) {
        this.locationA = locationA;
        this.locationB = locationB;
        this.cmdty = cmdty;
    }

    enum State {
        unprocessed,
        loaded,
        finished
    }

    State getCurrentState() {
        return currentState;
    }

    private State currentState = State.unprocessed;

    long deadline;

    State switchState() {
        switch (currentState) {
            case unprocessed:
                currentState = State.loaded;
                break;
            case loaded:
                currentState = State.finished;
                break;
        }
        return currentState;
    }
}
