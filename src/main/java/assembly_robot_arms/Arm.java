package assembly_robot_arms;

import conveyor.BeltControlSystem;
import shared.ItemType;
import warehouse.MessagingInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Monitoring.Monitor;

public class Arm implements Runnable {
    private static int IDS = 0;
    private int id;
    private BeltControlSystem bcs;
    private final int beltID;
    private boolean waitingForItem;
    
    protected List<RobotStorage> type;
    boolean stop = false;

    
    public List<RobotStorage> getRobotStorages() {
        return type;
    }

    public void stopArm() {
        stop = true;
    }

    public Arm(BeltControlSystem bcs, int beltID) {
        this.type = new ArrayList<>();
        this.id = IDS++;
        this.beltID = beltID;
        this.bcs = bcs;
        this.waitingForItem = false;
    }

    public void addStorageType(RobotStorage type) {
        this.type.add(type);
    }

    public void work() {
        try {
            if (type.isEmpty()) return;
	            if(type.get(0).type != ItemType.FINISHED_BLUE_CAR && type.get(0).type != ItemType.FINISHED_RED_CAR)
	            {	
	            if(waitingForItem && type.get(0).nrOfElements >= type.get(0).minNrOfElements) {
	            	waitingForItem = false;
	            	bcs.unlockBeltAt(beltID);
	            } else if(! waitingForItem && type.get(0).nrOfElements < type.get(0).minNrOfElements){
	            	waitingForItem = true;
	            	bcs.lockBeltAt(beltID);
	            	System.out.println("ORDERED FOR PRODUCTION LINE: " + type.get(0).type);
	            	for(RobotStorage s: type) {
	            			Monitor.getInstance().requestItemForProductionLine(this.id, s.type);
	            	}
	            }
            }
            if(waitingForItem) {
            	Thread.sleep(1000);
            } else {
	            switch (type.get(0).getType()) {
	                case CAR_BODY:
	                    //TODO Communication with Conveyorbelt
	                	Thread.sleep(3000);
	                	if (type.get(0).getNrOfElements() > 0 && bcs.isEmpty(beltID)) {
	                        type.get(0).removeElement();
	                        System.out.println("Move Car Body to Conveyor");
	                        bcs.addItemAt(beltID, ItemType.CAR_BODY);
	                    }	                    
	                    break;
	                case SCREW:
	                case WHEEL:
	                	if(!bcs.isEmpty(beltID) && bcs.getItemTypeAt(beltID) == ItemType.CAR_BODY) {
	                		bcs.removeItemAt(beltID);
	                		bcs.lockBeltAt(beltID);
	                	
		                    Thread.sleep(2000);
		                    if (type.get(0).getNrOfElements() > 0) {
		                    	System.out.println("WHEEL REMOVED");
		                        type.get(0).removeElement();
		                    }
		                    if (type.get(1).getNrOfElements() > 0) {
		                        type.get(1).removeElement();
		                    }
		                    System.out.println("Add Wheels to Car");
		                    bcs.addItemAt(beltID, ItemType.CAR_BODY_WHEELS);
		                }
		                Thread.sleep(250);
		                
	                	
	                    break;
	                case RED_PAINT:
	                	if(!bcs.isEmpty(beltID) && bcs.getItemTypeAt(beltID) == ItemType.CAR_BODY_WHEELS) {
	                		bcs.lockBeltAt(beltID);
	                	
		                	Thread.sleep(2000);
		                    if (type.get(0).getNrOfElements() > 0) {
		                        type.get(0).removeElement();
		                    }
		                    bcs.removeItemAt(beltID);
	                		bcs.addItemAt(beltID, ItemType.FINISHED_RED_CAR);
		                }
	                    break;
	                case BLUE_PAINT:
	                	if(!bcs.isEmpty(beltID) && bcs.getItemTypeAt(beltID) == ItemType.CAR_BODY_WHEELS) {
	                		bcs.lockBeltAt(beltID);
	                	
	                		Thread.sleep(2000);
	                		if (type.get(0).getNrOfElements() > 0) {
	                			type.get(0).removeElement();
	                		}
		                    bcs.removeItemAt(beltID);
	                		bcs.addItemAt(beltID, ItemType.FINISHED_BLUE_CAR);
	                	}
	                    break;
	                case REMOTE:
	                    if(type.get(0).getNrOfElements() > 0 && !bcs.isEmpty(beltID)) {
	                        if( bcs.getItemTypeAt(beltID) == ItemType.FINISHED_RED_CAR) {
	                        	bcs.lockBeltAt(beltID);
		                    
	                        	bcs.removeItemAt(beltID);
	                        	Thread.sleep(2000);
			                    
	                        	bcs.addItemAt(beltID, ItemType.FINISHED_RED_CAR_REMOTE);

		                    	type.get(0).removeElement();
	                        }else if(bcs.getItemTypeAt(beltID) == ItemType.FINISHED_BLUE_CAR) {
	                        	bcs.lockBeltAt(beltID);
		                    
	                        	bcs.removeItemAt(beltID);
	                        	Thread.sleep(2000);
			                    
	                        	bcs.addItemAt(beltID, ItemType.FINISHED_BLUE_CAR_REMOTE);	                        	

		                    	type.get(0).removeElement();
	                        }
	                    }
	                    break;
	                case FINISHED_BLUE_CAR:
	                	if(!bcs.isEmpty(beltID) && bcs.getItemTypeAt(beltID) == ItemType.FINISHED_BLUE_CAR_REMOTE) {
	                		bcs.removeItemAt(beltID);
	                		//TODO if finished car on belt
	                		type.get(0).addElement();
	                    	Thread.sleep(2000);
	                    	//TODO GUI print "ROBOTARM NR: (ID) Action...."
	                    	System.out.println("Finished Blue Car");
	                    	if(type.get(0).getNrOfElements() >= type.get(0).maxNrOfElements) {
	                    		Monitor.getInstance().removeItemForProductionLine(id, type.get(0).maxNrOfElements, type.get(0).type);
	                    		type.get(0).nrOfElements = 0;
	                    	}
	                    }else {
	                    	Thread.sleep(2000);
	                    }
	                    break;
	                case FINISHED_RED_CAR:
	                	if(!bcs.isEmpty(beltID) && bcs.getItemTypeAt(beltID) == ItemType.FINISHED_RED_CAR_REMOTE) {
	                		bcs.removeItemAt(beltID);
	                		//TODO if finished car on belt
	                		type.get(0).addElement();
	                    	Thread.sleep(2000);
	                    	//TODO GUI print "ROBOTARM NR: (ID) Action...."
	                    	System.out.println("Finished Red Car");
	                    	if(type.get(0).getNrOfElements() >= type.get(0).maxNrOfElements) {
	                    		Monitor.getInstance().removeItemForProductionLine(id, type.get(0).maxNrOfElements, type.get(0).type);
	                    		type.get(0).nrOfElements = 0;
	                    	}
	                    }else {
	                    	Thread.sleep(2000);
	                    }
	                    break;
	                default:
	                    return;
	            }
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

    public void addElement(ItemType item) {
        for(RobotStorage s: type) {
        	if(s.type.equals(item)) s.addElement();
        }
    }

    @Override
    public void run() {
        System.out.println("ROBOT ARM " + this.id + " STARTED");
    	while (!stop) {
            work();
        }
    }

    public void addItemToConveyorBelt() {

    }
}
