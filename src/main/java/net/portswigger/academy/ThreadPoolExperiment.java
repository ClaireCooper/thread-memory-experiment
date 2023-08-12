package net.portswigger.academy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolExperiment {
    public static void main(String[] args) throws InterruptedException {
        int threadPoolSize = 1024;
        int processesToRun = 2048;
        int megabytesToPutInHeap = 550;

        System.out.println("Test started...");
        byte[] heapFiller = new byte[megabytesToPutInHeap*1024*1024];
        System.out.println("Filled heap with " + megabytesToPutInHeap +"MB");
        Thread.sleep(10000);

        Callable<Boolean> threadStackFillerTask = () -> {
            try {
                fillStack(0L);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        };

        System.out.println("Starting thread execution");
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        List<Future<Boolean>> outcomes = new ArrayList<>();

        try {
            for (int i = 1; i <= processesToRun; i++) {
                outcomes.add(executor.submit(threadStackFillerTask));
                System.out.println("Submitted: " + i);
//                System.out.println(outcomes.get(i-1).get());
//                IMPORTANT when this ^ is uncommented it still crashes,
//                so it seems that it fills empty slots before closed ones
//                (because when doing more processes than threads it will
//                happily replace closed threads' stacks)
                Thread.sleep(10);
            }
            for (int i = 0; i < processesToRun; i++) {
                System.out.println(i + 1 + " " + outcomes.get(i).get());
            }
//            Thread.sleep(50000);
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        finally {
//            executor.shutdownNow();
            executor.shutdown();
        }
    }

    static private int fillStack(long n) throws InterruptedException {
        if (n==5000)
        {
            System.out.println("Filled stack in " + Thread.currentThread().getName());
//            Thread.sleep(30*1000);
            return 0;
        }
        return fillStack(n+1);
    }

}