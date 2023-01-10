package share.java.forkjoinpool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * @author liyuxiang
 * @date 2022-01-28
 */
public class ForkJoinTaskExample extends RecursiveTask<Integer> {

	private int start;
	private int end;
	private String name;

	public ForkJoinTaskExample(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected Integer compute() {

		int sum = 0;

		/*System.out.println("active thread:"+forkjoinPool.getActiveThreadCount());
		System.out.println("poolSize:" + forkjoinPool.getPoolSize());

		ForkJoinWorkerThread forkJoinWorkerThread = (ForkJoinWorkerThread)Thread.currentThread();
		System.out.println("thread name:"+forkJoinWorkerThread.getName()+"， taskName:"+getName());*/

		//如果任务足够小就计算任务
		boolean canCompute = (end - start) <= 2;
		if (canCompute) {
			for (int i = start; i <= end; i++) {
				sum += i;
				System.out.println(Thread.currentThread().getName() + "start:" + start);
				System.out.println("end:" + end);
			}
		} else {

			// 如果任务大于阈值，就分裂成两个子任务计算
			int middle = (start + end) / 2;
			ForkJoinTaskExample leftTask = new ForkJoinTaskExample(start, middle);
			leftTask.setName(this.name);
			ForkJoinTaskExample rightTask = new ForkJoinTaskExample(middle + 1, end);
			rightTask.setName(this.name);

			// 执行子任务
			leftTask.fork();
			rightTask.fork();

			// 等待任务执行结束合并其结果
			int leftResult = leftTask.join();
			int rightResult = rightTask.join();


			// 合并子任务
			sum = leftResult + rightResult;

			System.out.println("fork start:" + start);
			System.out.println("fork end:" + end);

		}
		return sum;


	}


	public static ForkJoinPool forkjoinPool = new ForkJoinPool(2);

	public static void main(String[] args) throws Exception {

		//执行一个任务
		for (int i = 0; i <= 3; i++) {

			int start = 100*i;

			//生成一个计算任务，计算1+2+3+4 + ~ + 100
			ForkJoinTaskExample task = new ForkJoinTaskExample(start, start+100);
			task.setName("-------------"+i+"-------------");
			Future<Integer> resultFuture = forkjoinPool.submit(task);
		}

		Thread.sleep(10000000);

		//System.out.println(resultFuture.get());


	}
}