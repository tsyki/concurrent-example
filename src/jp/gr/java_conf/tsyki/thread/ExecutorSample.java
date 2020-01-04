package jp.gr.java_conf.tsyki.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Futureを利用した例
 */
public class ExecutorSample {

    public static void main(String[] args) {
        ExecutorSample sample = new ExecutorSample();
        sample.execute();
    }

    public void execute() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Callable<String> call1 = new Calc("1");
        Callable<String> call2 = new Calc("2");
        Future<String> future1 = executor.submit(call1);
        Future<String> future2 = executor.submit(call2);
        try {
            System.out.println(future1.get());
            System.out.println(future2.get(500, TimeUnit.NANOSECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private class Calc implements Callable<String> {

        private String name;

        public Calc(String name) {
            this.name = name;
        }

        @Override
        public String call() throws InterruptedException {
            Thread.sleep(1000);
            System.out.println("thread " + name + " is end");
            return name;
        }

    }
}