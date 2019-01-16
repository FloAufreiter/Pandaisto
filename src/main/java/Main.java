import Monitoring.Monitor;
import warehouse.Database;

import java.sql.SQLException;

public class Main {
    //THIS SHOULD BE IN MONITOR BUT DEPENDENCIES ARE FUCKED
    public static void main(String[] args) {
        try {
            Database.getInstance().initTestDB();
        } catch (SQLException e1) {
            e1.printStackTrace();
            System.exit(-1); //SHIT
        }
        Monitor.getInstance();
    }
}
