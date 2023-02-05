
# Unsafe解析

## 简要介绍

​		Unsafe是位于sun.misc包下的一个类，主要提供一些用于执行低级别、不安全操作的方法，如直接访问系统内存资源、自主管理内存资源等，这些方法在提升Java运行效率、增强Java语言底层资源操作能力方面起到了很大的作用。

​		但由于Unsafe类使Java语言拥有了类似C语言指针一样操作内存空间的能力，这无疑也增加了程序发生相关指针问题的风险。在程序中过度、不正确使用Unsafe类会使得程序出错的概率变大，使得Java这种安全的语言变得不再“安全”，因此对Unsafe的使用一定要慎重。

### 单例类

​		Unsafe类为一单例实现，提供静态方法getUnsafe获取Unsafe实例，当且仅当调用getUnsafe方法的类为引导类加载器所加载时才合法，否则抛出SecurityException异常。

* 类加载器
  * 引导类加载器(Bootstrap ClassLoader), 用来加载 Java 的核心库，是用原生代码来实现的，并不继承自 java.lang.ClassLoader
  * 拓展类加载器(Extension ClassLoader), 用来加载 Java 的扩展库。Java 虚拟机的实现会提供一个扩展库目录。该类加载器在此目录里面查找并加载 Java 类
  * 应用类加载器(Application ClassLoader), 用来加载开发者自己编写的 Java 类。可以通过 ClassLoader.getSystemClassLoader()来获取它

```java
    private static final Unsafe theUnsafe = new Unsafe();

    @CallerSensitive
    public static Unsafe getUnsafe() {

        Class<?> caller = Reflection.getCallerClass();

        // 仅在引导类加载器`BootstrapClassLoader`加载时才合法
        if (!VM.isSystemDomainLoader(caller.getClassLoader()))
            throw new SecurityException("Unsafe");
        return theUnsafe;
    }

```

### 获取方式

#### 设置引导类加载
​		从getUnsafe方法的使用限制条件出发，通过Java命令行命令-Xbootclasspath/a把调用Unsafe相关方法的类A所在jar包路径追加到默认的bootstrap路径中，使得A被引导类加载器加载，从而通过Unsafe.getUnsafe方法安全的获取Unsafe实例。

java -Xbootclasspath/a: ${path}   // 其中path为调用Unsafe相关方法的类所在jar包路径

#### 反射
```java
    private static Unsafe reflectGetUnsafe() {
        try {
          Field field = Unsafe.class.getDeclaredField("theUnsafe");
          field.setAccessible(true);
          return (Unsafe) field.get(null);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          return null;
        }
    }

```

## 主要功能
![avatar](../assets/f182555953e29cec76497ebaec526fd1297846.png)

### 数组相关
* arrayBaseOffset() 返回数组中第一个元素的偏移地址
* arrayIndexScale() 返回数组中一个元素占用的大小
* AtomicIntegerArray 
  * 通过Unsafe的arrayBaseOffset、arrayIndexScale分别获取数组首元素的偏移地址base及单个元素大小因子scale。后续相关原子性操作，均依赖于这两个值进行数组中元素的定位，如下图二所示的getAndAdd方法即通过checkedByteOffset方法获取某数组元素的偏移地址，而后通过CAS实现原子性操作
  
```java

    // 获取数组元素的首地址
    private static final int base = unsafe.arrayBaseOffset(int[].class);

    static {
    
        // 获取每个元素所占的大小
        int scale = unsafe.arrayIndexScale(int[].class);
        if ((scale & (scale - 1)) != 0)
            throw new Error("data type scale not a power of two");
        shift = 31 - Integer.numberOfLeadingZeros(scale);
    }

    private long checkedByteOffset(int i) {
        if (i < 0 || i >= array.length)
            throw new IndexOutOfBoundsException("index " + i);

        return byteOffset(i);
    }


    // 通过数组元素计算偏移地址
    private static long byteOffset(int i) {
        return ((long) i << shift) + base;
    }
    
    public final int getAndDecrement(int i) {
        return getAndAdd(i, -1);
    }

    /**
     * Atomically adds the given value to the element at index {@code i}.
     * 给索引i位置上的值加delta
     */
    public final int getAndAdd(int i, int delta) {
        return unsafe.getAndAddInt(array, checkedByteOffset(i), delta);
    }

```

