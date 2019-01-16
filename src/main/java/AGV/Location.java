package AGV;

/**
 * this class is used to specify the locations the forklifts should visit in a task
 */
public class Location {

    public enum LocationType {
        FLOOR_SHELF,
        TOP_SHELF1,
        TOP_SHELF2,
        PRODUCTION_LINE,
        LOADING_DOCK
    }

    private int id;

    public int getId() {
        return id;
    }

    LocationType getType() {
        return type;
    }

    private LocationType type;

    Location(LocationType type, int id) {
        this.id = id;
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Location other = (Location) obj;
        if (id != other.id)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return type + " at id: " + id;
    }
}
