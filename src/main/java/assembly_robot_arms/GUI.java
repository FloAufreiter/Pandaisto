package assembly_robot_arms;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import conveyor.BeltControlleSystem;

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;

	public GUI() {
		initGUI();
	}
	
	private void initGUI() {
		setTitle("Robot arms");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		BeltControlleSystem belt = BeltControlleSystem.getInstance(10,100);
		belt.start();
		Scheduler s = Scheduler.getInstance(belt);
		s.start();
		Thread t1 = new Thread(s);
		t1.start();
		t1 = new Thread(belt);
		t1.start();
		
		
		/*GUI gui = new GUI();
		
		// Die Daten für das Table
		String[][] data = new String[][]{
						{"a", "b", "c", "d"},
						{"e", "f", "g", "h"},
						{"i", "j", "k", "l"}
		};
				
		// Die Column-Titles
		String[] title = new String[]{
				"Grab Arm 1", "Grab Arm 2", "Grab Arm 3", "Grab Arm 4"
		};
				
		// Das JTable initialisieren
		JTable table = new JTable( data, title );
		JScrollPane tableContainer = new JScrollPane(table);
		JPanel p = new JPanel();
		
	    p.setLayout(new BorderLayout());
	    p.add(tableContainer, BorderLayout.NORTH);
	    
		gui.getContentPane().add(p);
		gui.pack();
		gui.setVisible(true);*/
    }
}
