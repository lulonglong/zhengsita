## 原则

回答面试问题的原则

1.题面的答案

2.延伸深层次知识

3.实战：实战中的一些小技巧，一些踩过的坑经历。或者是举例一些应用场景。


## 问题

### 基础篇

#### 1.StringBuffer 和 StringBuilder 的区别

最大区别在于，StringBuffer 是线程安全的，而 StringBuilder 是非线程安全的，但 StringBuilder 的性能却高于 StringBuffer，所以在单线程环境下推荐使用 StringBuilder，多线程环境下推荐使用 StringBuffer 。

延伸：虽然java对Synchronized 做了优化，即使在无锁状态也要进行检查，和对锁进行偏向升级等额外开支。

延伸点：锁消除，锁粗化

#### 2.初始化顺序问题：

```java
class Bowl {
   Bowl(int marker) {
      System.out.println("Bowl(" + marker + ")");
   }
}

 class Tableware {
   static Bowl bowl5 = new Bowl(5);

   static {
      System.out.println("Tableware静态代码块");
   }


   Tableware() {
      System.out.println("Tableware构造方法");
   }

   Bowl bowl4 = new Bowl(4);
}

class Table extends Tableware {
   {
      System.out.println("Table非静态代码块_1");
   }

   Bowl bowl3 = new Bowl(3);    // 9

   {
      System.out.println("Table非静态代码块_2");
   }

   static Bowl bowl2 = new Bowl(2);

   static {
      System.out.println("Table静态代码块");
   }

   Table() {
      System.out.println("Table构造方法");
   }

   static Bowl bowl1 = new Bowl(1);
   
   void otherMethod(int marker) {
      System.out.println("otherMethod(" + marker + ")");
   }
 
}

Class TestInit{
  static Table table = new Table();
	public static void main(String[] args) {
			System.out.println("main()");
			table.otherMethod(1);
	}
}
```

顺序原则：

1.先父类，再子类

2.先静态，后对象成员

3.代码块和字段，按照顺序优先。

4.代码块和字段，优于构造函数



#### 3.下面两个代码块能正常编译和执行吗？

```java
// 代码块1
short s1 = 1;
s1 = s1 + 1; //失败，原因是，int到short会存在损失


// 代码块2
short s1 = 1;
s1 += 1;//成功，因为底层会处理成s1=（short）（s1+ 1）
```



#### 4.基础考察，指出下题的输出结果

```java
 public static void main(String[] args) {
    Integer a = 128, b = 128, c = 127, d = 127;
   
    System.out.println(a == b);
    System.out.println(c == d);
}
```



#### 5.try、catch、finally 考察，请指出下面程序的运行结果。
```java
public class TryDemo {
    public static void main(String[] args) {
        System.out.println(test1());
				System.out.println(test2());
  }

  public static int test1() {
      try {
         return 2;
     } finally {
          return 3;
      }
   }

  public static int test2() {
        int i = 0;
        try {
            i = 2;
            return i;
        } finally {
            i = 3;
        }
    } 
}
```



#### 6.介绍下 HashMap 的底层数据结构



