package conveyor;

import java.util.concurrent.TimeUnit;
import assembly_robot_arms.Item;

public class BeltControlleSystem implements Runnable{
	private final BeltSegment beltStart;
	private final LubricantControll lubController;
	private static BeltControlleSystem bcs;
	
	private static boolean STOP = true;
	
	private BeltControlleSystem(float minLubPressure, float maxConveyorSpeed){
		lubController = new LubricantControll(minLubPressure);
		//init conveyor belt segments
		int id = 0;
		BeltSegment curr = new BeltSegment(null, id, maxConveyorSpeed);
		for(int i = 1 ; i <= 16; i++) {
			id++;
			curr = new BeltSegment(curr, id, maxConveyorSpeed);
		}        
		beltStart = curr;
	}
	public static BeltControlleSystem getInstance(float minLubPressure, float maxConveyorSpeed) {
		if(bcs == null) {
			return new BeltControlleSystem(minLubPressure, maxConveyorSpeed);
		}
		return bcs;
	}
	
	
	public void moveAllBeltsForward() {
		moveAllForwardRec(beltStart);
	}
	
	private void moveAllForwardRec(BeltSegment curr) {
		if(curr != null) {
			moveAllForwardRec(curr.getSuccessor());
			curr.moveToSuccessor();
		}
	}
	
	public boolean moveBeltForward(int beltID) {
		return getBeltSegment(beltID).moveToSuccessor();
	}
	
	private BeltSegment getBeltSegment(int id) {

		for(BeltSegment curr = beltStart;
				curr != null;
				curr = curr.getSuccessor()) {
				if(curr.getBeltID() == id) {
					return curr;
				}
		}
		return null;
	}
	
	public Item removeItemAt(int beltID) {
		synchronized(this) {
			return getBeltSegment(beltID).removeItem();
		}
	}
	public boolean addItemAt(int beltID, Item item) {
		synchronized(this) {
			return getBeltSegment(beltID).addItem(item); //this will change a bit when the ItemType is added
		}
	}
	
	public boolean isEmpty(int beltID) {
		return getBeltSegment(beltID).isEmpty();
		
	}
	public void printConveyor() {
		System.out.println("Start");
		for(BeltSegment curr = beltStart;
				curr != null;
				curr = curr.getSuccessor()) {
			if(curr.isEmpty()) {
				System.out.println("0\t " + curr.getBeltID());
			}else {
				System.out.println("temp" + "\t " + curr.getBeltID());
			}
		}
		System.out.println("End");	
	}
	
	@Override
	public void run() {
		while(!STOP) {
			try {
				Thread.sleep(4000); //TODO make this delay something that makes more sence
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized(this) {
				moveAllBeltsForward();
			}
			printConveyor();
			lubController.updateLubLevels();
		}
	}
	
	public static void start() {
		STOP = false;
	}
	
	public static void stop() {
		STOP = true;
	}
	
}
