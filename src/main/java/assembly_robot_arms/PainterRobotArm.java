package assembly_robot_arms;

import conveyor.BeltControlleSystem;
import shared.ItemType;

public class PainterRobotArm extends RobotArm implements Runnable {
	private static PainterRobotArm painter;
	private final int ID;
	private final BeltControlleSystem belt; 
	private static boolean STOP;
	
	private PainterRobotArm(BeltControlleSystem belt, int ID) {
		this.ID = ID;
		this.belt = belt;
	}
	
	public static PainterRobotArm getInstance(BeltControlleSystem belt, int ID) {
		if(painter == null) {
			painter = new PainterRobotArm(belt, ID);
		}
		return painter;
	}
	
	private String chooseColor() {
		String ret = null;
		
		if(type.equals(ItemType.BLUE_PAINT)) {
			ret = "color choosen blue";
		} else {
			if(type.equals(ItemType.RED_PAINT)) {
				ret = "color choosen red";
			}
		}
		
		return ret;
		// TODO: Gui color choosen 
	}
	
	private String startPainting() {
		String ret = null;
		
		if(chooseColor() != null) {
			ret = "start Painting";
		}
		
		return ret;
	}
	
	private String finishPainting() {
		String ret = null;
		
		if(startPainting() != null) {
			ret = "finished Painting";
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	@Override
	public void run() {
		while(!STOP) {
			if(!belt.isEmpty(ID)) {
				startPainting();
				finishPainting();
			}
		}
	}
	
	public void start() {
		STOP = false;
	}
	
	public void stop() {
		STOP = true;
	}
}