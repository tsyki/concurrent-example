package jp.gr.java_conf.tsyki.thread.lecture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Callableの実行時に例外が発生した場合の例
 */
public class OutputterExecutorException implements Callable<Void> {

	private String name;

	public OutputterExecutorException(String name) {
		this.name = name;
	}

	@Override
	public Void call() throws Exception {
		for (int i = 0; i < 100; i++) {
			System.out.println(name + ":" + i);
		}
		throw new IllegalStateException("hoge");
	}

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Void> call1 = new OutputterExecutorException("1");
		Callable<Void> call2 = new OutputterExecutorException("2");
		Future<Void> future1 = executor.submit(call1);
		Future<Void> future2 = executor.submit(call2);
		try {
			future1.get();
			future2.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			Throwable original = e.getCause();
			System.out.println(original);
		}
		System.out.println("main thread end");
		executor.shutdown();
	}

}