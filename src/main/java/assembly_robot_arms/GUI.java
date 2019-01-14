package assembly_robot_arms;

import javax.swing.JFrame;

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
}
