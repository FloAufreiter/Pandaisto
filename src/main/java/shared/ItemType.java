package shared;

public enum ItemType {
	SCREW(3, 1000), WHEEL(5, 12), RED_PAINT(2, 1), BLUE_PAINT(4, 1), CAR_BODY(10, 3), REMOTE(20, 3), FINISHED_BLUE_CAR(50, 1), FINISHED_RED_CAR(50, 1);
	
	private final int weight;

	private int nrOfElements;
	
	public int getweight() { return weight; }
	
	ItemType(int weight, int nrOfElements) {
		this.weight = weight;
		this.nrOfElements = nrOfElements;
	}
	
}
