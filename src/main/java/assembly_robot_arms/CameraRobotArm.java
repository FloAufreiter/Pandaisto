package assembly_robot_arms;

import conveyor.BeltControlleSystem;

public class CameraRobotArm extends RobotArm implements Runnable {
	private Item item, referenzItem;
	private static CameraRobotArm camera;
	private final int ID;
	private final BeltControlleSystem belt; 
	private static boolean STOP = false;
	
	private CameraRobotArm(BeltControlleSystem belt, int ID) {
		this.ID = ID;
		this.belt = belt;
	}
	
	public static CameraRobotArm getInstance(BeltControlleSystem belt, int ID) {
		if(camera == null) {
			camera = new CameraRobotArm(belt, ID);
		}
		return camera;
	}

	public String checkItem() {
		String ret = null;
		
		if(isItemOK()) {
			ret = "Item is OK";
		} else {
			ret = "Item is damaged.";
		}
		
		return ret;
	}
	
	@Override
	public void run() {
		while(!STOP) {
			if(!belt.isEmpty(ID)) {
				checkItem();
			}
		}
	}
	
	public boolean isItemOK() {
		if(item.Material.compareTo(referenzItem.Material) == 0 || 
		   item.shapePoints.equals(referenzItem.shapePoints)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void start() {
		STOP = false;
	}
	
	public void stop() {
		STOP = true;
	}
}
