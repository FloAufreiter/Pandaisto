package assembly_robot_arms;

public class CameraRobotArm extends RobotArm {
	private Item item, referenzItem;
	
	public boolean isItemOK() {
		if(item.Material.compareTo(referenzItem.Material) == 0 || 
		   item.shapePoints.equals(referenzItem.shapePoints)) {
			return true;
		} else {
			return false;
		}
	}
}
