import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import assembly_robot_arms.RobotScheduler;
import shared.ItemType;

public class TestRobotArms {
	static RobotScheduler rs;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rs = RobotScheduler.getInstance();
		rs.startRobotArms();
	}
	
	@Test
	public void testRobotStorage() {
		for(int i = 1; i < RobotScheduler.getArms().length; i++) {
			assertEquals(0, RobotScheduler.get(0).getRobotStorages().get(0).getNrOfElements());
		}
	}
	
	@Test
	public void testAddCarBody() {
		RobotScheduler.get(0).addElement(ItemType.CAR_BODY);
		assertEquals(5, RobotScheduler.get(0).getRobotStorages().get(0).getNrOfElements());
	}
	
	@Test
	public void testAddRemovePaint() {
		RobotScheduler.get(4).addElement(ItemType.RED_PAINT);
		RobotScheduler.get(5).addElement(ItemType.BLUE_PAINT);
		
		for(int i = 0; i < 10; i++) {
			RobotScheduler.get(4).removeElement();
			RobotScheduler.get(5).removeElement();
		}
		
		assertEquals(0, RobotScheduler.get(4).getRobotStorages().get(0).getNrOfElements());
		assertEquals(0, RobotScheduler.get(5).getRobotStorages().get(0).getNrOfElements());
	}
}
