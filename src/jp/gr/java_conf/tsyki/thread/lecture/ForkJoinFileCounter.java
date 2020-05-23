package jp.gr.java_conf.tsyki.thread.lecture;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * 指定のフォルダ以下のファイル数をカウントする
 */
public class ForkJoinFileCounter {

	private static final ForkJoinPool pool = new ForkJoinPool();

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("arg is empry");
			return;
		}
		String path = args[0];
		//Executorで同じことをする場合の例(スレッドが大量に作られ効率が悪い)
		ForkJoinTask<Long> task = pool.submit(new FileCountTask(path));
		long sumFileCount = task.join();
		System.out.println(sumFileCount);
	}

	public static class FileCountTask extends RecursiveTask<Long> {
		private final String path;

		public FileCountTask(String path) {
			this.path = path;
		}

		@Override
		protected Long compute() {
			File file = new File(path);
			log();
			if (file.isDirectory()) {
				Collection<ForkJoinTask<Long>> tasks = new ArrayList<>();
				for (File child : file.listFiles()) {
					FileCountTask task = new FileCountTask(child.getPath());
					pool.submit(task);
					tasks.add(task);
				}
				Long sum = 0L;
				for (ForkJoinTask<Long> task : tasks) {
					sum += task.join();
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
