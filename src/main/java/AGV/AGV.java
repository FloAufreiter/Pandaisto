package AGV;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class AGV {
    private static TaskScheduler scheduler;
    private static Area area;
    static private DefaultTableModel model;
    static private JTable table;

    public static AGV getInstance() {
        return instance;
    }

    private static final AGV instance = new AGV();

    private AGV() {
        this.area = new Area();
        this.scheduler = new TaskScheduler();
    }

    public TaskScheduler getAGVTaskScheduler() {
        return scheduler;
    }

    public void startAGV() {
        openGui();
        new Thread(scheduler).start();
    }

    public void stopAGV() {
        scheduler.stopScheduler();
    }

    static void openGui() {
        JFrame frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,400);
        String[] colNames = {"ID", "Availability Status", "Current Location"};
        model = new DefaultTableModel(colNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(1000, 100));
        Font f = new Font("Arial", Font.BOLD, 25);
        header.setFont(f);
        Font f2 = new Font("Arial", Font.PLAIN, 15);
        table.setFont(f2);
        for(Forklift fork: scheduler.getForklifts()) {
            Object [] data = {fork.getId(), fork.getStatus(), fork.getCurrentLocation()};
            model.addRow(data);
        }
        frame.getContentPane().add(scrollPane);
        frame.setVisible(true);
    }


    static void updateGui(Forklift f) {
        model.setValueAt(f.getStatus() ,f.getId(),1);
        model.setValueAt(f.getCurrentLocation() ,f.getId(),2);
        table.repaint();
    }
}
