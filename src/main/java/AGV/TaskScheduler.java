package AGV;

import shared.Commodity;
import shared.ItemContainer;
import shared.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TaskScheduler implements Runnable {

    public void stopScheduler() {
        STOP_AGV = true;
        System.out.println(tasks.isEmpty());
    }

    private boolean STOP_AGV = false;

    ArrayList<Forklift> getForklifts() {
        return forklifts;
    }

    private ArrayList<Forklift> forklifts = new ArrayList<>();

    private BlockingQueue<Task> tasks = new ArrayBlockingQueue<>(100);

    TaskScheduler() {
        initForklifts();
    }

    private void initForklifts() {
        for (int i = 0; i < 10; i++) {
            forklifts.add(new Forklift());
        }
    }

    private void scheduleTasksToForklift() {
        List<Forklift> freeLifters = getAvailableForklifts();
        while (!tasks.isEmpty() && !freeLifters.isEmpty()) {
            try {
                double min_cost = Double.MAX_VALUE;
                Task t = tasks.take();
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

    private List<Forklift> getAvailableForklifts() {
        List<Forklift> freeLifters = new ArrayList<>();
        for (Forklift f : forklifts) {
            if (f.getStatus() == Forklift.Status.AVAILABLE) {
                freeLifters.add(f);
            }
        }
        return freeLifters;
    }

    public synchronized boolean createTask(Location location1, Location location2, ItemType itemType) {
        if (tasks.size() == 100) {
            return false;
        } else {
            //TODO change Commodity to ItemContainer
            Task t = new Task(location1, location2, itemType);
            try {
                tasks.add(t);
                return true;
            } catch (IllegalStateException e) {
                return false;
            }
        }
    }


    @Override
    public void run() {
        while (!STOP_AGV || !tasks.isEmpty()) {
            if (!tasks.isEmpty()) {
                scheduleTasksToForklift();
            }
        }
    }
}
