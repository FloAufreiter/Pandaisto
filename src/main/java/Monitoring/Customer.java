package Monitoring;

import java.util.ArrayList;

public class Customer {

	private String name;
	private String password;
	private String address;
	
	public static ArrayList<Customer> CUSTOMERS = new ArrayList<Customer>();
	
	
	public Customer(String name,
	String password,
	String address) {
		
		this.setName(name);
		this.setPassword(password);
		this.setAddress(address);
		
		CUSTOMERS.add(this);
	}
	
	public static void AddCustomer(String name,
			String password,
			String address) {
		
		CUSTOMERS.add(new Customer(name, password, address));
	}
	
	 public static ArrayList<Customer> getCustomers() {
	        return (ArrayList<Customer>) CUSTOMERS.clone();
	 }

	public String getName() {
		return name;
	}

	 void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	 void setPassword(String password) {
		this.password = password;
	}

	 public String getAddress() {
		return address;
	}

	void setAddress(String address) {
		this.address = address;
	}
}
