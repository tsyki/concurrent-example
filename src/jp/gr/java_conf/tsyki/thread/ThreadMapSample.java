package jp.gr.java_conf.tsyki.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * 同じキーでputされてしまう例
 */
public class ThreadMapSample {

	private Map<String,String> map = new HashMap<String,String>();

	public static void main(String[] args) {
		ThreadMapSample sample = new ThreadMapSample();
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
		// 1000個しか入らないはずだが…
		System.out.println(map.size());
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
				// これはどっちでも挙動は同じのはず
				if(!map.containsKey(key)) {
					map.put( key, name + i);
				}
				//map.putIfAbsent( key, name + i);
			}
			System.out.println("thread " + name + " is end");
		}
	}
}