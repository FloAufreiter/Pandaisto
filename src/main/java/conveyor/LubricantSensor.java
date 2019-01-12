package conveyor;

public class LubricantSensor {
	private float lastReading;
	private final float minPressure;
	
	public LubricantSensor(float minPressure) {
		this.minPressure = minPressure;
		this.lastReading = 0;
	}
	//returns true when the lubricant pressure is below the minPressure.
	public boolean senseLubricantPressure() {
		lastReading = 0;
		for(int i = 0; i < 10; i++) {
			lastReading += (Math.random()*100.0)/10.0;
		}
		return (lastReading < minPressure);
	}
	
	public float getLubricantLevel(){
		return lastReading;
	}
}
