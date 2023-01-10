package share.java.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liyuxiang
 * @date 2021-12-14
 */
public class ThreadPoolDemo {


	public static void main(String[] args) throws Exception {
		System.out.println(Runtime.getRuntime().availableProcessors());
		//test1();
		/*//test2();
		System.out.println(Thread.currentThread().isDaemon());
		test3();
		Thread.sleep(100);
		//test4();*/
	}

	public static void test3() {
		Thread t = new Thread(() -> {
			test4();
			while (true) {
				try {
					System.out.println(11);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		});
		t.setDaemon(true);
		System.out.println(t.isDaemon());

		t.start();
	}

	public static void test4() {
		Thread t = new Thread(() -> {
			while (true) {
				try {
					System.out.println(44);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		//t.setDaemon(false);
		System.out.println(t.isDaemon());

		t.start();
	}

	/**
	 * 测试PriorityBlockingQueue
	 */
	public static void test1() throws Exception {

		int[] orders = new int[]{0, 10, 211, 2, 123, 123, 455, 23, 45};

		// 无序的
		ThreadPoolExecutor executorService1 = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
		for (int i = 0; i < orders.length; i++) {
			final int order = orders[i];
			executorService1.execute(() -> System.out.println(order));
		}

		executorService1.shutdown();

		Thread.sleep(2000);

		System.out.println(executorService1.getActiveCount());

		/*Thread.sleep(2000);
		System.out.println("----------------------");

		ThreadPoolExecutor executorService2 = new ThreadPoolExecutor(2, 2, 0, TimeUnit.SECONDS, new PriorityBlockingQueue<>());

		for (int i = 0; i < orders.length; i++) {
			final int order = orders[i];
			RunnableFuture<TestRunnable> futureTask = new ComparableFutureTask<>(new TestRunnable(order), null);
			executorService2.execute(futureTask);
		}

		Thread.sleep(5000);*/
	}

	/**
	 * 测试DelayQueue
	 */
	public static void test2() {

		ThreadPoolExecutor executorService1 = new ThreadPoolExecutor(1, 1, 10000, TimeUnit.MILLISECONDS, new DelayQueue());
		//executorService1.prestartAllCoreThreads();

		long[] delayTimes = new long[]{10000, 3000, 1000, 2000, 5000, 15000, 1000, 1100, 19999, 1};

		for (int i = 0; i < delayTimes.length; i++) {
			TaskDelayed taskDelayed = new TaskDelayed("任务:" + i, delayTimes[i]);
			executorService1.execute(taskDelayed);
			/*if(i==1){
				try{

					Thread.sleep(1000);
				}catch (Exception e){

				}
			}*/
		}

		executorService1.shutdown();
	}

}
