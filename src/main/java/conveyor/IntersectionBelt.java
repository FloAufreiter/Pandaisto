package conveyor;

public class IntersectionBelt extends BeltSegment {
	private int currDirection = 0;
	private final BeltSegment successorLeft;
	
	public IntersectionBelt(BeltSegment successorRight, BeltSegment successorLeft, int beltID,float maxSpeed) {
		super(successorRight, beltID, maxSpeed);
		this.successorLeft = successorLeft;
 	}
	
	//0 = left 1 = right
	public boolean setDirection(int direction) {
		if(direction < 0 || direction > 1) {
			return false;
		}
		currDirection = direction;
		return true;
	}
	
	public boolean moveToSuccessor(){
		if(currDirection == 0) {
			if(successorLeft.isEmpty()) {
				successorLeft.addItem(removeItem());
				return true;
			}
		}else if(currDirection == 1){
			if(super.getSuccessor().isEmpty()) {
				super.getSuccessor().addItem( removeItem());
				return true;
			}
		}
		return false;
	}

}
