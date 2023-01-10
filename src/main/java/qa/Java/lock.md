

## LockSupport

`LockSupport`是一个线程阻塞工具类，所有的方法都是静态方法，可以让线程在任意位置阻塞，当然阻塞之后肯定得有唤醒的方法。归根结底，LockSupport调用的Unsafe中的native代码。

LockSupport是用来创建锁和其他同步类的基本**线程阻塞**原语。LockSupport 提供park()和unpark()方法实现阻塞线程和解除线程阻塞，LockSupport和每个使用它的线程都有一个许可(permit)关联。permit相当于1，0的开关，默认是0，调用一次unpark就加1变成1，调用一次park会消费permit, 也就是将1变成0，同时park立即返回。再次调用park会变成block（因为permit为0了，会阻塞在这里，直到permit变为1）, 这时调用unpark会把permit置为1。每个线程都有一个相关的permit, permit最多只有一个，重复调用unpark也不会积累。

如果调用线程被中断，则park方法会返回。同时park也拥有可以设置超时时间的版本。

```Java
public static void park(Object blocker); // 暂停当前线程
public static void parkNanos(Object blocker, long nanos); // 暂停当前线程，不过有超时时间的限制
public static void parkUntil(Object blocker, long deadline); // 暂停当前线程，直到某个时间
public static void park(); // 无期限暂停当前线程
public static void parkNanos(long nanos); // 暂停当前线程，不过有超时时间的限制
public static void parkUntil(long deadline); // 暂停当前线程，直到某个时间
public static void unpark(Thread thread); // 恢复当前线程
public static Object getBlocker(Thread t);
```

为什么叫park呢，park英文意思为停车。我们如果把Thread看成一辆车的话，park就是让车停下，unpark就是让车启动然后跑起来。

我们可以使用它来阻塞和唤醒线程,功能和wait,notify有些相似,但是LockSupport比起wait,notify功能更强大，也好用的多。

相比较使用 wait，notify 来实现等待唤醒功能至少有两个缺点：

- 1. 由上面的例子可知，wait 和 notify 都是 Object 中的方法，在调用这两个方法前必须先获得锁对象，这限制了其使用场合：只能在同步代码块中。
- 2. 另一个缺点可能上面的例子不太明显，当对象的等待队列中有多个线程时，notify只能随机选择一个线程唤醒，无法唤醒指定的线程。

参见：https://www.cnblogs.com/liang1101/p/12785496.html



## AQS

### AQS是什么？有什么用？

AQS全称
AbstractQueuedSynchronizer，即抽象的队列同步器，是一种用来构建锁和同步器的框架



#### **基于AQS构建同步器*

- ReentrantLock
- Semaphore
- CountDownLatch
- ReentrantReadWriteLock
- SynchronusQueue
- FutureTask



#### **优势**

- AQS 解决了在实现同步器时涉及的大量细节问题，例如自定义标准同步状态、FIFO 同步队列。
- 基于 AQS 来构建同步器可以带来很多好处。它不仅能够极大地减少实现工作，而且也不必处理在多个位置上发生的竞争问题。

### AQS核心知识

#### AQS核心思想

如果被请求的共享资源空闲，则将当前请求资源的线程设置为有效的工作线程，并且将共享资源设置为锁定状态。如果被请求的共享资源被占用，那么就需要一套线程阻塞等待以及被唤醒时锁分配的机制，这个机制AQS是用CLH队列锁实现的，即将暂时获取不到锁的线程加入到队列中。如图所示：

