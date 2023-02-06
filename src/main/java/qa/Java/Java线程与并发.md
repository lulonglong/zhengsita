### 线程篇

#### 1.sleep() 和 wait() 有什么区别？

类的不同：sleep() 来自 Thread，wait() 来自 Object。

释放锁：sleep() 不释放锁；wait() 释放锁。

用法不同：sleep() 时间到会自动恢复；wait() 可以使用 notify()/notifyAll()直接唤醒  



#### 2.线程的 sleep() 方法和 yield() 方法有什么区别？

线程执行 sleep() 方法后进入超时等待（TIMED_WAITING）状态，而执行 yield() 方法后进入就绪（READY）状态。

sleep() 方法给其他线程运行机会时不考虑线程的优先级，因此会给低优先级的线程运行的机会；yield() 方法只会给相同优先级或更高优先级的线程以运行的机会。 



#### 3.什么是多线程中的上下文切换？

上下文切换（有时也称做进程切换或任务切换）是指 CPU 从一个进程（或线程）切换到另一个进程（或线程）。上下文是指某一时间点 CPU 寄存器和程序计数器的内容。



#### 4.在 java 中守护线程和本地线程区别？

我们大部分时间所使用的线程，都是本地线程。进程中有一个本地线程没有结束工作，进程都不会退出。

而守护线程，是为守护当前进程而生的，一般都是框架性功能，基础功能所使用，比如一个服务程序的“心跳维持”线程。

一个程序的日志搜集线程，等等。此种线程会在进程中所有本地线程完成之后，自动消亡。

我们创建线程时，除主动设置，默认情况下，线程是否属于守护线程的属性继承自父线程。

通过setDaemon进行设置。



#### 5.线程的状态流转

