package conveyor;
import java.util.*;

public class LubricantControll {
	private float averagePressure;
	private final List<LubricantSensor> sensors;
	
	public LubricantControll(float minPressure){
		sensors = new ArrayList<LubricantSensor>();
		//add sensors to the lubricant Controller
		for(int i = 0; i < 5; i++){
			sensors.add(new LubricantSensor(minPressure));	
		}
	}
	
	public void updateLubLevels() {
		for(int i = 0; i < 5; i++) {
			if(sensors.get(i).senseLubricantPressure()) {
				System.out.println("Lubricant pressure low! Things might get hot");
			}
			averagePressure += sensors.get(i).getLubricantLevel()/5.0;  
		}
	}
	
	public float getCurrentLubricantLevel() {
		return averagePressure;
	}
}
