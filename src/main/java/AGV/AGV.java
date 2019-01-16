package AGV;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;


/**
 * Interface for the other subsystems
 * provided as a singleton
 * AGV needs to be started explicitly
 * then Tasks can be added by using the function createTask of the TaskScheduler
 */
public class AGV {
    private static TaskScheduler scheduler;

    private static Thread schedulerThread;
    static private DefaultTableModel model;
    static private JTable table;

    public static AGV getInstance() {
        return instance;
    }

    private static final AGV instance = new AGV();

    private AGV() {
        scheduler = new TaskScheduler();
    }

    /**
     * @return the TaskScheduler of this AGV system
     */
    public TaskScheduler getAGVTaskScheduler() {
        return scheduler;
    }

    /**
     * Starts the whole AGV system
     * Opens the GUI for scheduler status
     * Starts the TaskScheduler, which is then ready to accept Tasks
     */
    public void startAGV() {
        openGui();
        schedulerThread = new Thread(scheduler);
        schedulerThread.start();
    }

    /**
     * Stops the whole AGV system
     * Remaining tasks in queue are still executed
     * Thread stops, when all Tasks are done
     */
    public void stopAGV() {
        scheduler.stopScheduler();
    }

    /**
     * opens the GUI of the AGV
     */
    private static void openGui() {
        JFrame frame = new JFrame("AGV");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 400);
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
        for (Forklift fork : scheduler.getForklifts()) {
            Object[] data = {fork.getId(), fork.getStatus(), fork.getCurrentLocation()};
            model.addRow(data);
        }
        frame.getContentPane().add(scrollPane);
        frame.setVisible(true);
    }

    public Thread getSchedulerThread() {
        return schedulerThread;
    }


    /**
     * updates the information about a forklift in the GUI
     *
     * @param f
     */
    void updateGui(Forklift f) {
        model.setValueAt(f.getStatus(), f.getId(), 1);
        model.setValueAt(f.getCurrentLocation(), f.getId(), 2);
        table.repaint();
    }
}
