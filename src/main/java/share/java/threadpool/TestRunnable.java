package share.java.threadpool;

/**
 * @author liyuxiang
 * @date 2021-12-16
 */
public class TestRunnable implements Runnable, Comparable<TestRunnable>{

	private int priority;

	public int getPriority() {
		return priority;
	}

	public TestRunnable(int priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(TestRunnable o) {
		return o.getPriority() - this.priority;
	}

	@Override
	public void run() {
		System.out.println(this.priority);
		try{
			Thread.sleep(500);
		}catch (Exception e){

		}
	}

}
