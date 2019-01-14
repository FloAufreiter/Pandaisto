import AGV.AGV;
import AGV.Location;
import Monitoring.Monitor;
import warehouse.Database;
import AGV.Area;

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


        //=====STUPID TEST====
//        Location l1 = Area.getLocation(Location.LocationType.FLOOR_SHELF, 0);
//        Location r1 = Area.getLocation(Location.LocationType.PRODUCTION_LINE, 1);
//        Location l2 = Area.getLocation(Location.LocationType.TOP_SHELF1, 1);
//        Location l3 = Area.getLocation(Location.LocationType.FLOOR_SHELF, 33);

//        agv.getAGVTaskScheduler().createTask(l1, r1, ItemType.SCREW);
//        try {
//            Thread.sleep(1000);
//            //agv.getAGVTaskScheduler().createTask(l2, l3, ItemType.SCREW);
//            Thread.sleep(20000);//sleep to make sure 2 forklifts are started
//
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        //====STUPID TEST END===

//        while(true) {
//
//        }

    }
}
