package jp.gr.java_conf.tsyki.thread.lecture;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 指定のフォルダ以下のファイル数をカウントする
 */
public class ExecutorFileCounter {

	// 制限なくスレッドを作るので効率が悪い
	private static final ExecutorService executor = Executors.newCachedThreadPool();

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("arg is empry");
			return;
		}
		String path = args[0];
		Callable<Long> task1 = new FileCountTask(path);
		Future<Long> future1 = executor.submit(task1);
		long sumFileCount;
		try {
			sumFileCount = future1.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return;
		}
		System.out.println(sumFileCount);
	}

	public static class FileCountTask implements Callable<Long> {
		private final String path;

		public FileCountTask(String path) {
			this.path = path;
		}

		@Override
		public Long call() throws Exception {
			File file = new File(path);
			log();
			if (file.isDirectory()) {
				Collection<Future<Long>> futures = new ArrayList<>();
				for (File child : file.listFiles()) {

					//Join/Forkと異なり、ここでスレッドが再利用されないため、大量のスレッドが作成される
					FileCountTask task = new FileCountTask(child.getPath());
					Future<Long> future = executor.submit(task);
					futures.add(future);
				}
				Long sum = 0L;
				for (Future<Long> future : futures) {
					sum += future.get();
				}
				return sum;
			} else {
				return 1L;
			}
		}

		private void log() {
			StringBuilder buf = new StringBuilder();
			buf.append(Thread.currentThread());
			buf.append(": path=").append(path);
			System.out.println(buf.toString());
		}
	}

}