![img](https://img-blog.csdnimg.cn/img_convert/520e99cd9ee7bcb9911fab32c53a51ac.png)

https://joonwhee.blog.csdn.net/article/details/106324537

注释：存储的对象个数大于threshold（capacity*loadFactor）就进行扩容（扩桶的数量），目的是尽量把数据都直接在桶里，力求通过数据计算（位移）就能直接找到对象，无需在链上线性对比，或树上2叉查找。



#### 7. String s = new String("xyz") 创建了几个字符串对象？

1.如果jvm字符串常量池里已有xyz，则创建1个对象

2.如果“xyz”不存在常量池，则创建两个，一个在常量池，一个是s指向的对象（char数组）



#### 8.String s = "xyz" 和 String s = new String("xyz") 区别？

前者s是指向常量池中xyz的内存地址

后者s指向的是一个”xyz“的包装对象的内存地址 



#### 9.强引用、软引用、弱引用、虚引用有什么区别？

1. 强引用：强引用是最传统的“引用”的定义，是指在程序代码之中普遍存在的引用赋值。

2. 软引用：软引用是用来描述一些还有用，但非必须的对象。只被软引用关联着的对象，在系统将要发生内 存溢出异常前，会把这些对象列进回收范围之中进行第二次回收，如果这次回收还没有足够的内存， 才会抛出内存溢出异常。

   //创建软引用对象        

   DemoClass dc=new DemoClass()

   SoftReference<DemoClass> reference = new SoftReference<>(dc);

   应用场景：适合做本地缓存。既可以就近缓存数据，又弹性使用内存，在内存用尽时会自动回收这部分对象。小对象没必要使用，因为本身会额外创建出一个封住那个对象SoftReference。

3. 弱引用：也是用来描述那些非必须对象，但是它的强度比软引用更弱一些，被弱引用关联的对象只 能生存到下一次垃圾收集发生为止。当垃圾收集器开始工作，无论当前内存是否足够，都会回收掉只 被弱引用关联的对象。在JDK 1.2版之后提供了WeakReference类来实现弱引用。

   应用场景：可在辅助类中使用，实体业务对象种强引用，辅助类中弱引用，在业务对象超出作用域强引用释放后，弱引用即使释放。

   业务对象未终结前，又可以使用辅助功能，比如：threadlocal本地变量的实现

4. 虚引用：最弱的一种引用。与软、弱引用不同，虚引用指向的对象十分脆弱，无法通过 get 方法得到其指向的对象。它的唯一作用就是当其指向的对象被回收之后，自己被加入到引用队列，用作记录该引用指向的对象已被销毁。

   应用场景：监测对象的释放，然后做相应的操作。比如记录销毁时间，释放一些对应堆外资源资源等。



#### 10. JDK1.8之后有哪些新特性？

https://www.zhihu.com/question/513873484/answer/2417344098



#### 11.为什么String类实例要设定为不可变？

字符串缓冲池，使用享元模式，减少String对象的产生，而享元模式的弊端就是对象的内部状态不可变。
使用安全，String是我们程序中最最常用的对象，不可变规避了直接在堆中直接改变对象内容，除非我们主动修改引用地址，否则我们将String传递进任何方法中，他都不会改变。防止一不小心就在其他函数内被更改了。
线程安全，并发使用String时，不需要再去考虑他的线程安全问题。
性能，由于String一出生或一初始化就不可变，所以一个String的hashcode是固定的不变的，可以只计算一次，并且String是最适合作为hash表结构中Key的类。
设定为不可变之后，String就更贴近一个基本类型了，这可能也是JDK开发者的期望。



JVM 使用一种StringTable（哈希表）数据结构，来存储所有字符串对象。



个人理解仅作参考：一般程序中存在着大量字符串的使用，且这些字符串大多是硬编码在代码中的，或采用硬编码去拼接。而这些静态程序在运行期间持续反复执行，

所以针对这种特性，jvm 构建了一个字符串池，对此种对象进行全局共享处理，因为是全局共享，所以不能对其进行更改，以免影响其他使用代码，如需要修改，则先判断字符串池中是否已有改后字符串，如果没有则直接创建新的String对象。

#### 12.简述 BIO, NIO, AIO 的区别

![BIO、NIO、AIO 介绍和适用场景分析（绝对经典）](https://www.likecs.com/default/index/img?u=aHR0cHM6Ly9waWFuc2hlbi5jb20vaW1hZ2VzLzY3NS9jYzFhZjViYTU3MTc5NTJhODMyNmQyNmY3NmMyNjU0Yi5wbmc=)

BIO：同步并阻塞，服务实现模式为一个连接对应一个线程，即客户端发送一个连接，服务端要有一个线程来处理。如果连接多了，线程数量不够，就只能等待。

​			总结：阻塞在每一个客户端连接上。



![BIO、NIO、AIO 介绍和适用场景分析（绝对经典）](https://www.likecs.com/default/index/img?u=aHR0cHM6Ly9waWFuc2hlbi5jb20vaW1hZ2VzLzg0MC9iMmY3ZjljMzM2ZjI4YzEzOWM5NDBmNmQyZGRhNWNkMC5wbmc=)

NIO：同步非阻塞，服务实现模式是一个线程可以处理多个连接，即客户端发送的连接都会注册到多路复用器上，然后进行轮询连接，有I/O请求就处理。

​			总结：阻塞是阻塞在一个客户端连接组上。



![img](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg-blog.csdnimg.cn%2Fimg_convert%2Fb2f5ee71880bc297114df4aea4d25899.png&refer=http%3A%2F%2Fimg-blog.csdnimg.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1668240985&t=49554e4eca95c4736ec4815e26dbd9a6)

AIO：异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的I/O请求都是由OS先完成了再通知服务器应用去启动线程进行处理

总结：形势上的完全非阻塞

应用场景：

BIO：适用连接数目比较小且固定的架构，对服务器要求比较高，并发局限于应用中。如类似datax数据同步，每一个链接都是持续大量数据传输。

NIO：适用连接数目多且连接比较短的架构，如：聊天服务器，弹幕系统等，编程比较复杂

AIO：适用连接数目多且连接长的架构，如相册服务器 



#### 13、DCL单例模式中，为什么要加volatile

DCL：double check lock双重检查锁

https://juejin.cn/post/7102222154518757383



#### 14、类锁和对象的区别

[请说一下类锁和和对象锁的区别](https://www.cnblogs.com/fengzheng/p/12066239.html)

类锁：
类锁是加载类上的，而类信息是存在 JVM 方法区的，并且整个 JVM 只有一份，方法区又是所有线程共享的，所以类锁是所有线程共享的。

使用类锁的方式有如下方式：
* 锁住类中的静态变量。因为静态变量和类信息一样也是存在方法区的并且整个 JVM 只有一份，所以加在静态变量上可以达到类锁的目的。
* 直接在静态方法上加 synchronized。因为静态方法同样也是存在方法区的并且整个 JVM 只有一份，所以加在静态方法上可以达到类锁的目的。
* 锁住 xxx.class。
* 类锁是所有线程共享的锁，所以同一时刻，只能有一个线程使用加了锁的方法或方法体，不管是不是同一个实例。

对象锁：
修饰一个对象，同一类型不同对象之间，是不同的锁
* 修饰一个非静态成员变量

* 修饰一个非静态方法

* 修饰一个非静态代码块

* synchronized(this)

  

#### 15、24大设计模式和7个原则
![24大设计模式和7个原则](https://pdai.tech/md/dev-spec/pattern/1_overview.html)

#### ConcurrentHashMap 分段锁
[简单谈谈ConcurrentHashMap](https://juejin.cn/post/7031106182446055432)



#### 16、说说数组和链表的区别

关键点
内存分配
查找方式
修改成本
扩容成本
https://juejin.cn/post/6935049364616249381



#### 17. 说说Serializable原理
[解析Serializable原理](https://juejin.cn/post/6844904049997774856#heading-6)



#### 18. 枚举类的用法

[关于枚举类你可能不知道的事](https://www.cnblogs.com/54chensongxia/p/11581555.html)



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

### 虚拟机篇

#### 1.谈谈对象的内存布局

![img](https://img-blog.csdnimg.cn/img_convert/c8a8cbd8005e2f08cc640b9b35fd64ae.png)

![img](https://img-blog.csdnimg.cn/img_convert/a146f04f1c52599a60594005b09b5fe9.png)

![img](https://img-blog.csdnimg.cn/img_convert/f39e65d1f33ddb8593218e5e3ee95b5e.png)


对象实例数据会考虑内存对齐，会对对象进行字段重排（类型指针指向的类对象上已体现），原则把短字段对齐到32或64位，

最终会对包含对象头和对象数据整个对象大小，是否达到32或64整数位，不是则进行填充0进行对齐。

对象的最小内存，普通对象16字节，数组对象24个字节（不考虑指针压缩的情况的下）。

一个缓存行的长度是64个字节。

todo 指针压缩


#### 2.你知道的垃圾回收器有哪些？

jdk1.8默认，Parallel Scavenge(并行 复制算法)和Parallel Old(并发 标记-清除)

Parallel（Scavenge+Old）、CMS（不能单独使用，需要Serial、ParNew 这两个新生代配合）、G1、ZGC、Shenandoah

![image.png](https://i0.hdslb.com/bfs/article/0184461b7753a8a798e98212d80b2bd07a57b718.png@648w_348h_progressive.webp)

图中展示了7种作用于不同分代的收集器，如果两个收集器之间存在连线，则说明它们可以搭配使用。虚拟机所处的区域则表示它是属于新生代还是老年代收集器。

新生代收集器（全部的都是复制算法）：Serial、ParNew、Parallel Scavenge

老年代收集器：CMS（标记-清理）、Serial Old（标记-整理）、Parallel Old（标记整理）

整堆收集器： G1（一个Region中是标记-清除算法，2个Region之间是复制算法） 

![老的GC的内存结构图](http://zhaoyanblog.com/wp-content/uploads/2014/07/zhaoyanblog_2014-07-06_01-25-19.png)

##### Parallel（Scavenge+Old）：

##### jdk 7和8 默认使用的垃圾回收器，特点是并行处理，缺点整个过程中用户线程是暂停状态。

![img](https://img-blog.csdnimg.cn/57805d9527c349758a07ade0b9cc7832.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAa2sgwrc=,size_20,color_FFFFFF,t_70,g_se,x_16)

##### CMS

##### 全称：Concurrent Mark Sweep，翻译过来是**并发标记清除**，特点是部分过程是并发处理的，并发阶段用户线程是可以工作的。

CMS(Concurrent Mark Sweep)收集器是一种以获取最短回收停顿时间为目标的收集器。目前很 大一部分的Java应用集中在互联网网站或者基于浏览器的B/S系统的服务端上，这类应用通常都会较为 关注服务的响应速度，希望系统停顿时间尽可能短，以给用户带来良好的交互体验。CM S收集器就非 常符合这类应用的需求。

![在这里插入图片描述](https://img-blog.csdnimg.cn/96cd6583766c479fa5479720e4c43d79.png)

##### **G1垃圾收集器**

G1（Garbage-First）是一款面向服务端应用的垃圾收集器，主要针对配备多核cpu及大容量内存的机器，以及高概率满足GC停顿时间的同时，还兼顾高吞吐量的的性能特征

在JDK1.7版本正式启用，移除了Experimental的标识，是JDK 9以后的默认垃圾回收器，取代了CMS 回收器以及Parallel + Parallel Ol的组合。被Oracle官方称为**“全功能垃圾收集器”**。

![img](https://ask.qcloudimg.com/http-save/yehe-8223537/0c8b04f82e26b735d88ed4bf1f7d668f.webp?imageView2/2/w/1620/format/jpg)

O表示老生代（Old），E表示Eden，S表示Survivor

![img](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fwww.likecs.com%2Fdefault%2Findex%2Fimg%3Fu%3DaHR0cHM6Ly9pbWcyMDIwLmNuYmxvZ3MuY29tL2Jsb2cvMTMzNDAyMy8yMDIwMDcvMTMzNDAyMy0yMDIwMDcxMjA4NDAxOTgwMi0xMjQzNDE0ODQ3LnBuZw%3D%3D&refer=http%3A%2F%2Fwww.likecs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1667241315&t=ed7e4f40fd257c30b74202b3cbc1c0cd)


##### ZGC

- JDK11 中开始推出，追求超低时延，源自 Azul System 的 C4

- 目标

  - 停顿时间不超过10ms（JDK16已经达到不超过1ms）**
  - 停顿时间不会随着堆的大小，或者活跃对象的大小而增加
  - 支持8MB~4TB级别的堆，JDK15后已经可以支持16TB

- 支持 NUMA

​	部分特点：堆空间无代概念，染色指针+Forwarding Tabels实现更充分的并发。

![img](https://pic2.zhimg.com/80/v2-7f4b6bea067572cd7182735aa8a943c5_720w.webp)

![img](https://pic1.zhimg.com/80/v2-754c7da742d2d936654b2a607dc840f8_720w.webp)


#### 3.GC Root有哪些?

**Java中可以作为GC Roots的对象**

1、[虚拟机](https://so.csdn.net/so/search?q=虚拟机&spm=1001.2101.3001.7020)栈（javaStack）（栈帧中的局部变量区，也叫做局部变量表）中引用的对象。

2、方法区中的类静态属性引用的对象。

3、方法区中常量引用的对象。

4、本地方法栈中JNI([Native](https://so.csdn.net/so/search?q=Native&spm=1001.2101.3001.7020)方法)引用的对象

#### 4. 垃圾收集有哪些算法，各自的特点？

##### 1. Mark-Sweep（标记-清除）算法

![img](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9pbWFnZXMwLmNuYmxvZ3MuY29tL2kvMjg4Nzk5LzIwMTQwNi8xODEwMjQzODIzOTgxMTUuanBn)

标记-清除算法分为两个阶段：***\*标记阶段和清除阶段\****，标记出来活着的内存，然后清理掉死掉的对象占用

缺点：容易产生内存碎片


##### 2. Copying（复制）算法

![img](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9pbWFnZXMwLmNuYmxvZ3MuY29tL2kvMjg4Nzk5LzIwMTQwNi8xODEwNDE1Mjg0ODg3MjguanBn)

以空间换时间，分出两块大小相同的区域做复制使用，A区域程序使用，B区域垃圾回收备用。

第一步标记，标记活着的对象

第二步复制，把A区活着的对象拷贝到B区

第三步，清理整个A区

第四步，A，B身份互换

缺点，空间代价高。


##### 3. Mark-Compact（标记-整理）算法

![img](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9pbWFnZXMwLmNuYmxvZ3MuY29tL2kvMjg4Nzk5LzIwMTQwNi8xODExMDAxMjk1NzU5MTYuanBn)

其中的标记过程仍然与“标记-清除”算法一样，但后续步骤不是直接对可 回收对象进行清理，而是让所有存活的对象都向内存空间一端移动，然后直接清理掉边界以外的内存。


##### 4. Generational Collection（分代收集）算法

这是一个综合方案，把堆内存划分出多个代，每一代采用不同的收集方法

![老的GC的内存结构图](http://zhaoyanblog.com/wp-content/uploads/2014/07/zhaoyanblog_2014-07-06_01-25-19.png) 

eden，S0，S1 采用标记-复制，每次把eden+非备用Survivor区复制到，备用survivor区，然后清理两块整区域。

Tenured采用标记-整理



#### 5.类加载的过程?

![img](https://pics4.baidu.com/feed/aa18972bd40735fa97c3daafb995a6b50f24083b.jpeg@f_auto?token=d486b2d90ab80d502c688d1f62fccd64)

![img](https://img-blog.csdnimg.cn/8944e966b1a64bd2938cab97d4c93225.png)

#### 6.java有哪些类加载器？什么是双亲委派模型？

![img](https://img0.baidu.com/it/u=3803485979,4100471868&fm=253&fmt=auto&app=138&f=PNG?w=758&h=500)

当一个类加载器收到类加载任务时，会先交给自己的父加载器去完成，因此最终加载任务都会传递到最顶层的BootstrapClassLoader，只有当父加载器无法完成加载任务时，才会尝试自己来加载。

  用双亲委派模型来组织类加载器之间的关系，有一个显而易见的好处就是：Java类随着它的类加载器一起具备了一种带有优先级的层次关系。例如类java.lang.Object，它存放在rt.jar之中，无论哪一个类加载器要加载这个类，最终都是委派给处于模型最顶端的启动类加载器进行加载，因此Object类在程序的各种类加载器环境中都是同一个类。

​    相反，如果没有使用双亲委派模型，由各个类加载器自行去加载的话，如果用户自己编写了一个称为java.lang.Object类，并放在程序的ClassPath中，那系统中将会出现多个不同的Object类，Java类型体系中最基础的行为也就无法保证，应用程序也将会变得一片混乱。


**双亲委派机制优势：**

**避免类的重复加载**

当自己程序中定义了一个和Java.lang包同名的类，此时，由于使用的是双亲委派机制，会由启动类加载器去加载JAVA_HOME/lib中的类，而不是加载用户自定义的类。此时，程序可以正常编译，但是自己定义的类无法被加载运行。

**保护程序安全**

防止核心API被随意篡改。通过委托方式，不会去篡改核心.class，即使篡改也不会去加载，即使加载也不会是同一个.class对象了。不同的加载器加载同一个.class也不是同一个Class对象。这样保证了Class执行安全。 

双亲委派模型并不是一个具有强制性约束的模型，而是Java设计者推荐给开发者们的类加载器实现方式。在Java的世界中大部分的类加载器都遵循这个模型，但也有例外的情况，比如OSGi为实现模块化热部署破坏了这个模型。


#### 7.介绍一下Java 内存结构（运行时数据区)。

![img](https://pics3.baidu.com/feed/48540923dd54564e6401f2a939d79c8bd0584fc8.png@f_auto?token=0aff26b9778e49619f85474c29b63b1c)


#### 8.你知道“内存对齐”吗？（内存对齐，对齐填充）

![接近2万字细说 JVM 内存分布、内存对齐、压缩指针](https://i0.hdslb.com/bfs/article/051ebdd00817a56ea424778038b097006a6b8fb0.png@942w_726h_progressive.webp)

内存和cpu缓存交换数据是以cacaeline（缓存行）为单位，目前主流的处理器cacaeline 大小为64字节

java有个@Contended 注解显示指定某个字段或某个类的所有字段，独占一行 cacaeline（底层是补0实现）。

对齐和补齐主要解决的问题：

1. 提升性能，由于对象字段大小不一，如果 不对齐可能造成一个字段夸行，比如一个long字段8字节，4字节在a行，4字节在b行，一次装载就需要两次操作。

2. 解决伪共享问题

   
#### 9.jvm运行时区内容

![在这里插入图片描述](https://img-blog.csdnimg.cn/2f13414f069c461986cb2e3b5e06f91a.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAbmV3IGhpbGJlcnQoKQ==,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center)


#### 10.你对染色指针了解多少？

**染色指针是一种直接将少量额外的信息存储在指针上的技术**

染色指针**直接把标记信息记在引用对象的指针上**（这个时候，与其说可达性分析是遍历对象图来标记对象，还不如说是遍历“引用图”来标记“引用”了。）



参照资料：https://cloud.tencent.com/developer/article/1857112

​				   https://tech.meituan.com/2020/08/06/new-zgc-practice-in-meituan.html


#### 11. 垃圾回收的时机

##### Minor GC：

大多数情况下，对象在新生代Eden区中分配。当Eden区没有足够空间进行分配时，虚拟机将发起一次Minor GC。

##### Full GC：

在发生Minor GC之前，虚拟机必须先检查老年代最大可用的连续空间是否大于新生代所有对象总 空间，如果这个条件成立，那这一次Minor GC可以确保是安全的。如果不成立，则虚拟机会先查看- XX:HandlePromotionFailure参数的设置值是否允许担保失败(Handle Promotion Failure);如果允 许，那会继续检查老年代最大可用的连续空间是否大于历次晋升到老年代对象的平均大小，如果大 于，将尝试进行一次Minor GC，尽管这次Minor GC是有风险的;如果小于，或者-XX: HandlePromotionFailure设置不允许冒险，那这时就要改为进行一次Full GC

分代管理：HotSpot虚拟机中多数收集器都采用了分代收集来管理堆内存，那内存回收时就必须能决策哪些存 活对象应当放在新生代，哪些存活对象放在老年代中。为做到这点，虚拟机给每个对象定义了一个对 象年龄(Age)计数器，存储在对象头中(详见第2章)。对象通常在Eden区里诞生，如果经过第一次 Minor GC后仍然存活，并且能被Survivor容纳的话，该对象会被移动到Survivor空间中，并且将其对象 年龄设为1岁。对象在Survivor区中每熬过一次Minor GC，年龄就增加1岁，当它的年龄增加到一定程 度(默认为15)，就会被晋升到老年代中。对象晋升老年代的年龄阈值，可以通过参数-XXM axTenuringThreshold设置。


注意：在jvm发展当中，发展出了各种垃圾回收器，叫法各有不同。因为ZGC已经没有分代，都是采用标记复制算法的区域收集。

- Young GC/Minor GC：只收集young gen的GC

- Old GC：只收集old gen的GC。只有CMS的concurrent collection是这个模式

- Mixed GC：收集整个young gen以及部分old gen的GC。只有G1有这个模式 

- **Full GC：**收集整个堆，包括young gen、old gen、perm gen（如果存在的话），较为通用的叫法

  
##### ZGC：

 ZDirector和ZStat都是通过时钟触发器来控制是否执行业务。  

 ZDirector提供了4种触发垃圾回收的方法，分别是基于固定时间间隔，预热规则、分配速率和主动触发规则。ZDirector依次判断这4种规则是否满足，实际上这也说明了规则的优先级。  ![图片](https://mmbiz.qpic.cn/mmbiz_png/rEPTvCgZseKOYIuQBNPvqMaDJA5jkYCsDAvcJ3at4TCROwqJNqfVnky5Qzd52uE0tbsJibsqGXXR2PA7Vwgoaww/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

###### 固定时间：

时间间隔由一个参数ZCollectionInterval来控制，这个参数的默认值为0，表示不需要触发垃圾回收。实际工作中，可以根据场景设置该参数。

###### 预热：

设计这一规则的目的是当JVM刚启动时，还没有足够的数据来主动触发垃圾回收的启动，所以设置了预热规则。
预热规则指的是JVM启动后，当发现堆空间使用率达到10%、20%和30%时，会主动地触发垃圾回收。ZGC设计前3次垃圾回收可由预热规则触发，也就是说当垃圾回收触发（无论是由预热规则，还是主动触发垃圾回收）的次数超过3次时，预热规则将不再生效。 

###### 根据分配速率

这一规则设计的思路是：
1）收集数据：在程序运行时，收集过去一段时间内垃圾回收发生的次数和执行的时间、内存分配的速率MEMratio和当前空闲内存的大小MEMfree。
2）计算：根据过去垃圾回收发生的情况预测下一次垃圾回收发生的时间TIMEgc，按照内存分配的速率预测空闲内存能支撑应用程序运行的实际时间TIMEoom，例如TIMEoom=MEMfree/MEMratio。
3）设计规则：如当TIMEoom小于TIMEgc（垃圾回收的时间），则可以启动垃圾回收。这个规则的含义是如果从现在起到OOM发生前开始执行垃圾回收，刚好在OOM发生前完成垃圾回收的动作，从而避免OOM。在ZGC中ZDirector是周期运行的，所以在计算时还应该把OOM的时间减去采样周期的时间，采样周期记为TIMEinterval，则规则为TIMEoom<TIMEgc+TIMEinterval时触发垃圾回收。 


###### 主动触发

ZDirector提供的第四个规则是主动触发规则，该规则是为了应用程序在吞吐量下降的情况下，当满足一定条件时，还可以执行垃圾回收。这里满足一定条件指的是：
1）从上一次垃圾回收完成到当前时间，应用程序新增使用的内存达到堆空间的10%。
2）从上一次垃圾回收完成到当前时间已经过去了5min，记为TIMEelapsed。
如果这两个条件同时满足，预测垃圾回收时间为TIMEgc，定义规则：如果NUMgc * TIMEgc < TIMEelapsed，则触发垃圾回收。其中NUMgc是ZGC设计的常量，假设应用程序的吞吐率从50%下降到1%，需要触发一次垃圾回收。 

注意：ZDirector虽然实现为并发线程，但在ZGC中只有一个，所以ZDirector不会涉及并发的问题。

  

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


### 虚拟机篇

#### 你知道“内存对齐”吗？
![img](https://upload-images.jianshu.io/upload_images/26273155-b4cfc2c19c37bd2c.png?imageMogr2/auto-orient/strip|imageView2/2/w/696/format/webp)


#### 你知道的垃圾回收器有哪些？

字面答案：Parallel（Scavenge+Old）、CMS、G1、ZGC、Shenandoah

延伸：JDK 8 默认采用Parallel（Scavenge+Old）,G1 是目前最稳定的也是最新虚拟机默认支持的

​			ZGC是目前最先进的垃圾回收器。



详解知识点：

##### Parallel Scavenge + Parallel Old：

![img](https://img-blog.csdnimg.cn/57805d9527c349758a07ade0b9cc7832.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAa2sgwrc=,size_20,color_FFFFFF,t_70,g_se,x_16)

​															Parallel Scavenge 加 Parallel Old运行示意图

Parallel Scavenge 收集器也是一款新生代收集器，它同样是基于标记-复制算法实现的收集器，也是 能够并行收集的多线程收集器……Parallel Scavenge的诸多特性从表面上看和ParNew非常相似，那它有 什么特别之处呢？ Parallel Scavenge收集器的特点是它的关注点与其他收集器不同，CMS等收集器的关注点是尽可能 地缩短垃圾收集时用户线程的停顿时间，而**<u>Parallel Scavenge收集器的目标则是达到一个可控制的吞吐 量（Throughput）</u>**。所谓吞吐量就是处理器用于运行用户代码的时间与处理器总消耗时间的比值，

​		如果虚拟机完成某个任务，用户代码加上垃圾收集总共耗费了100分钟，其中垃圾收集花掉1分 钟，那吞吐量就是99%。停顿时间越短就越适合需要与用户交互或需要保证服务响应质量的程序，良 好的响应速度能提升用户体验;而高吞吐量则可以最高效率地利用处理器资源，尽快完成程序的运算任务，**<u>主要适合在后台运算而不需要太多交互的分析任务</u>**

Parallel Old是Parallel Scavenge收集器的老年代版本，支持多线程并发收集，基于标记-整理算法实 现。这个收集器是直到JDK 6时才开始提供的，在此之前，新生代的Parallel Scavenge收集器一直处于相 当尴尬的状态，原因是如果新生代选择了Parallel Scavenge收集器，老年代除了Serial Old(PS
 MarkSweep)收集器以外别无选择，其他表现良好的老年代收集器，如CM S无法与它配合工作。

​	直到Parallel Old收集器出现后，“吞吐量优先”收集器终于有了比较名副其实的搭配组合，在注重 吞吐量或者处理器资源较为稀缺的场合，都可以优先考虑Parallel Scavenge加Parallel Old收集器这个组 合



##### CMS

![在这里插入图片描述](https://img-blog.csdnimg.cn/96cd6583766c479fa5479720e4c43d79.png)

CMS(Concurrent Mark Sweep)收集器是一种以获取最短回收停顿时间为目标的收集器。目前很 大一部分的Java应用集中在互联网网站或者基于浏览器的B/S系统的服务端上，这类应用通常都会较为 关注服务的响应速度，希望系统停顿时间尽可能短，以给用户带来良好的交互体验。CM S收集器就非 常符合这类应用的需求。

从名字(包含“Mark Sweep”)上就可以看出CMS收集器是基于标记-清除算法实现的，它的运作 过程相对于前面几种收集器来说要更复杂一些，整个过程分为四个步骤，包括:

1、初始标记
初始标记仅仅只是标记一下GC Roots能直接关联到的对象，此过程需要“Stop The World”，停止用户进程。不过这个阶段非常快。

2、并发标记
并发标记就是从GC Roots的直接关联对象开始遍历整个对象图的过程，这个过程耗时较长但不需要停顿用户线程

3、重新标记
重新标记阶段是为了修正并发标记期间，因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录。这个阶段也会停顿用户线程，比初始标记时间要长，但远比并发标记阶段时间要短。

4、并发清除
清理删除掉标记阶段判断的已经死亡的 对象，由于不需要移动存活对象，所以这个阶段也是可以与用户线程同时并发的。 

缺陷：**对处理器资源敏感**、**无法处理浮动垃圾**、**产生大量空间碎片**

##### **G1垃圾收集器**

G1开创的基于Region的堆内存布局是它能够实现这个目标的关键。虽然G1也仍是遵循分代收集理 论设计的，但其堆内存的布局与其他收集器有非常明显的差异:G1不再坚持固定大小以及固定数量的 分代区域划分，而是把连续的Java堆划分为多个大小相等的独立区域(Region)，每一个Region都可以根据需要，扮演新生代的Eden空间、Survivor空间，或者老年代空间。收集器能够对扮演不同角色的 Region采用不同的策略去处理，这样无论是新创建的对象还是已经存活了一段时间、熬过多次收集的 旧对象都能获取很好的收集效果。

Region中还有一类特殊的Humongous区域，专门用来存储大对象。G1认为只要大小超过了一个
 R e gi o n 容 量 一 半 的 对 象 即 可 判 定 为 大 对 象 。 每 个 R e gi o n 的 大 小 可 以 通 过 参 数 - X X : G 1 H e a p R e gi o n Si z e 设 定，取值范围为1M B~32M B，且应为2的N次幂。而对于那些超过了整个Region容量的超级大对象， 将会被存放在N个连续的Humongous Region之中，G1的大多数行为都把Humongous Region作为老年代 的一部分来进行看待，如图3-12所示。

虽然G1仍然保留新生代和老年代的概念，但新生代和老年代不再是固定的了，它们都是一系列区 域(不需要连续)的动态集合。G1收集器之所以能建立可预测的停顿时间模型，是因为它将Region作 为单次回收的最小单元，即每次收集到的内存空间都是Region大小的整数倍，这样可以有计划地避免 在整个Java堆中进行全区域的垃圾收集。更具体的处理思路是让G1收集器去跟踪各个Region里面的垃 圾堆积的“价值”大小，价值即回收所获得的空间大小以及回收所需时间的经验值，然后在后台维护一 个优先级列表，每次根据用户设定允许的收集停顿时间(使用参数-XX:M axGCPauseM illis指定，默 认值是200毫秒)，优先处理回收价值收益最大的那些Region，这也就是“Garbage First”名字的由来。 这种使用Region划分内存空间，以及具有优先级的区域回收方式，保证了G1收集器在有限的时间内获 取尽可能高的收集效率。

Hotspot 堆示意图

![老的GC的内存结构图](http://zhaoyanblog.com/wp-content/uploads/2014/07/zhaoyanblog_2014-07-06_01-25-19.png)



![img](https://ask.qcloudimg.com/http-save/yehe-8223537/0c8b04f82e26b735d88ed4bf1f7d668f.webp?imageView2/2/w/1620/format/jpg)

 O表示老生代（Old），E表示Eden，S表示Survivor

![img](https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fwww.likecs.com%2Fdefault%2Findex%2Fimg%3Fu%3DaHR0cHM6Ly9pbWcyMDIwLmNuYmxvZ3MuY29tL2Jsb2cvMTMzNDAyMy8yMDIwMDcvMTMzNDAyMy0yMDIwMDcxMjA4NDAxOTgwMi0xMjQzNDE0ODQ3LnBuZw%3D%3D&refer=http%3A%2F%2Fwww.likecs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1667241315&t=ed7e4f40fd257c30b74202b3cbc1c0cd)



##### ZGC

- JDK11 中开始推出，追求超低时延，源自 Azul System 的 C4

- 目标

- - **停顿时间不超过10ms（JDK16已经达到不超过1ms）**
  - 停顿时间不会随着堆的大小，或者活跃对象的大小而增加
  - 支持8MB~4TB级别的堆，JDK15后已经可以支持16TB

- 支持 NUMA

###### 内存布局

![img](https://pic2.zhimg.com/80/v2-7f4b6bea067572cd7182735aa8a943c5_720w.webp)

- 无分代

- 将内存划分为小区域——页面（page），分为大中小3中：

- - 小页面 ： 放置小于等于256KB的小对象。
  - 中页面 ： 放置256KB~4MB的对象。
  - 大页面 ： 放置4MB或以上的大对象。 **每个大页面只存放一个大对象**

- 内存布局如此设计原因？回收策略

- - **小页面优先回收；中页面和大页面则尽量不回收**

###### 核心概念-染色指针

- - ZGC 在指针中借了几个位来事情，所以它必须在64位的机器上才可工作
  - 因为要求64位的指针，也就不支持压缩指针
  - ZGC中低42位表示使用中的堆空间
  - ZGC借几位高位来做GC相关的事情(快速实现垃圾回收中的并发标记、转移和重定位等)

- - 

![img](https://ask.qcloudimg.com/http-save/yehe-1065851/469f7707c65565f7617ba7c21d508b7f.png?imageView2/2/w/1620)



###### 运作流程

- 根可达算法（GC Root）

- - 判断对象是否存活

  - 作为 GC Root 的对象种类

  - - 虚拟机栈（栈帧中的本地变量表）
    - 方法区中类静态变量
    - 方法区中常量
    - 本地方法栈中的 JNI 指针，即 native 方法

![img](https://pic1.zhimg.com/80/v2-754c7da742d2d936654b2a607dc840f8_720w.webp)



###### ZGC 触发机制（JDK16）

- 预热规则

- - 服务刚启动时出现，一般不需要关注。

- 基于分配速率的自适应算法

- - 最主要的GC触发方式（默认方式）
  - 其算法原理可简单描述为”ZGC根据近期的对象分配速率以及GC时间，计算出当内存占用达到什么阈值时触发下一次GC”
  - 通过ZAllocationSpikeTolerance参数控制阈值大小，该参数默认2，数值越大，越早的触发GC
  - 日志中关键字是“Allocation Rate”

- 基于固定时间间隔

- - 通过ZCollectionInterval控制，适合应对突增流量场景
  - 通过调整此参数解决流量突增场景的问题，比如定时活动、秒杀等场景

- 主动触发规则

- - 类似于固定间隔规则，但时间间隔不固定，ZGC自行算出来的时机
  - 通过-ZProactive参数将该功能关闭，以免GC频繁，影响服务可用性

- 阻塞内存分配请求触发

- - 当垃圾来不及回收，垃圾将堆占满时，会导致部分线程阻塞
  - 应当避免出现这种触发方式
  - 日志中关键字是“Allocation Stall”

- 外部触发

- - 代码中显式调用System.gc()触发
  - 日志中关键字是“System.gc()”

- 元数据分配触发

- - 元数据区不足时导致，一般不需要关注
  - 日志中关键字是“Metadata GC Threshold”

###### ZGC运用了哪些技术？

a)着色指针技术，使用着色指针技术来快速实现GC中的并发标记、转移和重定位等功能

b)堆空间使用分页模型

c)巧妙的使用转发表技术实现GC中的对象的并发转移

d)使用读屏障技术解决指针修正问题

参考资料：https://cloud.tencent.com/developer/article/1857112





