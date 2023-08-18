package net.portswigger.academy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolExperiment {
    public static void main(String[] args) throws InterruptedException {
        int threadPoolSize = 1024;
        int processesToRun = 2*1024;
        int megabytesToPutInHeap = 650;

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
        Callable<Boolean> threadStackMediumTask = () -> {
            halfFillStack(0L);
            return true;
        };
        Callable<Boolean> threadStackMediumishTask = () -> {
            partFillStack(0L);
            return true;
        };
        Callable<Boolean> threadStackLightTask = () -> {
            doNothing(0);
            return true;
        };


        System.out.println("Starting thread execution");
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
        System.out.println(executor.getActiveCount() + " active threads in pool");
//        Thread.sleep(10000);
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

            for (Future<Boolean> o:outcomes
                 ) {
                System.out.println(outcomes.indexOf(o)+1 + " " + o.get());
            }
            System.out.println(executor.getActiveCount() + " active threads in pool");
            Thread.sleep(10000);
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        finally {
//            executor.shutdownNow();
            System.out.println(executor.getActiveCount() + " active threads in pool");//0 active but thread stacks still there
            executor.shutdown();
            System.out.println("Shutdown threads");
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

    static private int partFillStack(long n) throws InterruptedException {
        if (n==3000)
        {
            System.out.println("Part filled stack in " + Thread.currentThread().getName());
//            Thread.sleep(30*1000);
            return 0;
        }
        return partFillStack(n+1);
    }

    static private int halfFillStack(long n) throws InterruptedException {
        if (n==2500)
        {
            System.out.println("Part filled stack in " + Thread.currentThread().getName());
//            Thread.sleep(30*1000);
            return 0;
        }
        return halfFillStack(n+1);
    }

    static private int doNothing(int n)
    {
        return n;
    }
}