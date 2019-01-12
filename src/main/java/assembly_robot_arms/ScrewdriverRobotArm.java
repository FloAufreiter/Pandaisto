package assembly_robot_arms;

import conveyor.BeltControlleSystem;

public class ScrewdriverRobotArm extends RobotArm implements Runnable {
	private static ScrewdriverRobotArm screwdriver;
	private final int ID;
	private final BeltControlleSystem belt; 
	private static boolean STOP;
	
	private ScrewdriverRobotArm(BeltControlleSystem belt, int ID) {
		this.ID = ID;
		this.belt = belt;
	}
	
	public static ScrewdriverRobotArm getInstance(BeltControlleSystem belt, int ID) {
		if(screwdriver == null) {
			screwdriver = new ScrewdriverRobotArm(belt, ID);
		}
		return screwdriver;
	}
	
	private String startScrewing() {
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
			if(!belt.isEmpty(ID)) {
				startScrewing();
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
