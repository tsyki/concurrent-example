package jp.gr.java_conf.tsyki.thread.lecture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 複数スレッドからのインクリメントで数値が正しく加算されない例のExecutor版
 *
 */
public class ExecutorIncrementSample {

	private static class CountHolder {
		private int count;

		public void add() {
			count++;
		}

		public int getCount() {
			return count;
		}
	}

	private static class Counter implements Callable<Void> {

		private CountHolder holder;

		public Counter(CountHolder holder) {
			this.holder = holder;
		}

		@Override
		public Void call() {
			for (int i = 0; i < 100000; i++) {
				holder.add();
			}
			return null;
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		CountHolder holder = new CountHolder();
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<Void> future1 = executor.submit(new Counter(holder));
		Future<Void> future2 = executor.submit(new Counter(holder));
		future1.get();
		future2.get();
		System.out.println(holder.getCount());
	}

}