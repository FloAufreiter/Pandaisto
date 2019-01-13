package conveyor;

import shared.ItemType;

public class BeltControlSystem implements Runnable{
	private final BeltSegment beltStart;
	private final LubricantControl lubController;
	private static BeltControlSystem bcs;
	
	private static boolean STOP = true;
	
	private BeltControlSystem(float minLubPressure, float maxConveyorSpeed){
		lubController = new LubricantControl(minLubPressure);
		//init conveyor belt segments
		int id = 0;
		BeltSegment curr = new BeltSegment(null, id, maxConveyorSpeed);
		for(int i = 1 ; i <= 16; i++) {
			id++;
			curr = new BeltSegment(curr, id, maxConveyorSpeed);
		}        
		beltStart = curr;

		ConveyorGUI.openGUI(beltStart, lubController);
	}
	public static BeltControlSystem getInstance(float minLubPressure, float maxConveyorSpeed) {
		if(bcs == null) {
			return new BeltControlSystem(minLubPressure, maxConveyorSpeed);
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
	
	public ItemType removeItemAt(int beltID) {
		synchronized(this) {
			return getBeltSegment(beltID).removeItem();
		}
	}
	public boolean addItemAt(int beltID, ItemType item) {
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
				System.out.println(curr.getItemType().toString() + "\t " + curr.getBeltID());
			}
		}
		System.out.println("End");	
	}
	
	@Override
	public void run() {
		int count = 0;
		while(!STOP) {
			count++;
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//printConveyor();
			if(count % 10 == 0) {
				synchronized(this) {
					moveAllBeltsForward();
				}
			}
			lubController.updateLubLevels();
			ConveyorGUI.updateGUI();
		}
	}
	
	public static void start() {
		STOP = false;
	}
	
	public static void stop() {
		STOP = true;
	}

	public void lockBeltAt(int beltId){
		getBeltSegment(beltId).lockSegment();
	}
	public ItemType getItemTypeAt(int beltId) {
		return getBeltSegment(beltId).getItemType();
	}
}
