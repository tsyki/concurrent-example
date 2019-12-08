package jp.gr.java_conf.tsyki.thread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * synchoronizedではなく専用クラスを使った例
 *
 */
public class ThreadConcurrentMapSample {

	private Map<String,String> list = new ConcurrentHashMap<String,String>();

	public static void main(String[] args) {
		ThreadConcurrentMapSample sample = new ThreadConcurrentMapSample();
		sample.execute();
	}

	public void execute() {
		SampleThread thread1 = new SampleThread("1");
		SampleThread thread2 = new SampleThread("2");
		thread1.start();
		thread2.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 1000個しか入らない
		System.out.println(list.size());
	}

	private class SampleThread extends Thread{

		private String name;

		public SampleThread(String name) {
			this.name = name;
		}
		@Override
		public void run() {
			for(int i=0;i<1000;i++) {
				String key = String.valueOf(i);
				// XXX putIfAbsentではなくこれでもうまくいってしまう
				if(!list.containsKey(key)) {
					list.put( key, name + i);
				}
			}
			System.out.println("thread " + name + " is end");
		}
	}
}