### 内存屏障
​		在Java 8中引入，用于定义内存屏障（也称内存栅栏，内存栅障，屏障指令等，是一类同步屏障指令，是CPU或编译器在对内存随机访问的操作中的一个同步点，使得此点之前的所有读写操作都执行后才可以开始执行此点之后的操作），避免代码重排序
* loadFence() 内存屏障，禁止load操作重排序
* storeFence() 内存屏障，禁止store操作重排序
* fullFence() 内存屏障，禁止load、store操作重排序
* StampedLock
  * StampedLock提供了一种乐观读锁的实现，这种乐观读锁类似于无锁的操作，完全不会阻塞写线程获取写锁，从而缓解读多写少时写线程“饥饿”现象。由于StampedLock提供的乐观读锁不阻塞写线程获取读锁，当线程共享变量从主内存load到线程工作内存时，会存在数据不一致问题，所以当使用StampedLock的乐观读锁时，需要遵从如下图用例中使用的模式来确保数据的一致性
  ![avatar](../assets/839ad79686d06583296f3abf1bec27e3320222.png)
  * 下图为StampedLock.validate方法的源码实现，通过锁标记与相关常量进行位运算、比较来校验锁状态，在校验逻辑之前，会通过Unsafe的loadFence方法加入一个load内存屏障，目的是避免上图用例中步骤②和StampedLock.validate中锁状态校验运算发生重排序导致锁状态校验不准确的问题。
  ![avator](../assets/256f54b037d07df53408b5eea9436b34135955.png)

### 系统相关
* addressSize() 返回系统指针的大小。返回值为4（32位系统）或 8（64位系统）
* pageSize() 内存页的大小，值为2的幂次方。
* java.nio下的工具类Bits中计算待申请内存所需内存页数量的静态方法，其依赖于Unsafe中pageSize方法获取系统内存页大小实现后续计算逻辑

### 线程调度
* park() 阻塞线程
* unpark() 取消阻塞线程
  * LockSupport的park、unpark方法实际是调用Unsafe的park、unpark方式来实现
* monitorEnter() 获得对象锁（可重入锁）
* monitorExit() 释放对象锁
* tryMonitorEnter() 尝试获取对象锁
  * 已经被标记为deprecated

### 内存操作
  这部分主要包含堆外内存的分配、拷贝、释放、给定地址值操作等方法。

* allocateMemory() 分配内存, 相当于C++的malloc函数
* reallocateMemory() 扩充内存
* freeMemory() 释放内存
* setMemory() 在给定的内存块中设置值
* copyMemory() 内存拷贝
* getObject() 取给定地址值，忽略修饰限定符的访问限制
  * getInt()
  * getChar()
  * getLong()
* putObject() 为给定地址设置值，忽略修饰限定符的访问限制
  * putInt()
  * putChar()
  * putLong()

#### 堆外内存
* 对垃圾回收停顿的改善。由于堆外内存是直接受操作系统管理而不是JVM，所以当我们使用堆外内存时，即可保持较小的堆内内存规模。从而在GC时减少回收停顿对于应用的影响。
* 提升程序I/O操作的性能。通常在I/O通信过程中，会存在堆内内存到堆外内存的数据拷贝操作，对于需要频繁进行内存间数据拷贝且生命周期较短的暂存数据，都建议存储到堆外内存。

##### DirectByteBuffer
​		DirectByteBuffer是Java用于实现堆外内存的一个重要类，通常用在通信过程中做缓冲池，如在Netty、MINA等NIO框架中应用广泛。DirectByteBuffer对于堆外内存的创建、使用、销毁等逻辑均由Unsafe提供的堆外内存API来实现。

![avatar](../assets/5eb082d2e4baf2d993ce75747fc35de6486751.png)

### CAS
​		比较并替换，实现并发算法时常用到的一种技术。
​		CAS操作包含三个操作数——内存位置、预期原值及新值。执行CAS操作的时候，将内存位置的值与预期原值比较，如果相匹配，那么处理器会自动将该位置值更新为新值，否则，处理器不做任何操作。 我们都知道，CAS是一条CPU的原子指令（cmpxchg指令），不会造成所谓的数据不一致问题。 Unsafe提供的CAS方法（如compareAndSwapXXX）底层实现即为CPU指令cmpxchg

* compareAndSwapObject(Object o, long offset, Object expected, Object x)
  * o 包含要修改field的对象
  * offset 对象中某field的偏移量
  * expected 期望值
  * x 更新值
  
* AtomicInteger
```java
    // 给属性加delta值
    public final int getAndAddInt(Object o, long offset, int delta) {
        int v;
        do {
        		// 先执行do，获取旧值
            v = getIntVolatile(o, offset);
        } while (!compareAndSwapInt(o, offset, v, v + delta));
        return v;
    }

```

### Class相关
此部分主要提供Class和它的静态字段的操作相关方法。

* staticFieldOffset(Field f) 
  * 获取给定静态字段的内存地址偏移量，这个值对于给定的字段是唯一且固定不变的
* staticFieldBase(Field f) 
  * 获取一个静态类中给定字段的对象指针
* shouldBeInitialized(Class<?> c) 
  * 判断是否需要初始化一个类，通常在获取一个类的静态属性的时候（因为一个类如果没初始化，它的静态属性也不会初始化）使用。 当且仅当ensureClassInitialized方法不生效时返回false。
* ensureClassInitialized(Class<?> c)
  * 检测给定的类是否已经初始化。通常在获取一个类的静态属性的时候（因为一个类如果没初始化，它的静态属性也不会初始化）使用
