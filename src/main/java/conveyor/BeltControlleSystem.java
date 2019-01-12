package conveyor;

import java.util.concurrent.TimeUnit;

import shared.Commodity;

public class BeltControlleSystem implements Runnable{
	private final BeltSegment beltStart;
	private final LubricantControll lubController;
	private BeltControlleSystem bcs;
	
	private static boolean STOP = true;
	
	private BeltControlleSystem(float minLubPressure, float maxConveyorSpeed){
		lubController = new LubricantControll(minLubPressure);
		//init conveyor belt segments
		int id = 0;
		BeltSegment curr = new BeltSegment(null, id, maxConveyorSpeed);
		for(int i = 0 ; i < 10; i++) {
			id++;
			curr = new BeltSegment(curr, id, maxConveyorSpeed);
		}        
		beltStart = curr;
	}
	public BeltControlleSystem getInstance(float minLubPressure, float maxConveyorSpeed) {
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
	
	public boolean addItemAt(int beltID, Commodity item) {
		return getBeltSegment(beltID).addItem(item); //this will change a bit when the ItemType is added
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
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			moveAllBeltsForward();
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
