package share.java.threadpool;

import java.util.concurrent.FutureTask;

/**
 * @author liyuxiang
 * @date 2021-12-16
 */
public class ComparableFutureTask<TestRunnable> extends FutureTask<TestRunnable> implements Comparable<ComparableFutureTask<TestRunnable>> {

	private Object object;

	public ComparableFutureTask(Runnable runnable, TestRunnable result) {
		super(runnable, result);
		object = runnable;
	}

	@Override
	public int compareTo(ComparableFutureTask<TestRunnable> other) {
		return ((Comparable) object).compareTo(other.object);
	}
}
