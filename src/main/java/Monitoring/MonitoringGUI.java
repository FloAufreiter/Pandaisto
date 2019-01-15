package Monitoring;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import java.awt.GridLayout;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.awt.FlowLayout;


import shared.ItemType;
import warehouse.Database;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JList;

public class MonitoringGUI {

	public JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField lblOngoingOrders = new JTextField("Ongoing Customer Orders: 0");

	private JTextField lblOngoingComponentOrders = new JTextField("Ongoing Component Orders: 0");
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
			Database.getInstance().initTestDB();
		} catch (SQLException e1) {
			try {
				Database.getInstance().deleteTestDB();
				Database.getInstance().initTestDB();
			} catch (SQLException e) {
				System.exit(-1); //can't setup database
			}
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MonitoringGUI window = new MonitoringGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MonitoringGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lblRedCars = new JLabel("Red Cars:");

		JLabel lblBlueCars = new JLabel("Blue Cars:");

		textField = new JTextField();
		textField.setText("0");
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setText("0");
		textField_1.setColumns(10);

		JButton btnOrder = new JButton("Order");
		
		JLabel lblName = new JLabel("Name:");
		
		textField_2 = new JTextField();
		textField_2.setText("<Customer Name>");
		textField_2.setColumns(10);
		
		lblOngoingComponentOrders.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				
				lblOngoingOrders.setText("Ongoing Customer Orders: " + Monitor.getInstance().getNumberOfOngoingCustomerOrders());
				lblOngoingComponentOrders.setText("Ongoing Component Orders: " + Monitor.getInstance().getNumberOfOngoingComponentsOrders());			
				
			}
			
			
			
		});
		
		lblOngoingOrders.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				
				lblOngoingOrders.setText("Ongoing Customer Orders: " + Monitor.getInstance().getNumberOfOngoingCustomerOrders());
				lblOngoingComponentOrders.setText("Ongoing Component Orders: " + Monitor.getInstance().getNumberOfOngoingComponentsOrders());			
				
			}
			
			
			
		});
		
		JList list = new JList();

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(24)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblOngoingOrders)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(lblRedCars)
									.addComponent(lblBlueCars))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(228))
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(lblName)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
								.addGap(171)
								.addComponent(list, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE)
								.addGap(71)))
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
							.addComponent(btnOrder)
							.addComponent(lblOngoingComponentOrders)))
					.addGap(456))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(72)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblName)
						.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(list, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRedCars)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBlueCars)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnOrder)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblOngoingOrders)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblOngoingComponentOrders)
					.addGap(18))
		);
		frame.getContentPane().setLayout(groupLayout);

		btnOrder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Monitor monitor = Monitor.getInstance();
				// TODO: Ask warehouse for availability

				int result = JOptionPane.showConfirmDialog(frame, "Cars will be delivered in" + Math.max(monitor.getDeliveryDays(Integer.parseInt(textField.getText()), ItemType.FINISHED_RED_CAR), monitor.getDeliveryDays(Integer.parseInt(textField.getText()), ItemType.FINISHED_BLUE_CAR)) + " days");
				
				

				if (result == JOptionPane.OK_OPTION) {
					// order red car
					if (textField.getText() != "<Amount>" && Integer.parseInt(textField.getText()) > 0) {
						//Monitor.orderComponents(ItemType.FINISHED_RED_CAR, Integer.parseInt(textField.getText()));
						
						monitor.createCustomerOrder(Integer.parseInt(textField.getText()), ItemType.FINISHED_RED_CAR, new Customer(textField_2.getText(), "test", "test"));
					}
					// order blue car
					if (textField_1.getText() != "<Amount>" && Integer.parseInt(textField_1.getText()) > 0) {
						//Monitor.orderComponents(ItemType.FINISHED_BLUE_CAR, Integer.parseInt(textField_1.getText()));
						monitor.createCustomerOrder(Integer.parseInt(textField.getText()), ItemType.FINISHED_BLUE_CAR, new Customer(textField_2.getText(), "test", "test"));
						
					}

					lblOngoingOrders.setText("Ongoing Customer Orders:" + Monitor.getInstance().getNumberOfOngoingCustomerOrders());
					lblOngoingComponentOrders.setText("Ongoing Component Orders:" + Monitor.getInstance().getNumberOfOngoingComponentsOrders());
				}

			}
		});
	}
	
	public void Refresh() {
		lblOngoingOrders.setText("Ongoing Customer Orders:" + Monitor.getInstance().getNumberOfOngoingCustomerOrders());
		lblOngoingComponentOrders.setText("Ongoing Component Orders:" + Monitor.getInstance().getNumberOfOngoingComponentsOrders());		
		frame.repaint();
	}
}
