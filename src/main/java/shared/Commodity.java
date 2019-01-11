package shared;

public class Commodity {

    private int id;

    public int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    private int weight;

    public Commodity(int id, int weight) {
        this.id = id;
        this.weight = weight;
    }
}