* defineClass(String name, byte[] b, int off, int len, ClassLoader loader, ProtectionDomain protectionDomain);
  * 定义一个类，此方法会跳过JVM的所有安全检查，默认情况下，ClassLoader（类加载器）和ProtectionDomain（保护域）实例来源于调用者
* defineAnonymousClass(Class<?> hostClass, byte[] data, Object[] cpPatches)
  * 定义一个匿名类
  
#### 主要用来实现 Lambda 表达式
从Java 8开始，JDK使用invokedynamic及VM Anonymous Class结合来实现Java语言层面上的Lambda表达式。

* invokedynamic： invokedynamic是Java 7为了实现在JVM上运行动态语言而引入的一条新的虚拟机指令，它可以实现在运行期动态解析出调用点限定符所引用的方法，然后再执行该方法，invokedynamic指令的分派逻辑是由用户设定的引导方法决定。
* VM Anonymous Class：可以看做是一种模板机制，针对于程序动态生成很多结构相同、仅若干常量不同的类时，可以先创建包含常量占位符的模板类，而后通过Unsafe.defineAnonymousClass方法定义具体类时填充模板的占位符生成具体的匿名类。生成的匿名类不显式挂在任何ClassLoader下面，只要当该类没有存在的实例对象、且没有强引用来引用该类的Class对象时，该类就会被GC回收。故而VM Anonymous Class相比于Java语言层面的匿名内部类无需通过ClassClassLoader进行类加载且更易回收。
在Lambda表达式实现中，通过invokedynamic指令调用引导方法生成调用点，在此过程中，会通过ASM动态生成字节码，而后利用Unsafe的defineAnonymousClass方法定义实现相应的函数式接口的匿名类，然后再实例化此匿名类，并返回与此匿名类中函数式方法的方法句柄关联的调用点；而后可以通过此调用点实现调用相应Lambda表达式定义逻辑的功能。下面以如下图所示的Test类来举例说明。

![avator](../assets/7707d035eb5f04314b3684ff91dddb1663516.png)

​		Test类编译后的class文件反编译后的结果如下图一所示（删除了对本文说明无意义的部分），我们可以从中看到main方法的指令实现、invokedynamic指令调用的引导方法BootstrapMethods、及静态方法lambda$main$0（实现了Lambda表达式中字符串打印逻辑）等。在引导方法执行过程中，会通过Unsafe.defineAnonymousClass生成如下图二所示的实现Consumer接口的匿名类。其中，accept方法通过调用Test类中的静态方法lambda$main$0来实现Lambda表达式中定义的逻辑。而后执行语句consumer.accept（"lambda"）其实就是调用下图二所示的匿名类的accept方法。

![avator](../assets/1038d53959701093db6c655e4a342e30456249.png)

### 对象操作
此部分主要包含对象成员属性相关操作及非常规的对象实例化方式等相关方法

* objectFieldOffset(Field f)
  * 返回对象成员属性在内存地址相对于此对象的内存地址的偏移量
* getObject(Object o, long offset)
  * 获得给定对象的指定地址偏移量的值，与此类似操作还有：getInt，getDouble，getLong，getChar等
* putObject(Object o, long offset, Object x)
  * 给定对象的指定地址偏移量设值，与此类似操作还有：putInt，putDouble，putLong，putChar等
* getObjectVolatile(Object o, long offset)
  * 从对象的指定偏移量处获取变量的引用，使用volatile的加载语义
* putObjectVolatile(Object o, long offset, Object x)
  * 存储变量的引用到对象的指定的偏移量处，使用volatile的存储语义
* putOrderedObject(Object o, long offset, Object x)
  * 有序、延迟版本的putObjectVolatile方法，不保证值的改变被其他线程立即看到。只有在field被volatile修饰符修饰时有效
* allocateInstance(Class<?> cls) throws InstantiationException
  * 绕过构造方法、初始化代码来创建对象
  * Unsafe中提供allocateInstance方法，仅通过Class对象就可以创建此类的实例对象，而且不需要调用其构造函数、初始化代码、JVM安全检查等。它抑制修饰符检测，也就是即使构造器是private修饰的也能通过此方法实例化，只需提类对象即可创建相应的对象。由于这种特性，allocateInstance在java.lang.invoke、Objenesis（提供绕过类构造器的对象生成方式）、Gson（反序列化时用到）中都有相应的应用
  * 在Gson反序列化时，如果类有默认构造函数，则通过反射调用默认构造函数创建实例，否则通过UnsafeAllocator来实现对象实例的构造，UnsafeAllocator通过调用Unsafe的allocateInstance实现对象的实例化，保证在目标类无默认构造函数时，反序列化不够影响
  * ![avatar](../assets/b9fe6ab772d03f30cd48009920d56948514676.png)

## 参考文档
[Java魔法类：Unsafe应用解析](https://tech.meituan.com/2019/02/14/talk-about-java-magic-class-unsafe.html)
[openjdk-mirror/jdk](https://github.com/openjdk-mirror/jdk/tree/jdk8u/jdk8u/master)

