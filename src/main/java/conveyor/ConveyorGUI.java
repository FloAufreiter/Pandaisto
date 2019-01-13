package conveyor;

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class ConveyorGUI {
	private static JFrame frame;
	private static JTextField currPressureText;
	
	public static void openGUI(BeltSegment firstBelt, LubricantControl lc) {
		frame = new JFrame("Conveyor GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000,1000);
		JComponent conveyorcomp = new JComponent() {
			private static final long serialVersionUID = 2L;
			private BeltSegment belt = firstBelt;
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(Color.BLACK);
				belt.draw(g ,0,0);
			}
			
			public Dimension getPreferredSize() {
				return new Dimension(800,50);
			}
		};
		JPanel lubcomp = new JPanel() {
			private LubricantControl lubControl = lc;
			protected void paintComponent(Graphics g) {
				lubControl.draw(g,5,0);
			}
			public Dimension getPreferredSize() {
				return new Dimension(300,250);
			}
		};
		
		JPanel beltPanel = new JPanel();
		
		beltPanel.add(conveyorcomp);
		beltPanel.add(lubcomp);
		lubcomp.add(new JLabel("Min: " + Float.toString(lc.getMinPressureLevel())));
		
		currPressureText = new JTextField("Current: " + Float.toString(lc.getCurrentLubricantLevel()));
		
		lubcomp.add(currPressureText);
		frame.add(beltPanel);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void updateGUI() {
		
		frame.repaint();
	}
}
