package jp.gr.java_conf.tsyki.thread.lecture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * スレッドプールのサイズを固定とし、各スレッドで標準出力するだけ
 */
public class FixedPoolOutputterExecutor implements Callable<Void> {

	private String name;

	public FixedPoolOutputterExecutor(String name) {
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
		Callable<Void> call1 = new FixedPoolOutputterExecutor("1");
		Callable<Void> call2 = new FixedPoolOutputterExecutor("2");
		Callable<Void> call3 = new FixedPoolOutputterExecutor("3");
		Callable<Void> call4 = new FixedPoolOutputterExecutor("4");
		Future<Void> future1 = executor.submit(call1);
		Future<Void> future2 = executor.submit(call2);
		// スレッド数が2なのでcall3の実行は待機される
		Future<Void> future3 = executor.submit(call3);
		Future<Void> future4 = executor.submit(call4);
		try {
			future1.get();
			future2.get();
			future3.get();
			future4.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("main thread end");
		executor.shutdown();
	}

}