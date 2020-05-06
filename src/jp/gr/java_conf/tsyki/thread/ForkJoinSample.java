package jp.gr.java_conf.tsyki.thread;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 参考URL
// http://www.ne.jp/asahi/hishidama/home/tech/java/forkjoin.html
// https://seraphy.hatenablog.com/entry/20140504/p1
public class ForkJoinSample {

	public static void main(String[] args) {
		// 1～100*10000の合計を計算する
		//poolあり
		{
			ForkJoinPool pool = new ForkJoinPool();
			ForkJoinTask<Long> task = pool.submit(new ForkJoinExampleTask()); //処理開始
			long result = task.join(); // 終了待ち+結果取得
			System.out.println(result);

			long result2 = pool.invoke(new ForkJoinExampleTask()); // 処理開始＋終了待ち＋結果取得
			System.out.println(result2);
		}
		// pool無し
		{
			ForkJoinTask<Long> task = new ForkJoinExampleTask();
			task.fork(); // 処理開始
			long result = task.join(); // 終了待ち＋結果取得
			System.out.println(result);

			long result2 = task.fork().join(); // 処理開始＋終了待ち＋結果取得
			System.out.println(result2);
		}
		//処理分割
		{
			ForkJoinPool pool = new ForkJoinPool();
			ForkJoinTask<Long> task = pool.submit(new RecursiveExmaple(1, 100 * 10000));
			long result = task.join();
			System.out.println(result);

			long result2 = new RecursiveExmaple(1, 100 * 10000).fork().join();
			System.out.println(result2);
		}
		//Executorで同じことをする場合の例(スレッドが大量に作られ効率が悪い)
		{
			ExecutorService executor = ExecutorTask.executor;
			Callable<Long> task1 = new ExecutorTask(1, 100 * 10000);
			Future<Long> future1 = executor.submit(task1);
			long result;
			try {
				result = future1.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				result = -1;
			}
			System.out.println(result);
		}
		//再帰処理
		{
			Path path = FileSystems.getDefault().getPath("I:\\test");
			ForkJoinPool pool = new ForkJoinPool();
			ForkJoinTask<Void> task = pool.submit(new TraverseTask(path));
			task.join();
			new TraverseTask(path).fork().join();
		}
	}

	// 帰り値ありはRecursiveTaskを、無ければRecursiveActionを使うのがよいので、ForkJoinTaskは通常使わない
	public static class ForkJoinExampleTask extends ForkJoinTask<Long> {
		private static final long serialVersionUID = 1L;

		private long result;

		@Override
		protected boolean exec() {
			long sum = 0;
			for (int i = 1; i <= 100 * 10000; i++) {
				sum += i;
			}
			setRawResult(sum); // 結果保存

			return true;
		}
		@Override
		public Long getRawResult() {
			return result;
		}

		@Override
		protected void setRawResult(Long arg) {
			this.result = arg;
		}
	}

	public static class RecursiveExmaple extends RecursiveTask<Long>{
		private final int start;
		private final int end;

		public RecursiveExmaple(int start,int end) {
			this.start = start;
			this.end = end;

		}

		@Override
		protected Long compute() {
			System.out.println(start + " " + end + " " +  Thread.currentThread().getId());
			log("start");
			int size = end - start;
			if(size <= 10000) {
				long sum = 0;
				for (int i = start; i <= end; i++) {
					sum += i;
				}
				return sum;
			}
			// 処理を分割し、自クラスを再帰的に呼び出す
			int split = start + size / 2;
			ForkJoinTask<Long> task1 = new RecursiveExmaple(start, split).fork(); //スレッド作成・処理開始
			ForkJoinTask<Long> task2 = new RecursiveExmaple(split+1, end).fork(); //スレッド作成・処理開始
			long result = task1.join() + task2.join(); //終了待ち
			log("end");
			return result;
		}

		private void log(String prefix) {
			// 1つのスレッドの使われ方を見るために、特定のスレッドだけを診断メッセージを表示する。
			//if (Thread.currentThread().toString().equals("Thread[ForkJoinPool-1-worker-1,5,main]")) {
			StringBuilder buf = new StringBuilder();
			buf.append(prefix);
			buf.append(" start-end.").append(start + "-" + end );
			if(getPool() != null) {
				buf.append(" :numOfActive=").append(getPool().getActiveThreadCount());
				buf.append(" :poolSize=").append(getPool().getPoolSize());
			}
			buf.append(" :").append(Thread.currentThread());
			System.out.println(buf.toString());
			//}
		}
	}


	public static class ExecutorTask implements Callable<Long>{
		private final int start;
		private final int end;

		// XXX stealingPoolを使えばFork/Joinと変わらない？
		public static final ExecutorService executor = Executors.newWorkStealingPool();

		public ExecutorTask(int start,int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public Long call() throws Exception {
			System.out.println(start + " " + end + " " +  Thread.currentThread().getId());
			log("start");
			int size = end - start;
			if(size <= 10000) {
				long sum = 0;
				for (int i = start; i <= end; i++) {
					sum += i;
				}
				return sum;
			}
			// 処理を分割し、自クラスを再帰的に呼び出す
			int split = start + size / 2;
			Callable<Long> task1 = new ExecutorTask(start, split);
			Callable<Long> task2 = new ExecutorTask(split+1, end);
			//Join/Forkと異なり、ここでスレッドが再利用されないため、大量のスレッドが作成される
			Future<Long> future1 = executor.submit(task1);
			Future<Long> future2 = executor.submit(task2);
			long result = future1.get() + future2.get(); //終了待ち
			log("end");
			return result;
		}

		private void log(String prefix) {
			StringBuilder buf = new StringBuilder();
			buf.append(prefix);
			buf.append(" start-end.").append(start + "-" + end );
			buf.append(" :").append(Thread.currentThread());
			System.out.println(buf.toString());
		}
	}



	public static class TraverseTask extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private final Path path;

		public TraverseTask(Path path) {
			this.path = path;
		}

		@Override
		protected void compute() {
			log("enter");
			if (Files.isDirectory(path)) {
				try (Stream<Path> stream = Files.list(path)) { // ファイル・ディレクトリー一覧
					stream.map(p -> new TraverseTask(p)) // ForkJoinTaskを生成
					.peek(task -> task.fork()) // fork実行
					.collect(Collectors.toList()).forEach(task -> task.join()); // 終了待ち
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
				return;
			}

			// ファイルの処理
			System.out.println(path);
			log("end");
		}

		private void log(String prefix) {
			// 1つのスレッドの使われ方を見るために、特定のスレッドだけを診断メッセージを表示する。
			//if (Thread.currentThread().toString().equals("Thread[ForkJoinPool-1-worker-1,5,main]")) {
			StringBuilder buf = new StringBuilder();
			buf.append(prefix);
			buf.append(" path.").append(path);
			if(getPool() != null) {
				buf.append(" :numOfActive=").append(getPool().getActiveThreadCount());
				buf.append(" :poolSize=").append(getPool().getPoolSize());
			}
			buf.append(" :").append(Thread.currentThread());
			System.out.println(buf.toString());
			//}
		}
	}


}

