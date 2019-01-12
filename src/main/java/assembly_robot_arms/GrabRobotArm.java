package assembly_robot_arms;

import conveyor.BeltControlleSystem;

public class GrabRobotArm extends RobotArm implements Runnable {
	private static GrabRobotArm grab;
	private final int ID;
	private final BeltControlleSystem belt; 
	private GrabRobotArm(BeltControlleSystem belt, int ID) {
		this.ID = ID;
		this.belt = belt;
	}
	
	public static GrabRobotArm getInstance(BeltControlleSystem belt, int ID) {
		if(grab == null) {
			grab = new GrabRobotArm(belt, ID);
		}
		return grab;
	}
	
	private String addBelt() {
		boolean isAdded = false;
		
		while(!isAdded) {
			//isAdded = belt.addItemAt(ID, type);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//type.getnrOfElements();
		return "Item " + type.name() + " has been added by Grab Robot " + ID;
	}
	
	private String removeBelt() {
		boolean isRemoved = false;
		
		while(!isRemoved) {
			//isRemoved = belt.addItemAt(ID, type);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//type.getnrOfElements();
		return "Item " + type.name() + " has been removed by Grab Robot " + ID;
	}
	
	@Override
	public void run() {
		while(!STOP) {
			if(belt.isEmpty(ID)) {
				addBelt();
			} else {
				removeBelt();
			}
		}
	}
	
	
}