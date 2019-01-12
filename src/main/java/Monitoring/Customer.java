package Monitoring;

import java.util.ArrayList;

public class Customer {

	String name;
	String password;
	String address;
	
	public static ArrayList<Customer> customers = new ArrayList<Customer>();
	
	
	private Customer(String name,
	String password,
	String address) {
		
		this.name = name;
		this.password = password;
		this.address = address;
	}
	
	public static void AddCustomer(String name,
			String password,
			String address) {
		
		customers.add(new Customer(name, password, address));
	}
}
