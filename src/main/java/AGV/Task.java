package AGV;

import shared.Commodity;

class Task {

    static int ids = 0;

    private int id;
    private Location locationA;
    private Location locationB;
    private Commodity cmdty;
    private State currentState = State.unprocessed;
    //long deadline;


    Task(Location locationA, Location locationB, Commodity cmdty) {
        this.id = ids;
        ids++;
        this.locationA = locationA;
        this.locationB = locationB;
        this.cmdty = cmdty;
    }

    enum State {
        unprocessed,
        loaded,
        finished
    }

    Location getLocationA() {
        return locationA;
    }

    Location getLocationB() {
        return locationB;
    }

    Commodity getCmdty() {
        return cmdty;
    }

    public int getId() {
        return id;
    }

    State getCurrentState() {
        return currentState;
    }

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
