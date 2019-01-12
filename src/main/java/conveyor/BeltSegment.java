package conveyor;
import shared.*;

public class BeltSegment {

	private final BeltSegment successor;
	private final float maxSpeed;
	private float currentSpeed;
	private final int beltID;
	private Commodity com = null;
	
	public BeltSegment(BeltSegment successor, int beltID, float maxSpeed) {
		this.successor = successor;
		this.beltID = beltID;
		this.currentSpeed = 0;
		this.maxSpeed = maxSpeed;
	}
	
	public BeltSegment getSuccessor() {
		return successor;
	}
	
	public int getItemID() {
		return com.getId();
	}
	
	public boolean addItem(Commodity com) {
		if(isEmpty()) {
			this.com = com;
			return true;
		}
		return false;
	}
	
	public void changeSpeed(float speed) {
		if(speed <= maxSpeed) {
			currentSpeed = speed;
		} else {
			currentSpeed = maxSpeed;
		}
	}
		
	public boolean isEmpty(){
		return (com == null);
	}
	
	public boolean moveToSuccessor(){
		if(successor != null && successor.isEmpty()) {
			successor.addItem(removeItem());
			return true;
		}
		return false;
	}
	
	public Commodity removeItem(){
		if(isEmpty()) return null;
		
		Commodity temp = com;
		com = null;	
		return temp;
	}
	
	public boolean isEnd() {
		return (successor == null);
	}

	
	public int getBeltID() {
		return this.beltID;
	}
}
