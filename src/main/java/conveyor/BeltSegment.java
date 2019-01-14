package conveyor;

import shared.ItemType;
import java.awt.*;

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
	
	public void draw(Graphics g, int x, int y) {
		if(successor != null) {
			successor.draw(g,x+40,y);
		}
		if(!locked) {
			g.setColor(Color.WHITE);
		}else {
			g.setColor(Color.GRAY);
		}
		g.fillRect(x,y,25,25);
		
		if(!this.isEmpty()) {
			//draw what is on the belt
			switch(item) {
			case CAR_BODY_WHEELS:
					g.setColor(Color.BLACK);
					g.fillOval(x+13, y+9, 3, 3);
					g.fillOval(x+6, y+9, 3, 3);
					g.fillOval(x+13, y+16, 3, 3);
					g.fillOval(x+6, y+16, 3, 3);
			case CAR_BODY:
					g.setColor(Color.BLACK);
					g.fillRect(x+7, y+12, 10, 5);
				break;
			case FINISHED_RED_CAR:
				g.setColor(Color.BLACK);
				g.fillOval(x+13, y+9, 3, 3);
				g.fillOval(x+6, y+9, 3, 3);
				g.fillOval(x+13, y+16, 3, 3);
				g.fillOval(x+6, y+16, 3, 3);
				g.setColor(Color.RED);
				g.fillRect(x+7, y+12, 10, 5);
				break;
			case FINISHED_BLUE_CAR:
				g.setColor(Color.BLACK);
				g.fillOval(x+13, y+9, 3, 3);
				g.fillOval(x+6, y+9, 3, 3);
				g.fillOval(x+13, y+16, 3, 3);
				g.fillOval(x+6, y+16, 3, 3);
				g.setColor(Color.BLUE);
				g.fillRect(x+7, y+12, 10, 5);
				break;
			default:
				break;
		
			}
		}
		//draw the belt
		g.setColor(Color.BLACK);
		g.drawRect(x, y, 25, 25);
	}
}
