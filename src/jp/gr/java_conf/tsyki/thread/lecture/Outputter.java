package jp.gr.java_conf.tsyki.thread.lecture;

/**
 * Threadを使って各スレッドで標準出力するだけ
 */
public class Outputter implements Runnable {

	private String name;

	public Outputter(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			System.out.println(name + ":" + i);
		}
	}

	public static void main(String[] args) {
		Thread thread1 = new Thread(new Outputter("thread1"));
		Thread thread2 = new Thread(new Outputter("thread2"));
		thread1.start();
		thread2.start();
		System.out.println("main thread end");
	}

}