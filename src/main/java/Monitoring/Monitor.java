package Monitoring;

import java.util.ArrayList;

import shared.ItemType;

public class Monitor implements Runnable{
	
	private static ArrayList<ComponentOrder> ONGOING_ORDERS = new ArrayList<ComponentOrder>();
	
	private final Monitor monitor = new Monitor(); 
	
	private static boolean STOP = true;
	
	
	
	
	private static void createComponentOrder(int amount, ItemType itemType) {		
		ONGOING_ORDERS.add(new ComponentOrder(amount, itemType));			
	}
	
	private Monitor() {
		
	}
	
	// Singleton - because only one Monitor is allowed.
	public Monitor getInstance() {		
		return monitor;
	}
	
	
	@Override
	public void run() {
		while(!STOP) {
			if(!ONGOING_ORDERS.isEmpty()) {
				order();
			}
			
			
		}
		
		
		
	}
	
	
	
	public static void start() {
		STOP = false;
	}
	
	public static void stop() {
		STOP = true;
	}
	
	public static void orderComponents(ItemType itemType, int amount) {
		createComponentOrder(amount, itemType);
	}
	
	private static void order() {
		
		
		
		
	}
	
	
	private class OrderRunner implements Runnable{

		@Override
		public void run() {
			try {
				Thread.sleep(10000);
				
				AGV.
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
			
			
		}
		
		
		
	}
	
	
}
