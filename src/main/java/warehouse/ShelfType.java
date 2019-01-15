package warehouse;

import AGV.Location;
import AGV.Location.LocationType;

/**
 * Utility class for communication with AGV subsystem
 * This class contains information about the shelves in the warehouse
 * @author tom
 *
 */
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
