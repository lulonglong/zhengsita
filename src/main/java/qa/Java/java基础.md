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







