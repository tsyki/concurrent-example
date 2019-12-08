package jp.gr.java_conf.tsyki.thread;

/**
 * 複数スレッドからのインクリメントで数値が正しく加算されない例(だが実際はうまくいってしまう)
 *
 */
public class ThreadIncrementSample {

	private int count = 0;

	public static void main(String[] args) {
		ThreadIncrementSample sample = new ThreadIncrementSample();
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
		System.out.println(count);
	}

	private class SampleThread extends Thread{

		private String name;

		public SampleThread(String name) {
			this.name = name;
		}
		@Override
		public void run() {
			for(int i=0;i<1000;i++) {
				count++;
			}
			System.out.println("thread " + name + " is end");
		}
	}
}