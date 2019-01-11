package AGV;

public class Location {

    public enum LocationType {
        FLOORSHELF,
        TOPSHELF1,
        TOPSHELF2,
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
    public String toString() {
        return "id " + id + " type: " + type;
    }
}
