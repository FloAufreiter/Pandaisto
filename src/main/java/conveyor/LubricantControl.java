package conveyor;
import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

public class LubricantControl {
	private float averagePressure;
	private final List<LubricantSensor> sensors;
	private final float minPressure;
	
	public LubricantControl(float minPressure){
		sensors = new ArrayList<LubricantSensor>();
		//add sensors to the lubricant Controller
		for(int i = 0; i < 5; i++){
			sensors.add(new LubricantSensor(minPressure));	
		}
		this.minPressure = minPressure;
	}
	
	public void updateLubLevels() {
		averagePressure = 0 ;
		for(int i = 0; i < 5; i++) {
			if(sensors.get(i).senseLubricantPressure()) {
				System.out.println("Lubricant pressure low! Things might get hot");
			}
			averagePressure += sensors.get(i).getLubricantLevel()/5.0;  
		}
		System.out.println(averagePressure);
	}
	
	public float getCurrentLubricantLevel() {
		return averagePressure;
	}
	
	public void draw(Graphics g, int x, int y) {
		if(averagePressure > minPressure) {
			g.setColor(Color.gray);
		}else {
			g.setColor(Color.RED);
		}
		g.fillRect(x, y,20, 200);
		g.setColor(Color.WHITE);
		g.fillRect(x, y, 20, 200 - (int) averagePressure * 2);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, 20, 200);
		g.setColor(Color.BLUE);
		g.drawLine(x + 25,(int) (200+y - minPressure*2), x - 5,(int) (200 + y - minPressure*2));
	}
	public float getMinPressureLevel() {
		return minPressure;
	}
}
