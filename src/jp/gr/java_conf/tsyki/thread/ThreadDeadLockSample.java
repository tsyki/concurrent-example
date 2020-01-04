package jp.gr.java_conf.tsyki.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * デッドロックが発生する例
 *
 */
public class ThreadDeadLockSample {

    public static void main(String[] args) {
        ThreadDeadLockSample sample = new ThreadDeadLockSample();
        sample.execute();
    }

    public void execute() {
        List<Integer> list1 = new ArrayList<Integer>();
        list1.add(1);
        List<Integer> list2 = new ArrayList<Integer>();
        list2.add(2);

        SampleThread thread1 = new SampleThread("1", list1, list2);
        SampleThread thread2 = new SampleThread("2", list2, list1);
        thread1.start();
        thread2.start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(list1);
        System.out.println(list2);
    }

    private class SampleThread extends Thread {

        private String name;
        private List<Integer> srcList;
        private List<Integer> destList;

        public SampleThread(String name, List<Integer> srcList, List<Integer> destList) {
            this.name = name;
            this.srcList = srcList;
            this.destList = destList;
        }

        @Override
        public void run() {
            append(srcList, destList);
            System.out.println("thread " + name + " is end");
        }
    }

    public static void append(List<Integer> srcList, List<Integer> destList) {
        synchronized (srcList) {
            for (Integer srcValue : srcList) {
                synchronized (destList) {
                    destList.add(srcValue);
                }
            }
        }
    }
}