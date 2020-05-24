package jp.gr.java_conf.tsyki.thread.lecture;

import java.util.ArrayList;
import java.util.List;

/**
 * デッドロックが発生しないようにした例
 *
 */
public class ThreadDeadLockSample {

	private Object lock = new Object();

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

	public void append(List<Integer> srcList, List<Integer> destList) {
		int srcHash = System.identityHashCode(srcList);
		int destHash = System.identityHashCode(srcList);
		if (srcHash < destHash) {
			synchronized (srcList) {
				// ここで待ってもデッドロックは発生しない
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for (Integer srcValue : srcList) {
					synchronized (destList) {
						destList.add(srcValue);
					}
				}
			}
		} else if (srcHash > destHash) {
			synchronized (destList) {
				synchronized (srcList) {
					// ここで待ってもデッドロックは発生しない
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (Integer srcValue : srcList) {
						destList.add(srcValue);
					}
				}
			}
		} else {
			synchronized (lock) {
				synchronized (srcList) {
					// ここで待ってもデッドロックは発生しない
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (Integer srcValue : srcList) {
						synchronized (destList) {
							destList.add(srcValue);
						}
					}
				}
			}
		}
	}
}