package shared;

public enum ItemType {
	SCREW(3, 30), WHEEL(5, 30), RED_PAINT(2, 10), BLUE_PAINT(4, 10), CAR_BODY(10, 5), REMOTE(20, 5),
	FINISHED_BLUE_CAR(50, 1), FINISHED_RED_CAR(50, 1), CAR_BODY_WHEELS(40,1),FINISHED_RED_CAR_REMOTE(50, 1), FINISHED_BLUE_CAR_REMOTE(50, 1); 
	
	private final int weight;

	public int getNrOfElements() {
		return nrOfElements;
	}

	private int nrOfElements;
	
	public int getweight() { return weight; }
	
	ItemType(int weight, int nrOfElements) {
		this.weight = weight;
		this.nrOfElements = nrOfElements;
	}
	
}
