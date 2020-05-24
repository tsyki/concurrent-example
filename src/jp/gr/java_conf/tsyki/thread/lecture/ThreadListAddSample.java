package jp.gr.java_conf.tsyki.thread.lecture;

import java.util.ArrayList;
import java.util.List;

/**
 * 複数スレッドからの要素追加でこける例
 *
 */
public class ThreadListAddSample {

	private static class CountHolder {
		private List<Integer> list = new ArrayList<>();

		public void add(Integer i) {
			list.add(i);
		}

		public List<Integer> getList() {
			return list;
		}
	}

	private static class Counter implements Runnable {

		private CountHolder holder;

		public Counter(CountHolder holder) {
			this.holder = holder;
		}

		@Override
		public void run() {
			for (int i = 0; i < 1000000; i++) {
				holder.add(i);
			}
			System.out.println("end add");
		}
	}

	public static void main(String[] args) throws InterruptedException {
		CountHolder holder = new CountHolder();
		Thread thread1 = new Thread(new Counter(holder));
		Thread thread2 = new Thread(new Counter(holder));
		thread1.start();
		thread2.start();
		Thread.sleep(1000); //スレッドの処理が終わるまで1秒待つ
		System.out.println(holder.getList().size());
	}

}