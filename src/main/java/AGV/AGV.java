package AGV;

public class AGV {
    private TaskScheduler scheduler;
    private Area area;

    AGV() {
        this.area = new Area();
        this.scheduler = new TaskScheduler();
    }

    public TaskScheduler getAGVTaskScheduler() {
        return scheduler;
    }

    public void startAGV() {
        new Thread(scheduler).start();
    }

    public void stopAGV() {
        scheduler.stopScheduler();
    }
}
