package assembly_robot_arms;

import static assembly_robot_arms.RobotArm.STOP;

import shared.ItemType;

public abstract class RobotArm implements Runnable {
	private int x, y, z;
	private long timeLastMalfunction, timeLastMaintanence;
	private String title, text;
	protected ItemType type;
	protected static boolean STOP = false;
	
	public RobotArm() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public void setItemType(ItemType type) {
		this.type = type;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	public long getTimeLastMalfunction() {
		return timeLastMalfunction;
	}
	public long getTimeLastMaintanence() {
		return timeLastMaintanence;
	}
	
	public void hitDetection(Object o) {
		
	}
	
	// Report to Monitoring
	public void reportToMonitoring(String title, String text) {
		// Example what could be in title and text
		
		title = "Task 1";
		text = "Start with Assembling of Product 1...";
		
		new InformationHandling(title, text).send();
	}
	public void start() {
		STOP = false;
	}
	
	public void stop() {
		STOP = true;
	}
}