![img](https://img-blog.csdnimg.cn/20210401000320769.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3YxMjM0MTE3Mzk=,size_16,color_FFFFFF,t_70)

#### 6.为什么要使用线程池？直接new个线程不是很舒服？

线程池优点： 

1. 降低资源消耗。通过重复利用已创建的线程，降低线程创建和销毁造成的消耗。

2. 提高响应速度。当任务到达时，任务可以不需要等到线程创建就能立即执行。
3. 增加线程的可管理型。线程是稀缺资源，使用线程池可以进行统一分配，调优和监控。

 

#### 7.线程池的核心属性有哪些？

threadFactory（线程工厂）：用于创建工作线程的工厂。

corePoolSize（核心线程数）：当线程池运行的线程少于 corePoolSize 时，将创建一个新线程来处理请求，即使其他工作线程处于空闲状态。

workQueue（队列）：用于保留任务并移交给工作线程的阻塞队列。

maximumPoolSize（最大线程数）：线程池允许开启的最大线程数。

handler（拒绝策略）：往线程池添加任务时，将在下面两种情况触发拒绝策略：1）线程池运行状态不是 RUNNING；2）线程池已经达到最大线程数，并且阻塞队列已满时。

keepAliveTime（保持存活时间）：如果线程池当前线程数超过 corePoolSize，则多余的线程空闲时间超过 keepAliveTime 时会被终止。 



#### 8.说下线程池的运作流程

![img](https://img-blog.csdnimg.cn/20200608092639652.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3YxMjM0MTE3Mzk=,size_16,color_FFFFFF,t_70)

#### 9.线程池有哪些拒绝策略？

AbortPolicy：中止策略。默认的拒绝策略，直接抛出 RejectedExecutionException。调用者可以捕获这个异常，然后根据需求编写自己的处理代码。

DiscardPolicy：抛弃策略。什么都不做，直接抛弃被拒绝的任务。

DiscardOldestPolicy：抛弃最老策略。抛弃阻塞队列中最老的任务，相当于就是队列中下一个将要被执行的任务，然后重新提交被拒绝的任务。如果阻塞队列是一个优先队列，那么“抛弃最旧的”策略将导致抛弃优先级最高的任务，因此最好不要将该策略和优先级队列放在一起使用。

CallerRunsPolicy：调用者运行策略。在调用者线程中执行该任务。该策略实现了一种调节机制，该策略既不会抛弃任务，也不会抛出异常，而是将任务回退到调用者（调用线程池执行任务的主线程），由于执行任务需要一定时间，因此主线程至少在一段时间内不能提交任务，从而使得线程池有时间来处理完正在执行的任务。 


#### 10.为何stop()和suspend()方法不推荐使用
不安全，意外被其他线程终止或暂停，如果程序持有锁资源，容易死锁。

#### 11. execute 和 submit 的区别

* 接收的参数不一样：submit可以是Callable，也可以是Runnable，execute只能是Runnable 。
* submit有返回值；而execute没有，fs.get()的结果是null
* submit在执行过程中与execute不一样，不会抛出异常而是把异常保存在成员变量中，在FutureTask.get阻塞获取的时候再把异常抛出来。
* Spring的@Schedule注解的内部实现就是使用submit，因此，如果你构建的任务内部有未检查异常，你是永远也拿不到这个异常的。
* execute直接抛出异常之后线程就死掉了，submit保存异常线程没有死掉，因此execute的线程池可能会出现没有意义的情况，因为线程没有得到重用。而submit不会出现这种情况。


### 并发编程篇

#### 1.死锁与活锁的区别，死锁与饥饿的区别？

死锁：是指两个或两个以上的进程（或线程）在执行过程中，因争夺资源而造成的一种互相等待的现象，若无外力作用，它们都将无法推进下去。此时称系统处于死锁状态或系统产生了死锁，这些永远在互相等待的进程称为死锁进程。

活锁：任务或者执行者没有被阻塞，由于某些条件没有满足，导致一直重复尝试，失败，尝试，失败

活锁和死锁的区别：

1）处于活锁的实体是在不断的改变状态，所谓的“活”； 处于死锁的实体表现为等待。

2）活锁可能自行解开，死锁不能自行解开。

**饥饿：一个或者多个线程因为种种原因无法获得所需要的资源，导致一直无法执**行的状态。

**Java** **中导致饥饿的原因：**

1、高优先级线程吞噬所有的低优先级线程的 CPU 时间。

2、线程被永久堵塞在一个等待进入同步块的状态，因为其他线程总是能在它之前

持续地对该同步块进行访问。

3、线程在等待一个本身也处于永久等待完成的对象(比如调用这个对象的 wait 方

法)，因为其他线程总是被持续地获得唤醒

#### 2.谈谈threadlocal

ThreadLocal意为线程本地变量，作用是对资源进行隔离，线程级隔离。

每一个线程都有一个ThreadLocalMap类型的threadLocals属性用来存储本线程的所有“线程本地变量”。

使用ThreadLocal 变量存储数据时会以ThreadLocal变量作为key，要存储的业务对象作为value，存储到线程的threadLocals的map里。

```java
public class Test {
    static ThreadLocal<String> threadLocal = new ThreadLocal<>();
		public void m1(String value) {
   			threadLocal.set(value);
		}

		public void m2() {
    		String value = threadLocal.get();
    		// 使用
    
    		// 使用完清除
    		threadLocal.remove();
		}
}
```
[ThreadLocal使用与原理](https://juejin.cn/post/6959333602748268575)

#### 3.协程/纤程是什么？

可以理解为轻量级线程，或用户线程(区别操作系统内核线程)

是应用曾的一种抽象，抽象出更细颗粒度的计算单元，虚拟出一种轻量化线程模型，解决线程切换带来的问题。 

#### 4.synchronized 和 Lock 的区别

1）Lock 是一个接口；synchronized 是 Java 中的关键字，synchronized 是内置的语言实现；

2）Lock 在发生异常时，如果没有主动通过 unLock() 去释放锁，很可能会造成死锁现象，因此使用 Lock 时需要在 finally 块中释放锁；synchronized 不需要手动获取锁和释放锁，在发生异常时，会自动释放锁，因此不会导致死锁现象发生；

3）Lock 的使用更加灵活，可以有响应中断、有超时时间等；而 synchronized 却不行，使用 synchronized 时，等待的线程会一直等待下去，直到获取到锁；

4）在性能上，随着近些年 synchronized 的不断优化，Lock 和 synchronized 在性能上已经没有很明显的差距了，所以性能不应该成为我们选择两者的主要原因。官方推荐尽量使用 synchronized，除非 synchronized 无法满足需求时，则可以使用 Lock 

Mark Word 在32位 [JVM](https://so.csdn.net/so/search?q=JVM&spm=1001.2101.3001.7020) 中：

![在这里插入图片描述](https://img-blog.csdnimg.cn/139a6b653a4341918622aa04d6a2432b.png)

Mark Word 在64位 JVM 中：

![在这里插入图片描述](https://img-blog.csdnimg.cn/4337760476214560b55302192663fa1f.png)

#### 5.死锁形成的四个条件

1. 互斥使用，即当资源被一个线程使用(占有)时，别的线程不能使用

2. 不可抢占，资源请求者不能强制从资源占有者手中夺取资源，资源只能由资源占用者主动释放

3. 请求和保持，即当资源的请求者在请求其他的资源的同时保持对原有资源的占有

4. 循环等待，即存在一个等待队列: P1占有P2的资源，P2占有P3的资源，P3占有P1的资源。这样就形成了一个等待环路。


#### 6.怎么预防死锁？

预防死锁的方式就是打破四个必要条件中的任意一个即可。

1）打破互斥条件：在系统里取消互斥。若资源不被一个进程独占使用，那么死锁是肯定不会发生的。但一般来说在所列的四个条件中，“互斥”条件是无法破坏的。因此，在死锁预防里主要是破坏其他几个必要条件，而不去涉及破坏“互斥”条件。。

2）打破请求和保持条件：1）采用资源预先分配策略，即进程运行前申请全部资源，满足则运行，不然就等待。 2）每个进程提出新的资源申请前，必须先释放它先前所占有的资源。

3）打破不可剥夺条件：当进程占有某些资源后又进一步申请其他资源而无法满足，则该进程必须释放它原来占有的资源。

4）打破环路等待条件：实现资源有序分配策略，将系统的所有资源统一编号，所有进程只能采用按序号递增的形式申请资源。 

#### 7.介绍下 CountDownLatch 、 CyclicBarrier、Semaphore

CountDownLatch： 线程计数器，A过程的计数达到，B过程前行

```java
   final CountDownLatch latch = new CountDownLatch(2);
    new Thread() {
        public void run() {
            System.out.println("子线程" + Thread.currentThread().getName() + "正在执行");
            Thread.sleep(3000);
            System.out.println("子线程" + Thread.currentThread().getName() + "执行完毕");
            latch.countDown();
        }
        ;
    }.start();
    new Thread() {
        public void run() {
            System.out.println("子线程" + Thread.currentThread().getName() + "正在执行");
            Thread.sleep(3000);
            System.out.println("子线程" + Thread.currentThread().getName() + "执行完毕");
            latch.countDown();
        }
        ;
    }.start();
    System.out.println("等待 2 个子线程执行完毕...");
    latch.await();
    System.out.println("2 个子线程已经执行完毕");
    System.out.println("继续执行主线程");
}
```

CyclicBarrier：回环栅栏-等待至 barrier 状态再全部同时执行，“人数到齐一起走”

```java
public class Demo {
    public static void main(String[] args) {
        int N = 4;
        CyclicBarrier barrier = new CyclicBarrier(N);
        for (int i = 0; i < N; i++)
            new Writer(barrier).start();
    }
    
    static class Writer extends Thread {
        private CyclicBarrier cyclicBarrier;
    		public Writer(CyclicBarrier cyclicBarrier) {
    		    this.cyclicBarrier = cyclicBarrier;
    		}

   		 @Override
   		 public void run() {
        try {
            Thread.sleep(5000); //以睡眠来模拟线程需要预定写入数据操作
            System.out.println("线程" + Thread.currentThread().getName() + "写入数据完毕，等待其他线程写入完毕");
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println("所有线程写入完毕，继续处理其他任务，比如数据操作");
    }
	}
}
```
Semaphore:信号量

```java
	public static void main(String[] args) {
    int N = 8; //工人数
    Semaphore semaphore = new Semaphore(5); //机器数目
    for (int i = 0; i < N; i++)
        new Worker(i, semaphore).start();
}

static class Worker extends Thread {
    private int num;
    private Semaphore semaphore;

    public Worker(int num, Semaphore semaphore) {
        this.num = num;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            System.out.println("工人" + this.num + "占用一个机器在生产...");
            Thread.sleep(2000);
            System.out.println("工人" + this.num + "释放出机器");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```

#### 8.ConcurrentHashMap的get方法是否要加锁，为什么？

不需要，get方法采用了unsafe方法，用来保证线程安全。

使用了volatile关键字，保证了内存的可见性
多线程的环境下修改了节点的value和新增了节点对于其他线程是可见的


```    
   static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K,V> next;
    }
    
    static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
        return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }

```

#### 9.为什么有Volatile，Volatile的使用需要注意些什么？

Volatile的存在是解决可见性问题的，当多个线程同时访问或操作一个共享对象，跨cpu执行时修改不会立即同步到其他cpu。

就需要Volatile来显示的控制，共享对象变更的及时同步。

使用时需要注意：1.Volatile不能保证完全原子性（如i++）。

TODO：展开到内存栅栏更深处

#### 10.你了解读写锁吗?

读写锁内部有读锁写锁两把锁

线程进入读锁的前提条件：
  没有其他线程的写锁

线程进入写锁的前提条件：
  没有其他线程的读锁
  没有其他线程的写锁


#### ReentranLock
[从ReentrantLock的实现看AQS的原理及应用](https://tech.meituan.com/2019/12/05/aqs-theory-and-apply.html)
[ReentrantLock非公平锁与公平锁的实现](https://cloud.tencent.com/developer/article/1563317)
[深入理解Java并发框架AQS系列（五）：条件队列（Condition）](https://www.cnblogs.com/xijiu/p/14711933.html)
[juc常用同步工具类](https://juejin.cn/post/6844904083216662536)



## 答案展开


### 线程篇

#### 什么是多线程中的上下文切换？

线程是操作系统独立调度和分派的基本单位，cpu在同时期运行多个就绪的线程，执行时会采用某种cup调度策略，来分配cpu的使用权。

在线程的使用权交换时，前线程的状态会被保存下来，后线程的状态会被装载进来。这里所说的状态包括：栈帧所组成的程序栈，以及程序计数器等。

细节上状态的保存动作是把状态信息从cpu计数器以及最近缓存往下级缓存和主存更新，装载则是反过程。

以及涉及到操作系统就绪队列的调整，会存在用户态和内核态的切换（为了避免进一步深入问，cpu调度策略和就绪队列，如果没信心深入可以不提此点）

所以上下文切换是一种大开销动作，尽量避免不必要的切换。

造成上下文切换的一些主动因素，就是一些锁的阻塞，线程的sleep主动让出cup使用权等。

避免上下文切换的方式有：

1. 避免过多的创建线程，比如一些小开销任务避免用子线程来完成。至于大开销任务，采用线程池来实现，线程池在完成上一个任务之后，会直接拿下一个，或短暂自己旋等待获取下一个任务。

2. 避免有锁编程，遇到资源竞争情况，尽量使用CAS方式解决。


高级加分点：引入协程/纤程的技术和理论。

​		由于互联网系统都是采用分布式模式，大量的微服务化，造成了调用链明显变长，一个请求执行链路中所有上下文切换的成本大幅增加。同时单微服务接口的计算量明显变小，上下文切换所带来的成本和计算的比率明显增高，甚至有些计算，切换上下文的成本比计算本身的成本都高。所以Java开始寻求无上下文切换的虚拟线程，来解决这种问题。这种虚拟线程有个名字叫纤程，又叫有栈协程。

​		OpenJDK在2018年创建了Loom项目专负责java虚拟线程，Loom团队在JVM LS 2018大会上公布了他们对Jetty基于纤程改造后的测试结果，同样在 5000QPS的压力下，以容量为400的线程池的传统模式和每个请求配以一个纤程的新并发处理模式进行 对比，前者的请求响应延迟在10000至20000毫秒之间，而后者的延迟普遍在200毫秒以下

 **今年9月份发布的JDK19 里已经把虚拟线程列为JEP（JDK增强建议）**


#### 为何stop()和suspend()方法不推荐使用

stop()方法作为一种粗暴的线程终止行为，在线程终止之前没有对其做任何的清除操作，因此具有固有的不安全性。 用Thread.stop()方法来终止线程将会释放该线程对象已经锁定的所有监视器。如果以前受这些监视器保护的任何对象都处于不连贯状态，那么损坏的对象对其他线程可见，这有可能导致不安全的操作。 由于上述原因，因此不应该使用stop()方法，而应该在自己的Thread类中置入一个标志，用于控制目标线程是活动还是停止。如果该标志指示它要停止运行，可使其结束run（）方法。如果目标线程等待很长时间，则应使用interrupt()方法来中断该等待。

suspend()方法 该方法已经遭到反对，因为它具有固有的死锁倾向。调用suspend（）方法的时候，目标线程会停下来。如果目标线程挂起时在保护关键系统资源的监视器上保持有锁，则在目标线程重新开始以前，其他线程都不能访问该资源。除非被挂起的线程恢复运行。对任何其他线程来说，如果想恢复目标线程，同时又试图使用任何一个锁定的资源，就会造成死锁。由于上述原因，因此不应该使用suspend（）方法，而应在自己的thread类中置入一个标志，用于控制线程是活动还是挂起。如果标志指出线程应该挂起，那么用wait（）方法命令其进入等待状态。如果标志指出线程应当恢复，那么用notify()方法重新启动线程。

#### Threadlocal

ThreadLocal意为线程本地变量，作用是对资源进行隔离，线程级隔离。

该变量对其他线程而言是隔离的，也就是说该变量是当前线程独有的变量。ThreadLocal为变量在每个线程中都创建了一个副本，那么每个线程可以访问自己内部的副本变量。

每一个线程内部都有一个ThreadLocalMap类型的threadLocals属性，此属性负责具体线程本地变量的存储。

延伸：ThreadLocalMap 内部是采用一个弱引用对象，来存储threadlocal对象，以达到在业务代码使用完之后，gc能够即时清理。

![ThreadLocal弱引用](https://picx.zhimg.com/v2-3923406319c1fe3c6656aeda7d7de121_1440w.jpg?source=172ae18b)

[ThreadLocal和SimpleDateFormat]https://blog.jrwang.me/2016/java-simpledateformat-multithread-threadlocal/


### 并发编程篇

#### 介绍一下**Synchronized**？

##### 答案：

字面答案：Sysnchronized 是一个保证线程安全的工具，保障同一代码块，或存在共享资源竞争的相关代码块，在同一时

​				间只有一个线程执行。Sysnchronized可以理解为一种复合型隐式锁，可以修饰在作用在静态方法上，锁class对象，可以修饰在成员方法上，锁this对象，也可以指定锁对象。

延伸：

​		1.Sysnchronized会根据程序的竞争情况，选择使用或升级到对应的锁级别。

​		2.Sysnchronized的锁升级过程是，无锁->偏向锁->轻量级锁->重量级锁。

​		3.当一个对象已经计算过一 致性哈希码后，它就再也无法进入偏向锁状态了;而当一个对象当前正处于偏向锁状态，又收到需要

​		 计算其一致性哈希码请求时，它的偏向状态会被立即撤销，并且锁会膨胀为重量级锁

​		4.针对频繁撤销偏向锁的情况，底层实现了**批量重偏向**和***批量撤销***来进行了优化。

**从JDK 15版本开始就默认关闭了偏向锁**。

关闭原因：1.从偏向锁的加锁和解锁的过程中可以看出，当只有一个线程反复进入同步代码块时，偏向锁带来的性能开销基本可以忽略，但是当有其他线程尝试获取锁的时候，就需要等到 safe point 时，再将偏向锁撤销为无锁的状态或者升级为轻量级锁，会消耗一定的性能，所以在多线程竞争频繁的情况下，偏向锁不仅不能提升性能，还会导致性能下降。

2.偏向锁定在同步子系统中引入了许多复杂的代码，并且还侵入了其他 HotSpot 组件。这种复杂性是理解代码各个部分的障碍，也是在同步子系统内进行重大设计更改的障碍。为此，我们希望禁用、弃用并最终移除对偏向锁定的支持。
https://openjdk.org/jeps/374		


实战/技巧：避免Sysnchronized和Spring的动态代理交叉使用的坑，比如@Transactional，以及避免使用引用实例会变化的对象做锁对象。		

知识详解：

Sysnchronized锁升级过程

![img](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimages2.10qianwan.com%2F10qianwan%2F20180411%2Fb_0_201804110626062944.jpg&refer=http%3A%2F%2Fimages2.10qianwan.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1667375946&t=749cff6559c8c260b05efb53a0863eb2)


![在这里插入图片描述](https://img-blog.csdnimg.cn/1e6582ac17844ca9bd4426efd52edda8.png)



##### 重量级锁

`ObjectMonitor() {
_header = NULL;
_count = 0;
_waiters = 0,
_recursions = 0;
_object = NULL;
_owner = NULL;`

//等待队列，waiting
_WaitSet = NULL;
_WaitSetLock = 0 ;
_Responsible = NULL ;
_succ = NULL ;
//多线程竞争锁进入时的单向链表
_cxq = NULL ;
FreeNext = NULL ;
//_owner从该双向循环链表中唤醒线程结点，_EntryList是第一个节点
_EntryList = NULL ;
_SpinFreq = 0 ;
_SpinClock = 0 ;
OwnerIsThread = 0 ;
_previous_owner_tid = 0;
}`

Objectmonitor中的关键词

- EntryList
- WaitList
- cxq(ContentionList)
- Owner
- OnDeckThread
- recursions

###### cxq(竞争列表)

cxq是一个单向链表。被挂起线程等待重新竞争锁的链表, monitor 通过CAS将包装成ObjectWaiter写入到列表的头部。为了避免插入和取出元素的竞争，所以Owner会从列表尾部取元素。

![img](https:////upload-images.jianshu.io/upload_images/18113429-62551fe64ddc79bf.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/597/format/webp)

重量级锁3.jpg



###### EntryList(锁候选者列表)

​		EntryList是一个双向链表。当EntryList为空，cxq不为空，Owener会在unlock时，将cxq中的数据移动到EntryList。并指定EntryList列表头的第一个线程为OnDeck线程。

EntryList跟cxq的区别

​		在cxq中的队列可以继续自旋等待锁，若达到自旋的阈值仍未获取到锁则会调用park方法挂起。而EntryList中的线程都是被挂起的线程。

###### WaitList

​		WatiList是Owner线程地调用wait()方法后进入的线程。进入WaitList中的线程在notify()/notifyAll()调用后会被加入到EntryList。

###### Owner

​		当前锁持有者。

###### OnDeckThread

​		可进行锁竞争的线程。若一个线程被设置为OnDeck，则表明其可以进行tryLock操作，若获取锁成功，则变为Owner,否则仍将其回插到EntryList头部。

#### OnDeckThread竞争锁失败的原因

​		cxq中的线程可以进行自旋竞争锁，所以OnDeckThread若碰上自旋线程就需要和他们竞争

###### recursions(重入计数器)

​		用来表示某个线程进入该锁的次数。 



###### 执行流程

获取monitor

1. 线程首先通过CAS尝试将monitor的owner设置为自己。
2. 若执行成功，则判断该线程是不是重入。若是重入，则执行recursions + 1,否则执行recursions = 1。
3. 若失败，则将自己封装为ObjectWaiter，并通过CAS加入到cxq中。

释放monitor

1. 判断是否为重量级锁，是则继续流程。
2. recursions - 1
3. 根据不同的策略设置一个OnDeckThread


![img](https://img-blog.csdnimg.cn/20210815163352506.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0NDE2MTkx,size_16,color_FFFFFF,t_70)

![img](https://img2020.cnblogs.com/blog/1559627/202105/1559627-20210511154125098-1137705222.png)

参考资料: 锁升级 https://blog.csdn.net/weixin_36114346/article/details/125280063

​				重量级锁：https://www.jianshu.com/p/60ea4b0d4487

​				批量重偏向 https://blog.51cto.com/u_11720620/5198494



#### 死锁与活锁的区别，死锁与饥饿的区别？



#### 协程/纤程是什么？

答案：可以理解为轻量级线程，或用户线程。

​			基础层在线程层级上虚拟出更细的颗粒度计算单元，虚拟出一种轻量化线程模型，协程间切换不存在线程上下文切换的问题。

知识背景：	

​	今天对Web应用的服务要求，不论是 在请求数量上还是在复杂度上，与十多年前相比已不可同日而语，这一方面是源于业务量的增长，另 一方面来自于为了应对业务复杂化而不断进行的服务细分。现代B/S系统中一次对外部业务请求的响 应，往往需要分布在不同机器上的大量服务共同协作来实现，这种服务细分的架构在减少单个服务复 杂度、增加复用性的同时，也不可避免地增加了服务的数量，缩短了留给每个服务的响应时间。这要 求每一个服务都必须在极短的时间内完成计算，这样组合多个服务的总耗时才不会太长;也要求每一 个服务提供者都要能同时处理数量更庞大的请求，这样才不会出现请求由于某个服务被阻塞而出现等待。

​		Java目前的并发编程机制就与上述架构趋势产生了一些矛盾，1:1的内核线程模型是如今Java虚拟 机线程实现的主流选择，但是这种映射到操作系统上的线程天然的缺陷是切换、调度成本高昂，系统 能容纳的线程数量也很有限。以前处理一个请求可以允许花费很长时间在单体应用中，具有这种线程 切换的成本也是无伤大雅的，但现在在每个请求本身的执行时间变得很短、数量变得很多的前提下， 用户线程切换的开销甚至可能会接近用于计算本身的开销，这就会造成严重的浪费。

​		传统的Java Web服务器的线程池的容量通常在几十个到两百之间，当程序员把数以百万计的请求 往线程池里面灌时，系统即使能处理得过来，但其中的切换损耗也是相当可观的。现实的需求在迫使 Java去研究新的解决方案，同大家又开始怀念以前绿色线程的种种好处，绿色线程已随着Classic虚拟 机的消失而被尘封到历史之中，它还会有重现天日的一天吗?

协程就是为解决以上问题的一种解决方案。

​		OpenJDK在2018年创建了Loom项 目，这是Java用来应对本节开篇所列场景的官方解决方案，根据目前公开的信息，如无意外，日后该 项目为Java语言引入的、与现在线程模型平行的新并发编程机制中应该也会采用“纤程”这个名字