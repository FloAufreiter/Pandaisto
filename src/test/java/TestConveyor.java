import conveyor.BeltControlSystem;
import org.junit.Test;
import shared.ItemType;

public class TestConveyor {
	@Test
	public void addItemToConveyorAndMoveForward() {
		BeltControlSystem bcs = BeltControlSystem.getInstance(10, 100);
		bcs.addItemAt(16, ItemType.WHEEL);
		for(int i = 0; i < 16; i++) {
			bcs.moveAllBeltsForward();
		}
		assert(bcs.addItemAt(0, ItemType.RED_PAINT) == false);
	}
	
	@Test
	public void addItemToConveyorAndMoveForwardWithLock() {
		BeltControlSystem bcs = BeltControlSystem.getInstance(10, 100);
		bcs.lockBeltAt(5);
		bcs.addItemAt(16, ItemType.WHEEL);
		for(int i = 0; i < 16; i++) {
			bcs.moveAllBeltsForward();
		}
		assert(bcs.addItemAt(6, ItemType.CAR_BODY) == false);
		assert(bcs.addItemAt(0, ItemType.RED_PAINT) != false);
	}
	
	@Test
	public void addMultibleItemsToConveyorAndMoveForward() {
		BeltControlSystem bcs = BeltControlSystem.getInstance(10, 100);
		
		bcs.addItemAt(16, ItemType.WHEEL);
		bcs.moveAllBeltsForward();
		bcs.addItemAt(16, ItemType.CAR_BODY);
		bcs.moveAllBeltsForward();
		bcs.addItemAt(16, ItemType.FINISHED_BLUE_CAR);
		
		for(int i = 0; i < 15; i++) {
			bcs.moveAllBeltsForward();
		}

		assert(bcs.addItemAt(2, ItemType.BLUE_PAINT) == false);
		assert(bcs.addItemAt(1, ItemType.BLUE_PAINT) == false);
		assert(bcs.addItemAt(0, ItemType.CAR_BODY) == false);
	}
	@Test 
	public void addItemToConveyorAndRemoveItAgain() {
		BeltControlSystem bcs = BeltControlSystem.getInstance(10, 100);
		
		bcs.addItemAt(16, ItemType.WHEEL);
		
		bcs.removeItemAt(16);
		assert(!bcs.isEmpty(16));
	}
	
//	@Test
//	public void 
	
}
