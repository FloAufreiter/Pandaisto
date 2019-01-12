package shared;

public enum ItemType {
	SCREW(3), WHEEL(5), RED_PAINT(2), BLUE_PAINT(4), CAR_BODY(10), REMOTE(20), FINISHED_BLUE_CAR(50), FINISHED_RED_CAR(50);
	
	private final int weight;
	
	public int getweight() { return weight; }
	
	ItemType(int weight) { this.weight = weight; }
	
}
