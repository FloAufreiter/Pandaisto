package assembly_robot_arms;

public abstract class RobotArm {
	private int x, y, z;
	private long timeLastMalfunction, timeLastMaintanence;
	private String title, text;
	
	public RobotArm() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
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
}
