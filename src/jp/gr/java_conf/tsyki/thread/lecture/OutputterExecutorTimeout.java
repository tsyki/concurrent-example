package jp.gr.java_conf.tsyki.thread.lecture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Executorを使って各スレッドで標準出力するだけ(タイムアウトあり)
 */
public class OutputterExecutorTimeout implements Callable<Void> {

	private String name;

	public OutputterExecutorTimeout(String name) {
		this.name = name;
	}

	@Override
	public Void call() throws Exception {
		for (int i = 0; i < 100; i++) {
			System.out.println(name + ":" + i);
		}
		return null;
	}

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Void> call1 = new OutputterExecutorTimeout("1");
		Callable<Void> call2 = new OutputterExecutorTimeout("2");
		Future<Void> future1 = executor.submit(call1);
		Future<Void> future2 = executor.submit(call2);
		try {
			future1.get();
			future2.get(500, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			System.out.println("timeout");
		}
		System.out.println("main thread end");
		executor.shutdown();
	}

}