package jp.gr.java_conf.tsyki.thread;

/**
 * sleep時のInterruptedExceptionを発生させる例
 */
public class ThreadSleepSample {

	private int count = 0;

	public static void main(String[] args) {
		ThreadSleepSample sample = new ThreadSleepSample();
		sample.execute();
	}

	public void execute() {
		SampleThread thread1 = new SampleThread("1");
		SampleThread thread2 = new SampleThread("2");
		thread1.start();
		thread2.start();
		thread1.interrupt();
		try {
			Thread.sleep(100);
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
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				System.out.println("thread " + name + " interrupted");
				return;
			}
			for(int i=0;i<1000;i++) {
				count++;
			}

			System.out.println("thread " + name + " is end");
		}
	}
}