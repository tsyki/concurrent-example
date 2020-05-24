package jp.gr.java_conf.tsyki.thread.lecture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Executorを使って各スレッドで標準出力するだけ
 */
public class OutputterExecutor implements Callable<Void> {

	private String name;

	public OutputterExecutor(String name) {
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
		Callable<Void> call1 = new OutputterExecutor("1");
		Callable<Void> call2 = new OutputterExecutor("2");
		Future<Void> future1 = executor.submit(call1);
		Future<Void> future2 = executor.submit(call2);
		try {
			future1.get();
			future2.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("main thread end");
		executor.shutdown();
	}

}