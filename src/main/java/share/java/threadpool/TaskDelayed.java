package share.java.threadpool;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author liyuxiang
 * @date 2021-12-17
 */
public class TaskDelayed implements Delayed, Runnable {

	private String name;

	private long delayTime;

	private long executeTime;

	public TaskDelayed() {
	}

	public TaskDelayed(String name, long delayTime) {
		this.name = name;
		this.delayTime = delayTime;
		this.executeTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(this.executeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
	}

	@Override
	public int compareTo(Delayed o) {
		TaskDelayed other = (TaskDelayed) o;
		return this.executeTime >= other.executeTime ? 1 : -1;
	}

	@Override
	public void run() {
		System.out.println("name:" + this.name + ", executeTime:" + this.delayTime+", now:"+System.currentTimeMillis()/1000);
	}
}
