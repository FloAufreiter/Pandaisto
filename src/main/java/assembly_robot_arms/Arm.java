package assembly_robot_arms;

import conveyor.BeltControlSystem;
import shared.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Arm implements Runnable {
    private static int IDS = 0;
    private int id;
    private BeltControlSystem bcs;

    protected List<RobotStorage> type;
    boolean stop = false;

    
    public List<RobotStorage> getRobotStorages() {
        return type;
    }

    public void stopArm() {
        stop = true;
    }

    public Arm(BeltControlSystem bcs) {
        this.type = new ArrayList<>();
        this.id = IDS++;
        this.bcs = bcs;
    }

    public void addStorageType(RobotStorage type) {
        this.type.add(type);
    }

    public void work() {
        try {
            if (type.isEmpty()) return;
            switch (type.get(0).getType()) {
                case CAR_BODY:
                    //TODO Communication with Conveyorbelt
                	Thread.sleep(2000);
                    if (type.get(0).getNrOfElements() > 0) {
                        type.get(0).removeElement();
                    }
                    System.out.println("Move Car Body to Conveyor");
                    bcs.addItemAt(16, ItemType.CAR_BODY);
                    break;
                case WHEEL:
                	if(!bcs.isEmpty(14) && bcs.getItemTypeAt(14) == ItemType.CAR_BODY) {
                		bcs.removeItemAt(14);
                		bcs.lockBeltAt(14);
                	
	                    Thread.sleep(2000);
	                    if (type.get(0).getNrOfElements() > 0) {
	                        type.get(0).removeElement();
	                    }
	                    if (type.get(1).getNrOfElements() > 0) {
	                        type.get(1).removeElement();
	                    }
	                    System.out.println("Add Wheels to Car");
	                    bcs.addItemAt(14, ItemType.CAR_BODY_WHEELS);
	                }
	                Thread.sleep(250);
	                
                	
                    break;
                case RED_PAINT:
                	if(!bcs.isEmpty(12) && bcs.getItemTypeAt(12) == ItemType.CAR_BODY_WHEELS) {
                		bcs.lockBeltAt(12);
                	
	                	Thread.sleep(2000);
	                    if (type.get(0).getNrOfElements() > 0) {
	                        type.get(0).removeElement();
	                    }
	                    bcs.removeItemAt(12);
                		bcs.addItemAt(12, ItemType.FINISHED_RED_CAR);
	                }
                    break;
                case BLUE_PAINT:
                    
                	Thread.sleep(2000);
                    if (type.get(0).getNrOfElements() > 0) {
                        type.get(0).removeElement();
                    }

                    break;
                case REMOTE:
                    Thread.sleep(2000);
                    if(type.get(0).getNrOfElements() > 0) {
                        type.get(0).removeElement();
                    }
                    System.out.println("Calibrate with Remote Control");
                    break;
                case FINISHED_BLUE_CAR:
                    Thread.sleep(2000);
                    //TODO check if Car damaged laaaaaaater
                    Random r = new Random();
                    if(!(r.nextInt(100) < 5)) {
                        if(type.get(0).getNrOfElements() > 0) {
                            type.get(0).removeElement();
                        }
                    }
                    System.out.println("Finished Blue Car");
                    break;
                case FINISHED_RED_CAR:
                	if(!bcs.isEmpty(0) && bcs.getItemTypeAt(0) == ItemType.FINISHED_RED_CAR) {
                		bcs.removeItemAt(0);
                		//TODO if finished car on belt
                		type.get(0).addElement();
                    	Thread.sleep(2000);
                    	//TODO GUI print "ROBOTARM NR: (ID) Action...."
                    	System.out.println("Finished Red Car");
                    }else {
                    	Thread.sleep(2000);
                    }
                    break;
                default:
                    return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void removeElement() {
        type.get(0).removeElement();
    }

    public void addElement() {
        type.get(0).addElement();
    }

    @Override
    public void run() {
        while (!stop) {
            work();
        }
    }

    public void addItemToConveyorBelt() {

    }
}
