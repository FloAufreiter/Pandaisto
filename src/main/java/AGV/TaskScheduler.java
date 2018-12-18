package AGV;

import shared.Commodity;
import shared.Location;
import shared.Task;

import java.util.ArrayList;

public class TaskScheduler {

    private static boolean schedulingBlocked = false;

    private static final ArrayList<Forklifter> forklifters = new ArrayList<>();

    public TaskScheduler() {
        initForklifters();
    }

    private static void initForklifters() {
        for (int i = 0; i < 10; i++) {
            forklifters.add(new Forklifter());
        }
    }

    private void scheduleTask(Task task) {

    }

    private void blockScheduling() {
        schedulingBlocked = true;
    }

    private Forklifter getAvailableForklifter(Task task) { //TODO get nearest available Forklifter for given task
        return null;
    }

    private boolean isSchedulable(Task task) {
        return false;
    }

    public boolean createTask(Location location1, Location location2, Commodity cmdty) {
        return false;
    }
}
