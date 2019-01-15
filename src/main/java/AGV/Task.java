package AGV;
import shared.ItemType;

class Task {

    static int ids = 0;

    private int id;
    private Location locationA;
    private Location locationB;
    private ItemType itemType;
    private State currentState = State.unprocessed;
    //long deadline;
    
    Task(Location locationA, Location locationB, ItemType itemType) {
        this.id = ids;
        ids++;
        this.locationA = locationA;
        this.locationB = locationB;
        this.itemType = itemType;
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

    ItemType getItemType() {
        return this.itemType;
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
