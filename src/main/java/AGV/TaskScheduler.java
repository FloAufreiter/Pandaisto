package AGV;

import shared.Commodity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TaskScheduler implements Runnable {

    public void stopAgv() {
        STOP_AGV = true;
    }

    public void startAGV() {
        STOP_AGV = false;
        run();
    }

    private static boolean STOP_AGV = false;

    private static final TaskScheduler instance = new TaskScheduler();


    private static final ArrayList<Forklift> FORKLIFTS = new ArrayList<>();

    private static BlockingQueue<Task> TASKS = new ArrayBlockingQueue<>(100);

    private TaskScheduler() {
        initForklifts();
        instance.run();
    }

    private static void initForklifts() {
        for (int i = 0; i < 10; i++) {
            FORKLIFTS.add(new Forklift());
        }
    }

    private static void scheduleTasksToForklift() {
        List<Forklift> freeLifters = getAvailableForklifts();
        while (!TASKS.isEmpty() && !freeLifters.isEmpty()) {
            try {
                double min_cost = Double.MAX_VALUE;
                Task t = TASKS.take();
                Forklift nearest_free = null;
                for (Forklift f : freeLifters) {
                    Double d = Area.getMinimalCostFrom(f.getCurrentLocation(), t.getLocationA());
                    if (d < min_cost && f.canAcceptTask(t)) {
                        min_cost = d;
                        nearest_free = f;
                    }
                }
                nearest_free.addTask(t);
                if (nearest_free.isFullyLoaded()) {
                    nearest_free.execute();
                    freeLifters.remove(nearest_free);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!freeLifters.isEmpty()) {
            for (Forklift f : freeLifters) {
                f.execute();
            }
        }
    }

    private static List<Forklift> getAvailableForklifts() {
        List<Forklift> freeLifters = new ArrayList<>();
        for (Forklift f : FORKLIFTS) {
            if (f.getStatus() == Forklift.Status.available) {
                freeLifters.add(f);
            }
        }
        return freeLifters;
    }

    public static synchronized boolean createTask(Location location1, Location location2, Commodity commodity) {
        if (TASKS.size() == 100) {
            return false;
        } else {
            Task t = new Task(location1, location2, commodity);
            try {
                TASKS.add(t);
                return true;
            } catch (IllegalStateException e) {
                return false;
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (!TASKS.isEmpty()) {
                scheduleTasksToForklift();
            }
        }
    }
}
