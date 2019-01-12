package conveyor;

import shared.ItemType;

public class BeltSegment {

	private final BeltSegment successor;
	private final float maxSpeed;
	private float currentSpeed;
	private final int beltID;
	private ItemType item = null;
	private boolean locked = false;
	
	public BeltSegment(BeltSegment successor, int beltID, float maxSpeed) {
		this.successor = successor;
		this.beltID = beltID;
		this.currentSpeed = 0;
		this.maxSpeed = maxSpeed;
	}
	public ItemType getItemType(){
		return item;
	}
	public BeltSegment getSuccessor() {
		return successor;
	}
	
	public boolean addItem(ItemType item) {
		unlockSegment();
		if(isEmpty()) {
			this.item = item;
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
		return (item == null);
	}
	
	public void lockSegment() {
		locked = true;
	}
	public void unlockSegment() {
		locked = false;
	}
	
	public boolean moveToSuccessor(){
		if(successor != null && !successor.locked && successor.isEmpty()) {
			successor.addItem(removeItem());
			return true;
		}
		return false;
	}
	
	public ItemType removeItem(){
		if(isEmpty()) return null;
		ItemType temp = item;
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
