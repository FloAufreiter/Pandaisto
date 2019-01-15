package warehouse;

import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import shared.ItemType;

public class WarehouseGUI implements DBListener{


    static private DefaultTableModel model;
    static private JTable table;
    
	void openGui() {
		
        JFrame frame = new JFrame("Warehouse");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600,600);
        String[] colNames = {"Shelf Number", "Item"};
        model = new DefaultTableModel(colNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(1000, 100));
        Font f = new Font("Arial", Font.BOLD, 25);
        header.setFont(f);
        Font f2 = new Font("Arial", Font.PLAIN, 15);
        table.setFont(f2);

        try {
			Database.getInstance().addListener(this);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        for(int i = 0; i < 60; i++) {
        	ItemType item;
			try {
				item = Database.getInstance().getItemAt(i);
	            Object [] data = {i, item == null ? "EMPTY" : item.toString()};
	            model.addRow(data);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        frame.getContentPane().add(scrollPane);
        frame.setVisible(true);
    }

	@Override
	public void notifyEvent(DBEvent e) {
		if(e.eType.equals(EventType.ItemAdded)) {
			model.setValueAt(e.itemType.toString(), e.id, 1);
		} else {
			model.setValueAt("EMPTY", e.id, 1);
		}
	}
}
