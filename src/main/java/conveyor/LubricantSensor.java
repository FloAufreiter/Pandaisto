package conveyor;

public class LubricantSensor {
	private float lastReading;
	private final float minPressure;
	
	public LubricantSensor(float minPressure) {
		this.minPressure = minPressure;
		this.lastReading = 100;
	}
	//returns true when the lubricant pressure is below the minPressure.
	public boolean senseLubricantPressure() {
		lastReading -= Math.random()/10;
		return (lastReading < minPressure);
	}
	
	public float getLubricantLevel(){
		return lastReading;
	}
}
