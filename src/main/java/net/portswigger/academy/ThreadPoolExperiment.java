package net.portswigger.academy;
import java.util.concurrent.*;

public class ThreadPoolExperiment {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Test started");

        Runnable runnableTask = () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        int poolSize = 1024;
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        for (int i = 1; i <= poolSize; i++)
        {
            executor.execute(runnableTask);
            System.out.println("Executing " + i);
        }
        Thread.sleep(5000);
        executor.shutdown();

    }
}