package share.java.forkjoinpool;

import org.assertj.core.util.Lists;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * @author liyuxiang
 * @date 2022-01-27
 */
public class Demo {

	public static void main(String[] args) throws Exception{


		List<String> list = Lists.newArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

		list.parallelStream().forEach(s -> {
			System.out.println("thread name:" + Thread.currentThread().getName() + ", value:" + s+ ", active thread count:" + ForkJoinPool.commonPool().getActiveThreadCount() );
			System.out.println(Thread.currentThread().isDaemon());
		});


		/*System.out.println("-------------------");

		Thread thread = new Thread(()->{
			Thread thread1 = new Thread(()->{
				System.out.println(Thread.currentThread().isDaemon());
			});
			thread1.start();
		});
		thread.setDaemon(true);
		thread.start();
		System.out.println(thread.isDaemon());*/

		// 创建一个线程池Th
		ForkJoinPool forkJoinPool = new ForkJoinPool(8);
		forkJoinPool.submit(() -> list.parallelStream().forEach(s -> {
			System.out.println("thread name:" + Thread.currentThread().getName() + ", value:" + s + ", active thread count:" + forkJoinPool.getActiveThreadCount());
			System.out.println(Thread.currentThread().isDaemon());
		})).join();
		System.out.println(forkJoinPool.getActiveThreadCount());

		Thread.sleep(10000000L);
	}
}
