package assembly_robot_arms;

import conveyor.BeltControlSystem;
import shared.ItemType;

public class RobotScheduler {
	private static Arm[] arms = new Arm[16];
	private Thread[] threads = new Thread[16];
	private BeltControlSystem belt;
	private static RobotScheduler INSTANCE = null;
	private RobotScheduler(BeltControlSystem belt) {
		this.belt = belt;
	}
	
	public static RobotScheduler getInstance() {
		if(INSTANCE == null) {
			BeltControlSystem bcs = BeltControlSystem.getInstance(10, 100);
			BeltControlSystem.start();
			new Thread(bcs).start();
			INSTANCE = new RobotScheduler(bcs);
			
		}
		return INSTANCE;
	}

	public static Arm get(int id) {
		return arms[id];
	}

	public void startRobotArms() {
		initArms();
		for(int i = 0; i < arms.length; i++) {
			threads[i] = new Thread(arms[i]);
			threads[i].start();
		}
	}

	public void stopRobotArms() {
		for(Arm arm: arms) {
			arm.stopArm();
		}
	}
	
	
	public void initArms() {
		arms[0] = new Arm(belt,16);
		arms[0].addStorageType(new RobotStorage(ItemType.CAR_BODY, 10, 5));

		arms[1] = new Arm(belt,32);
		arms[1].addStorageType(new RobotStorage(ItemType.CAR_BODY, 10, 5));
		
		arms[2] = new Arm(belt,14);
		arms[2].addStorageType(new RobotStorage(ItemType.WHEEL, 40, 20));
		arms[2].addStorageType(new RobotStorage(ItemType.SCREW, 40, 20));

		arms[3] = new Arm(belt, 30);
		arms[3].addStorageType(new RobotStorage(ItemType.WHEEL, 40, 20));
		arms[3].addStorageType(new RobotStorage(ItemType.SCREW, 40, 20));

		arms[4] = new Arm(belt,12);
		arms[4].addStorageType(new RobotStorage(ItemType.RED_PAINT, 10, 5));

		arms[5] = new Arm(belt,28);
		arms[5].addStorageType(new RobotStorage(ItemType.BLUE_PAINT, 10, 5));

		arms[6] = new Arm(belt,10);
		arms[6].addStorageType(new RobotStorage(ItemType.REMOTE, 10, 5));

		arms[7] = new Arm(belt, 26);
		arms[7].addStorageType(new RobotStorage(ItemType.REMOTE, 10, 5));

		arms[8] = new Arm(belt, 0);
		arms[8].addStorageType(new RobotStorage(ItemType.FINISHED_RED_CAR, 3, 10));

		arms[9] = new Arm(belt, 18);
		arms[9].addStorageType(new RobotStorage(ItemType.FINISHED_BLUE_CAR, 3, 10));
	}

	public static Arm[] getArms() {
		return arms;
	}

	public BeltControlSystem getBelt() {
		return belt;
	}
}