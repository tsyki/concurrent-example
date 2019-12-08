package jp.gr.java_conf.tsyki.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * メソッドに対するsynchronizedの例
 */
public class ThreadSyncMethodListSample {

	private List<Integer> list = new ArrayList<Integer>();

	public static void main(String[] args) {
		ThreadSyncMethodListSample sample = new ThreadSyncMethodListSample();
		sample.execute();
	}

	public void execute() {
		SampleThread thread1 = new SampleThread("1");
		SampleThread thread2 = new SampleThread("2");
		thread1.start();
		thread2.start();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(list);
	}

	// これはThreadSyncListSampleに対するロックなのでうまくいく
	private synchronized void add(int i) {
		list.add(i);
	}

	private class SampleThread extends Thread{

		private String name;

		public SampleThread(String name) {
			this.name = name;
		}
		@Override
		public void run() {
			for(int i=0;i<1000;i++) {
				add(i);
				//threadAdd(i);
			}
			System.out.println("thread " + name + " is end");
		}

		// これはスレッドのインスタンスに対するロックなので意味がない
		private synchronized void threadAdd(int i) {
			list.add(i);
		}
	}
}