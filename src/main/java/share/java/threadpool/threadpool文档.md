
# Java线程池实现原理

## 简要介绍

线程池（Thread Pool）是一种基于池化思想管理线程的工具，经常出现在多线程服务器中，如MySQL。

线程过多会带来额外的开销，其中包括创建销毁线程的开销、调度线程的开销等等，同时也降低了计算机的整体性能。线程池维护多个线程，等待监督管理者分配可并发执行的任务。这种做法，一方面避免了处理任务时创建销毁线程开销的代价，另一方面避免了线程数量膨胀导致的过分调度问题，保证了对内核的充分利用。


## 实现原理

### 总体介绍

* ThreadPoolExecutor类图
![avatar](https://p1.meituan.net/travelcube/912883e51327e0c7a9d753d11896326511272.png)
* Executor: executor
* ExecutorService
    * submit：返回值Future
    * shutdown：不能在添加任务，已添加的会执行完成。  
    * shutdownNow：不能在添加任务，已添加的不会继续执行，并返回未执行的任务。

* ThreadPoolExecutor：
    * 线程池的实现类
    * corePoolSize 核心线程数
    * maximumPoolSize 最大线程数
    * keepAliveTime 空闲线程等待工作的超时时间
    * workQueue 待执行的任务队列
    * allowCoreThreadTimeOut: 是否允许核心线程过期从而回收掉

* 执行流程图
  ![avatar](https://p0.meituan.net/travelcube/77441586f6b312a54264e3fcf5eebe2663494.png)

### 线程池的生命周期

#### RUNNING
运行状态，能接受任务，能处理任务

#### SHUTDOWN
关闭状态，不接受新的任务，继续处理队列中的任务

#### STOP
不接受新的任务，不处理队列中的任务，正在处理中的，会尝试终端

#### TIDYING   
所有任务都执行完了，有效线程为0

#### TERMINATED
关闭状态

![avatar](https://p0.meituan.net/travelcube/582d1606d57ff99aa0e5f8fc59c7819329028.png)

### 任务的执行机制

#### 线程的创建
* 首先检测线程池运行状态，如果不是RUNNING，则直接拒绝，线程池要保证在RUNNING的状态下执行任务。
* 如果workerCount < corePoolSize，则创建并启动一个线程来执行新提交的任务。
* 如果workerCount >= corePoolSize，且线程池内的阻塞队列未满，则将任务添加到该阻塞队列中。
* 如果workerCount >= corePoolSize && workerCount < maximumPoolSize，且线程池内的阻塞队列已满，则创建并启动一个线程来执行新提交的任务。
* 如果workerCount >= maximumPoolSize，并且线程池内的阻塞队列已满, 则根据拒绝策略来处理该任务, 默认的处理方式是直接抛异常。

![avatar](https://p0.meituan.net/travelcube/31bad766983e212431077ca8da92762050214.png)

#### 阻塞队列
* ArrayBlockingQueue：数组实现的有界队列，按照先进先出的原则排序
* PriorityBlockingQueue
  * 数组实现的无界队列，支持自定义排序
  * test1
* DelayQueue：实现PriorityBlockingQueue的延迟获取的无界队列, 在加入队列时，可以指定线程多久以后才能获取到任务
  * ScheduledThreadPoolExecutor
* LinkedBlockingDeque：链表实现的无界队列，队列头和尾均可以添加和移除任务，提高性能

#### 拒绝策略
* ThreadPoolExecutor.AbortPolicy
  * 这是默认的拒绝策略
  * 丢弃任务并抛出异常RejectedExecutionException
  * 适合关键的业务，能够及时发现问题
  
* ThreadPoolExecutor.DiscardPolicy
  * 丢弃任务，不抛异常
  * 适合不关键的业务
  
* ThreadPoolExecutor.DiscardOldestPolicy
  * 丢弃队列最前面的任务，然后在尝试加入队列，不抛异常
  * 适合不关键的业务
  
* ThreadPoolExecutor.CallerRunsPolicy
  * 由当前线程处理该任务
  * 适合必须要执行的业务

#### Worker
* 线程池内的工作线程，为了方便线程池管理线程的状态而建立的。
* 一个Worker对象，持有一个线程
  ![avatar](https://p0.meituan.net/travelcube/03268b9dc49bd30bb63064421bb036bf90315.png)
  

#### 核心方法 
* execute
  * 添加任务并执行
  * 源码分析
```
  
  public void execute(Runnable command) {
        
        int c = ctl.get();
        
        // 创建核心线程并执行任务
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        
        // 加入到阻塞队列中
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                // 当核心线程数为0，则此时新建一个非核心线程，去执行任务
                addWorker(null, false);
        }
        
        // 创建非核心线程并执行任务，创建失败则执行拒绝策略
        else if (!addWorker(command, false))
            reject(command);
    }

```

* addWorker
  * 新增并启动线程
  * 源码分析
  
```
    private boolean addWorker(Runnable firstTask, boolean core) {
    
        // 检查是否需要新增线程
        retry:
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN &&
                ! (rs == SHUTDOWN &&
                   firstTask == null &&
                   ! workQueue.isEmpty()))
                return false;

            for (;;) {
                int wc = workerCountOf(c);
                if (wc >= CAPACITY ||
                    wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                if (compareAndIncrementWorkerCount(c))
                    break retry;
                c = ctl.get();  // Re-read ctl
                if (runStateOf(c) != rs)
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }
        }

        // 新增Work（线程）
        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;
        try {
            // 给Work添加任务
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.
                    int rs = runStateOf(ctl.get());

                    if (rs < SHUTDOWN ||
                        (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) // precheck that t is startable
                            throw new IllegalThreadStateException();
                        
                        // 将当前work加入到工作线程队列中
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    // 新增Work成功，并启动Work
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }
    
    // new Work的时候，会新建一个线程
    Worker(Runnable firstTask) {
        setState(-1); // inhibit interrupts until runWorker
        this.firstTask = firstTask;
        this.thread = getThreadFactory().newThread(this);
    }

```

* getTask
  * 线程从阻塞队列中获取任务并执行
  * 源码分析

```

    private Runnable getTask() {
        
        boolean timedOut = false; // Did the last poll() time out?

        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);
          
            // 获取当前线程总数
            int wc = workerCountOf(c);

            // Are workers subject to culling?
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

            // 判断是否需要减少线程
            if ((wc > maximumPoolSize || (timed && timedOut))
                && (wc > 1 || workQueue.isEmpty())) {
                if (compareAndDecrementWorkerCount(c))
                    // 需要关闭当前线程
                    return null;
                continue;
            }

            // 从队列中获取任务
            try {
                Runnable r = timed ?
                    workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                    workQueue.take();
                if (r != null)
                    return r;
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }


```
* submit
  * 可以获取任务的结果
  * FutureTask

* shutdown
  * 优雅停机
  * tryTerminate
  * processWorkerExit
    * 传递停机信号
    * 根据条件补充worker，继续传递停机信号

### Executors
* newWorkStealingPool
  * 多个线程在这个单cpu中是并发运行。多个线程在多个CPU内核中执行，是并行
  * 创建一个线程池，维护足够的线程以支持给定的并行度级别，并且可以使用多个队列来减少争用
  * 并行数：并行级别参数，应小于等于当前系统的CPU核数。默认是当前系统的CPU核数。

* ForkJoinPool  
  * 并行执行的任务框架
  * 将大任务分成若干小任务，之后再并行对这些小任务进行计算，最终汇总这些任务的结果
  * 应用
    * stream 
    * Executors.newWorkStealingPool


## 实际应用

### 应用场景
* 快速响应的场景
* 批量任务
  * 报表
  * 消息推送

### 参数配置
* corePoolSize
* maximumPoolSize 
* workQueue
* 动态化参数配置
* 报警
  

### 参考文档
[Java线程池实现原理及其在美团业务中的实践](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html)
[进程，线程与多核，多cpu之间的关系](https://www.cnblogs.com/valjeanshaw/p/11469514.html)
