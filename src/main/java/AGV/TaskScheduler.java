package AGV;

import shared.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TaskScheduler implements Runnable {

    ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    public void stopScheduler() {
        STOP_AGV = true;
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
                System.out.println("ADDED TASK " + t.getId());
                nearest_free.setStatus(Forklift.Status.IN_USE);
                if (nearest_free.isFullyLoaded()) {
                	
                    executorService.execute(nearest_free);
                    freeLifters.remove(nearest_free);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!freeLifters.isEmpty()) {
            for (Forklift f : freeLifters) {
                if(!f.loadingRouteEmpty()) {
                    executorService.execute(f);
                }
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
    	System.out.println("TASK CREATED");
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
        while (!STOP_AGV || !tasks.isEmpty() ) {
                scheduleTasksToForklift();
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
