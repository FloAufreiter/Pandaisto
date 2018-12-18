package shared;

public enum Location {
    WAREHOUSE(0),
    ROBOTARM(0),
    LOADINGDOCK(0);

    private int id;

    Location(int id) {
        this.id = id;
    }
}
