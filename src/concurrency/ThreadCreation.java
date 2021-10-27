package concurrency;

class CustomThread extends Thread {
    @Override
    public void run() {
        System.out.println(String.format("Extended thread class to create thread: %s", Thread.currentThread().getName()));
    }
}

public class ThreadCreation {

    public static void main(String[] args) {
        // by implementing Runnable interface
        Thread threadInstance = new Thread(()-> System.out.println(String.format("Implemented runnable interface to create thread: %s", Thread.currentThread().getName())));
        threadInstance.start();

        // by extending Thread class
        CustomThread threadInstanceTwo = new CustomThread();
        threadInstanceTwo.start();
    }
}
