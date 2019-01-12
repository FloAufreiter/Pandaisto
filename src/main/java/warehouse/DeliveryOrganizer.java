package warehouse;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;


/**
 * Class responsible for handling efficient storage of outgoing items according to delivery dates
 * @author Tom Peham
 *
 */
public class DeliveryOrganizer extends Organizer{

	/**
	 * Record storing necessary Data for Delivery
	 */
	private class Delivery implements Comparable<Delivery>{
		public Delivery(Date deliveryDate, int numRedTrucks, int numBlueCars, int loadingDockID) {
			this.deliveryDate = deliveryDate;
			this.numRedTrucks = numRedTrucks;
			this.numBlueCars = numBlueCars;
			this.loadingDockID = loadingDockID;
		}
		public Date deliveryDate;
		public int numRedTrucks;
		public int numBlueCars;
		public int loadingDockID;
		
		@Override
		public int compareTo(Delivery del) {
			return deliveryDate.compareTo(del.deliveryDate);
		}
	}
	
	//Priority queue ordering deliveries according to date of delivery
	private final PriorityQueue<Delivery> deliveries = new PriorityQueue<>();
	
	@Override
	public void requestReording() {
		// TODO
		
	}

	@Override
	public void calcEfficientItemDistribution() {
		// TODO 
	}

}
