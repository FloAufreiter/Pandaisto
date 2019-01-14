
package conveyor;

import shared.ItemType;

public class ConveyorApp {

	public static void main(String[] args) {
			BeltControlSystem bcs = BeltControlSystem.getInstance(10, 100);
			BeltControlSystem.start();
			new Thread(bcs).start();
			bcs.addItemAt(16, ItemType.CAR_BODY_WHEELS);
			bcs.lockBeltAt(12);
			bcs.addItemAt(32, ItemType.FINISHED_BLUE_CAR);
	}	

}
