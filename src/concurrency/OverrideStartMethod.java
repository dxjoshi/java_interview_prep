package concurrency;

class StartOverriden extends Thread {
    @Override
    public void start() {
        System.out.println("overriden start()");
    }

    @Override
    public void run() {
        System.out.println("overridden run()");
        super.run();
    }
}
public class OverrideStartMethod {
    public static void main(String[] args) {
        Thread thread = new StartOverriden();
        thread.start();
    }
}
