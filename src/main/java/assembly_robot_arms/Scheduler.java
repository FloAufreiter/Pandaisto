package assembly_robot_arms;

import conveyor.BeltControlleSystem;

public class Scheduler implements Runnable {
	private RobotArm[] arms = new RobotArm[16];
	private Thread[] threads = new Thread[16];
	private BeltControlleSystem belt;
	private static Scheduler scheduler;
	private static boolean STOP = true;
	
	private Scheduler(BeltControlleSystem belt) {
		this.belt = belt;
	}
	
	public static Scheduler getInstance(BeltControlleSystem belt) {
		if(scheduler == null) {
			return new Scheduler(belt);
		}else {
			return scheduler;
		}
	}
	
	public void start() {
		STOP = false;
	}
	
	public void stop() {
		STOP = true;
	}
	
	@Override
	public void run() {
		initArms();
		
		for(int i = 0; i < arms.length; i++) {
			arms[i].start();
			threads[i] = new Thread(arms[i]);
			threads[i].start();
		}
	}
	
	private void initArms() {
		arms[0] = GrabRobotArm.getInstance(belt, 1);
		arms[1] = GrabRobotArm.getInstance(belt, 2);
		arms[2] = GrabRobotArm.getInstance(belt, 3);
		arms[3] = GrabRobotArm.getInstance(belt, 4);
		arms[4] = GrabRobotArm.getInstance(belt, 5);
		arms[5] = GrabRobotArm.getInstance(belt, 6);
		arms[6] = GrabRobotArm.getInstance(belt, 7);
		arms[7] = GrabRobotArm.getInstance(belt, 8);
		arms[8] = ScrewdriverRobotArm.getInstance(belt, 9);
		arms[9] = ScrewdriverRobotArm.getInstance(belt, 10);
		arms[10] = PainterRobotArm.getInstance(belt, 11);
		arms[11] = PainterRobotArm.getInstance(belt, 12);
		arms[12] = CameraRobotArm.getInstance(belt, 13);
		arms[13] = CameraRobotArm.getInstance(belt, 14);
		arms[14] = GrabRobotArm.getInstance(belt, 15);
		arms[15] = GrabRobotArm.getInstance(belt, 16);
	}
}