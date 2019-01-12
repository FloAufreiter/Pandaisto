package warehouse;

import AGV.Location;

public class ShelfType {
    private int id;

    public int getId() {
        return id;
    }

    public Location.LocationType getType() {
        return type;
    }

    private Location.LocationType type;

    ShelfType(int id , Location.LocationType type) {
        id = id;
        type = type;
    }
}
