package jp.gr.java_conf.tsyki.thread.lecture;

/**
 * 複数スレッドからのインクリメントで数値が正しく加算されない例
 *
 */
public class ThreadIncrementSample {

	private static class CountHolder {
		private int count;

		public void add() {
			count++;
		}

		public int getCount() {
			return count;
		}
	}

	private static class Counter implements Runnable {

		private CountHolder holder;

		public Counter(CountHolder holder) {
			this.holder = holder;
		}

		@Override
		public void run() {
			for (int i = 0; i < 100000; i++) {
				holder.add();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		CountHolder holder = new CountHolder();
		Thread thread1 = new Thread(new Counter(holder));
		Thread thread2 = new Thread(new Counter(holder));
		thread1.start();
		thread2.start();
		Thread.sleep(1000); //スレッドの処理が終わるまで適当に1秒待つ
		System.out.println(holder.getCount());
	}

}