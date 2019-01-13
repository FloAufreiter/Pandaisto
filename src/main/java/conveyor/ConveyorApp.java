
package conveyor;

import shared.ItemType;

public class ConveyorApp {

	public static void main(String[] args) {
			BeltControlSystem bcs = BeltControlSystem.getInstance(10, 100);
			bcs.start();
			new Thread(bcs).start();
			bcs.addItemAt(16, ItemType.WHEEL);
			bcs.lockBeltAt(12);
	}

}
