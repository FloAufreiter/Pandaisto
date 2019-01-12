package warehouse;

import AGV.Location;
import AGV.Location.LocationType;

public class ShelfType {
    private int id;
    private LocationType type;
    
    public int getId() {
        return id;
    }

    public LocationType getType() {
        return type;
    }


    ShelfType(int id , Location.LocationType type) {
        this.id = id;
        this.type = type;
    }
}