![img](https://img-blog.csdnimg.cn/img_convert/9abebc6247074cc5571dac5259322aff.png)

**Sync queue：** 同步队列，是一个双向列表。包括head节点和tail节点。head节点主要用作后续的调度。

![img](https://img-blog.csdnimg.cn/img_convert/ad0a72a0627c0daf5c31cf3d63cde7bc.png)

**Condition queue：** 非必须，单向列表。当程序中存在cindition的时候才会存在此列表。

![img](https://img-blog.csdnimg.cn/img_convert/ade8cc3617c8b5c3ac6b2da35e3fe6be.png)

#### **AQS设计思想**

- AQS使用一个int成员变量来表示同步状态
- 使用Node实现FIFO队列，可以用于构建锁或者其他同步装置
- AQS资源共享方式：独占Exclusive（排它锁模式）和共享Share（共享锁模式）



```
AQS它的所有子类中，要么实现并使用了它的独占功能的api，要么使用了共享锁的功能，而不会同时使用两套api，即便是最有名的子类ReentrantReadWriteLock也是通过两个内部类读锁和写锁分别实现了两套api来实现的
```



#### state状态

state状态使用volatile int类型的变量，表示当前同步状态。state的访问方式有三种:

- getState()
- setState()
- compareAndSetState()



#### AQS中Node常量含义

**CANCELLED**
waitStatus值为1时表示该线程节点已释放（超时、中断），已取消的节点不会再阻塞。

**SIGNAL**
waitStatus为-1时表示该线程的后续线程需要阻塞，即只要前置节点释放锁，就会通知标识为 SIGNAL 状态的后续节点的线程

**CONDITION**
waitStatus为-2时，表示该线程在condition队列中阻塞（Condition有使用）

**PROPAGATE**
waitStatus为-3时，表示该线程以及后续线程进行无条件传播（CountDownLatch中有使用）共享模式下， PROPAGATE 状态的线程处于可运行状态 

#### Condition队列

 除了同步队列之外，AQS中还存在Condition队列，这是一个单向队列。调用ConditionObject.await()方法，能够将当前线程封装成Node加入到Condition队列的末尾，然后将获取的同步状态释放（即修改同步状态的值，唤醒在同步队列中的线程）。

```
Condition队列也是FIFO。调用ConditionObject.signal()方法，能够唤醒firstWaiter节点，将其添加到同步队列末尾。 
```



#### 自定义同步器的实现

在构建自定义同步器时，只需要依赖AQS底层再实现共享资源state的获取与释放操作即可。自定义同步器实现时主要实现以下几种方法：

- isHeldExclusively()：该线程是否正在独占资源。只有用到condition才需要去实现它。
- tryAcquire(int)：独占方式。尝试获取资源，成功则返回true，失败则返回false。
- tryRelease(int)：独占方式。尝试释放资源，成功则返回true，失败则返回false。
- tryAcquireShared(int)：共享方式。尝试获取资源。负数表示失败；0表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源。
- tryReleaseShared(int)：共享方式。尝试释放资源，如果释放后允许唤醒后续等待结点返回true，否则返回false。 



### AQS实现细节

```
线程首先尝试获取锁，如果失败就将当前线程及等待状态等信息包装成一个node节点加入到FIFO队列中。 接着会不断的循环尝试获取锁，条件是当前节点为head的直接后继才会尝试。如果失败就会阻塞自己直到自己被唤醒。而当持有锁的线程释放锁的时候，会唤醒队列中的后继线程。
```



#### 独占模式下的AQS

 **所谓独占模式**，即只允许一个线程获取同步状态，当这个线程还没有释放同步状态时，其他线程是获取不了的，只能加入到同步队列，进行等待。

```
很明显，我们可以将state的初始值设为0，表示空闲。当一个线程获取到同步状态时，利用CAS操作让state加1，表示非空闲，那么其他线程就只能等待了。释放同步状态时，不需要CAS操作，因为独占模式下只有一个线程能获取到同步状态。ReentrantLock、CyclicBarrier正是基于此设计的。 
```

例如，ReentrantLock，state初始化为0，表示未锁定状态。A线程lock()时，会调用tryAcquire()独占该锁并将state+1。

![img](https://img-blog.csdnimg.cn/img_convert/568e72da9bcad9bcd41edf5bdb87e2f0.png)

 独占模式下的AQS是不响应中断的，指的是加入到同步队列中的线程，如果因为中断而被唤醒的话，不会立即返回，并且抛出InterruptedException。而是再次去判断其前驱节点是否为head节点，决定是否争抢同步状态。如果其前驱节点不是head节点或者争抢同步状态失败，那么再次挂起。 

![Linux 中的等待队列机制](https://picx1.zhimg.com/v2-2b9458d3fc1292b79fde6f1ec80f9024_720w.jpg?source=172ae18b)



##### 独占模式获取资源-acquire方法

acquire以独占exclusive方式获取资源。如果获取到资源，线程直接返回，否则进入等待队列，直到获取到资源为止，且整个过程忽略中断的影响。源码如下：

```java
 public final void acquire(int arg) {
 		if (!tryAcquire(arg) &&acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
 				selfInterrupt();
 } 
```



**流程图：**

![img](https://img-blog.csdnimg.cn/img_convert/99551e3c9965ea44bade764f9124b131.png)



- 调用自定义同步器的tryAcquire()尝试直接去获取资源，如果成功则直接返回；
- 没成功，则addWaiter()将该线程加入等待队列的尾部，并标记为独占模式；
- acquireQueued()使线程在等待队列中休息，有机会时（轮到自己，会被unpark()）会去尝试获取资源。获取到资源后才返回。如果在整个等待过程中被中断过，则返回true，否则返回false。
- 如果线程在等待过程中被中断过，它是不响应的。只是获取资源后才再进行自我中断selfInterrupt()，将中断补上。 



#####  独占模式获取资源-tryAcquire方法

tryAcquire尝试以独占的方式获取资源，如果获取成功，则直接返回true，否则直接返回false，且具体实现由自定义AQS的同步器实现的。

```java
 protected boolean tryAcquire(int arg) {
 		throw new UnsupportedOperationException();
 }
```



以下是ReentrantLock非公平模式下的代码实现，acquires=1。

```java
/**
 * Performs non-fair tryLock.  tryAcquire is implemented in
 * subclasses, but both need nonfair try for trylock method.
 */
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0) // overflow
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}
```



##### 独占模式获取资源-addWaiter方法

根据不同模式(Node.EXCLUSIVE互斥模式、Node.SHARED共享模式)创建结点并以CAS的方式将当前线程节点加入到不为空的等待队列的末尾(通过compareAndSetTail()方法)。如果队列为空，通过enq(node)方法初始化一个等待队列，并返回当前节点。



##### 独占模式获取资源-acquireQueued方法

acquireQueued用于已在队列中的线程以独占且不间断模式获取state状态，直到获取锁后返回。

主要流程：

- 结点node进入队列尾部后，检查状态，如果刚加入尾部就轮到它了，那就直接返回；

- 如果没有轮到它，调用park()进入waiting状态，等待unpark()或interrupt()唤醒；

- 被唤醒后，尝试是否获取到锁。如果获取到，head指向当前结点，并返回从入队到获取锁的整个过程中是否被中断过；如果没获取到，继续流程1

   

  ```java
  final boolean acquireQueued(final Node node, int arg) {
     	//是否已获取锁的标志，默认为true 即为尚未 
      boolean failed = true;
      try {
        	//等待中是否被中断过的标记
          boolean interrupted = false;
          for (;;) {
            	//获取前节点
              final Node p = node.predecessor();
            	//如果当前节点已经成为头结点，尝试获取锁（tryAcquire）成功，然后返回
              if (p == head && tryAcquire(arg)) {
                  setHead(node);
                  p.next = null; // help GC
                  failed = false;
                  return interrupted;
              }
            	//shouldParkAfterFailedAcquire根据对当前节点的前一个节点的状态进行判断，对当前节点做出不同的操作
            	//parkAndCheckInterrupt让线程进入等待状态，并检查当前线程是否被可以被中断
              if (shouldParkAfterFailedAcquire(p, node) &&
                  parkAndCheckInterrupt())
                  interrupted = true;
          }
      } finally {
        	//将当前节点设置为取消状态；取消状态设置为1
          if (failed)
              cancelAcquire(node);
      }
  }
  ```

#####  独占模式释放资源-release方法

release方法是独占exclusive模式下线程释放共享资源的锁。它会调用tryRelease()释放同步资源，如果全部释放了同步状态为空闲（即state=0）,当同步状态为空闲时，它会唤醒等待队列里的其他线程来获取资源。这也正是unlock()的语义，当然不仅仅只限于unlock(). 

```
public final boolean release(int arg) {
    if (tryRelease(arg)) {
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```

 

#### 共享模式下的AQS

**共享模式**，当然是允许多个线程同时获取到同步状态,共享模式下的AQS也是不响应中断的.

```
很明显，我们可以将state的初始值设为N（N > 0），表示空闲。每当一个线程获取到同步状态时，就利用CAS操作让state减1，直到减到0表示非空闲，其他线程就只能加入到同步队列，进行等待。释放同步状态时，需要CAS操作，因为共享模式下，有多个线程能获取到同步状态。CountDownLatch、Semaphore正是基于此设计的。 
```

例如，CountDownLatch，任务分为N个子线程去执行，同步状态state也初始化为N（注意N要与线程个数一致）：

![img](https://img-blog.csdnimg.cn/img_convert/68223fcc9e4aeb67bb01f0080fc0d0f0.png)

##### 共享模式获取资源-acquireShared方法

acquireShared在共享模式下线程获取共享资源的顶层入口。它会获取指定量的资源，获取成功则直接返回，获取失败则进入等待队列，直到获取到资源为止，整个过程忽略中断。

```java
public final void acquireShared(int arg) {
 		if(tryAcquireShared(arg) < 0)
 			doAcquireShared(arg);
}
```

流程：

- 先通过tryAcquireShared()尝试获取资源，成功则直接返回；
- 失败则通过doAcquireShared()中的park()进入等待队列，直到被unpark()/interrupt()并成功获取到资源才返回(整个等待过程也是忽略中断响应)。

##### 共享模式获取资源-tryAcquireShared方法

tryAcquireShared()跟独占模式获取资源方法一样实现都是由自定义同步器去实现。但AQS规范中已定义好tryAcquireShared()的返回值：

负值代表获取失败；
0代表获取成功，但没有剩余资源；
正数表示获取成功，还有剩余资源，其他线程还可以去获取。

```
 protected int tryAcquireShared(int arg) {
 		throw new UnsupportedOperationException();
 } 
```



##### 共享模式获取资源-doAcquireShared方法

doAcquireShared() 用于将当前线程加入等待队列尾部休息，直到其他线程释放资源唤醒自己，自己成功拿到相应量的资源后才返回。

```java
private void doAcquireShared(int arg) {
  	//加入队列尾部
    final Node node = addWaiter(Node.SHARED);
  	//是否成功标志
    boolean failed = true;
    try {
      	//等待过程中是否被中断过的标志
        boolean interrupted = false;
        for (;;) {
          	//获取前驱节点
            final Node p = node.predecessor();
          	//如果到head的下一个，因为head是拿到资源的线程，此时node被唤醒，很可能是head用完资源来唤醒自己的
            if (p == head) {
              	//尝试获取资源
                int r = tryAcquireShared(arg);
              
                //成功
                if (r >= 0) { 
                  	//将head指向自己，还有剩余资源可以再唤醒之后的线程
                    setHeadAndPropagate(node, r);
                    p.next = null; // help GC
                  
                  	//如果等待过程中被打断过，此时将中断补上。
                    if (interrupted)
                        selfInterrupt();
                    failed = false;
                    return;
                }
            }
          
          	//判断状态，队列寻找一个适合位置，进入waiting状态，等着被unpark()或interrupt()
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```

##### 共享模式释放资源-releaseShared方法

releaseShared()用于共享模式下线程释放共享资源，释放指定量的资源，如果成功释放且允许唤醒等待线程，它会唤醒等待队列里的其他线程来获取资源。

```java
public final boolean releaseShared(int arg) {
     //尝试释放资源
     if (tryReleaseShared(arg)) {
         //唤醒后继结点
         doReleaseShared();
         return true;
     }
     return false;
} 
```



```
独占模式下的tryRelease()在完全释放掉资源（state=0）后，才会返回true去唤醒其他线程，这主要是基于独占下可重入的考量；而共享模式下的releaseShared()则没有这种要求，共享模式实质就是控制一定量的线程并发执行，那么拥有资源的线程在释放掉部分资源时就可以唤醒后继等待结点。

https://www.cnblogs.com/waterystone/p/4920797.html 
```



##### 共享模式释放资源-doReleaseShared方法

doReleaseShared()主要用于唤醒后继节点线程,当state为正数，去获取剩余共享资源；当state=0时去获取共享资源。

## ReentrantLock

java除了使用关键字synchronized外，还可以使用ReentrantLock实现独占锁的功能。而且ReentrantLock相比synchronized而言功能更加丰富，使用起来更为灵活，也更适合复杂的并发场景

**ReentrantLock** 总共有三个内部类，并且三个内部类是紧密相关的，下面先看三个类的关系。

 ![img](https://pics4.baidu.com/feed/5bafa40f4bfbfbed425ae56c01885c3eafc31f27.png@f_auto?token=ad7fe30587314423e6f2c7c69210dd9c)



### NonfairSync

 

```
final void lock() {
    if (compareAndSetState(0, 1))
        setExclusiveOwnerThread(Thread.currentThread());
    else
        acquire(1);
}
```



### FairSync

 

```
final void lock() {
    acquire(1);
}
```



###  ReentrantLock 和 **Synchronized**的对比

![img](https://img-blog.csdnimg.cn/20201111200435712.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nemhhb3lhbmcxMjI=,size_16,color_FFFFFF,t_70)





## ReentrantReadWriteLock

很多情况下有这样一种场景：对共享资源有读和写的操作，且写操作没有读操作那么频繁。

在没有写操作的时候，多个线程同时读一个资源没有任何问题，所以应该允许多个线程同时读取共享资源，但是如果一个线程想去写这些共享资源，就不应该允许其他线程对该资源进行读和写的操作了。



### State

读写锁式通过一个 int 类型的状态，来记录读锁状态和写锁状态

![ReentrantReadWriteLock读写锁详解-冯金伟博客园](https://img.fengjinwei.com/2022/09/20220923040135181.png)



### 例子

t1 写锁， t2 是读锁， t1获得了锁

![img](http://upload-images.jianshu.io/upload_images/26273155-201c2a84d7d8fbdc.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)



此时线程owner 是t1, state 0_1 值为1

此时t2 前来尝试获得读锁

![img](http://upload-images.jianshu.io/upload_images/26273155-82e0eaefe6098fcf.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)

- t2 拿不到锁，进行排队

![img](http://upload-images.jianshu.io/upload_images/26273155-7120772dbaf15d3d.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)

- t1 释放写锁

![img](http://upload-images.jianshu.io/upload_images/26273155-c769e56e18ca6746.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)



此时t2 获得了锁，但是不是exclusive 的的， state 现在为0

- 在t1 release 时候，unpark t2, ，t2 里从park状态激活，state =1_0

![img](http://upload-images.jianshu.io/upload_images/26273155-61c6591e99679826.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)

同时 t2 进入到doAcquireShared 线程 循环 运行setHeadAndPropagate，

```java
   private void setHeadAndPropagate(Node node, int propagate) {
        Node h = head; // Record old head for check below
        setHead(node);
        if (propagate > 0 || h == null || h.waitStatus < 0 ||
            (h = head) == null || h.waitStatus < 0) {
            Node s = node.next;
            if (s == null || s.isShared())
                doReleaseShared();
        }
    }
```

再进入doReleaseShared, 在此时释放t2. t2 节点从队列去除。 t3 线程被unpark，  t3 节点成为头结点。 当它进入doAcquireShared->setHeadAndPropagate 进入不了doReleaseShared， 因为t4 不是shared 的，所以t4 线程不能被激活。在此时state 为2_0

 ![img](http://upload-images.jianshu.io/upload_images/26273155-1cd340eda3ef0954.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)

http://events.jianshu.io/p/51b1337e9a40