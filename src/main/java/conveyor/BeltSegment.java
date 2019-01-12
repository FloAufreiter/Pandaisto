package conveyor;
import shared.*;
import assembly_robot_arms.Item;

public class BeltSegment {

	private final BeltSegment successor;
	private final float maxSpeed;
	private float currentSpeed;
	private final int beltID;
	private Item item = null;
	private boolean locked = false;
	
	public BeltSegment(BeltSegment successor, int beltID, float maxSpeed) {
		this.successor = successor;
		this.beltID = beltID;
		this.currentSpeed = 0;
		this.maxSpeed = maxSpeed;
	}
	
	public BeltSegment getSuccessor() {
		return successor;
	}
	
	public boolean addItem(Item item) {
		if(isEmpty()) {
			this.item = item;
			return true;
		}
		unlockSegment();
		return false;
	}
	
	public void cshangeSpeed(float speed) {
		if(speed <= maxSpeed) {
			currentSpeed = speed;
		} else {
			currentSpeed = maxSpeed;
		}
	}
		
	public boolean isEmpty(){
		return (item == null);
	}
	
	public void lockSegment() {
		locked = true;
	}
	public void unlockSegment() {
		locked = false;
	}
	
	public boolean moveToSuccessor(){
		if(!locked && successor != null && successor.isEmpty()) {
			successor.addItem(removeItem());
			return true;
		}
		return false;
	}
	
	public Item removeItem(){
		if(isEmpty()) return null;
		//This might not be right
		lockSegment();
		Item temp = item;
		item = null;	
		return temp;
	}
	
	public boolean isEnd() {
		return (successor == null);
	}

	
	public int getBeltID() {
		return this.beltID;
	}
}
