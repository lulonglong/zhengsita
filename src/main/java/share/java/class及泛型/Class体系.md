# Class及泛型

## .Class文件结构
```
.Class文件是一个字节码文件，无论一个类之前是什么模样，被编译成class文件过后，都会严格按照下图的格式顺序存储
u4代表4个字节，其他数字同理
od -tx1 xxx.class可以用16进制来显示文件内容
结合javap -verbose xxx.class可以反编译出类的大概信息以及方法指令
classpy工具来看会更加清晰，结合了以上两者的功能
```
 ![alt class存储结构](https://mmbiz.qpic.cn/mmbiz_jpg/1YLJUhm0Ztlia1yiaoicvZ5FxRd2oNtEkyrNBSwF6unicupbltUMA4oVFpyoXeBEPcE7vp0Bye5hmSicga7wl3LnR9A/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
```
常量池类型及数据结构
```
 ![alt ](http://mmbiz.qpic.cn/mmbiz_png/eZzl4LXykQx0tbIzgmPJIaQGsuCk4Iz5wSnGDqf0mYS6qVq1wdyTxPG4BubSogDIGzSWibFXM7ZhBdgePKOXR0A/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

```
访问标志
```
 ![alt](https://mmbiz.qpic.cn/mmbiz_jpg/1YLJUhm0Ztlia1yiaoicvZ5FxRd2oNtEkyrPGF2n68gMoXuqGX66cJDkA3B4R5iam2bKFGicxfCZdsibgHGpd71sebMA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)


## 泛型擦除
```
泛型思想早在C++语言的模板（Templates）中就开始生根发芽，在Java语言还没有出现泛型的版本时，只能通过Object来实现类型泛化。例如在哈希表的存取中，JDK 1.5之前使用HashMap的get()方法，返回值就是一个Object对象，由于Java语言里面所有的类型都继承于java.lang.Object，那Object转型成任何对象都是有可能的。但是也因为有无限的可能性，就只有程序员和运行期的虚拟机才知道这个Object到底是个什么类型的对象。在编译期间，编译器无法检查这个Object的强制转型是否成功，如果仅仅依赖程序员去保障这项操作的正确性，许多ClassCastException的风险就会被转嫁到程序运行期之中。 
　　C#里面泛型无论在程序源码中、编译后的IL中（Intermediate Language，中间语言，这时候泛型是一个占位符）或是运行期的CLR中都是切实存在的，List<int>与 List<String>就是两个不同的类型，它们在系统运行期生成，有自己的虚方法表和类型数据，这种实现称为类型膨胀，基于这种方法实现的泛型被称为真实泛型。 
　　Java语言中的泛型则不一样，它只在程序源码中存在，在编译后的字节码文件中，就已经被替换为原来的原生类型（Raw Type，也称为裸类型，也即没有了后面的参数）了，并且在相应的地方插入了强制转型代码，因此对于运行期的Java语言来说，ArrayList<int>与ArrayList<String>就是同一个类。所以说泛型技术实际上是Java语言的一颗语法糖，基于这种方法实现的泛型被称为伪泛型。
```
```
无限制类型擦除，直接擦成Object
```
![alt](https://mmbiz.qpic.cn/mmbiz_png/zpom4BeZSicbOer7sORfP56oLOmr1sTRrC1EBISHPgs4EqVEv4zgGHqPRC9ICkq41LuxsPFGiaojicMVnfKW4t8Sg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)
```
有限制类型擦除，擦到限制类型
```
![alt](https://mmbiz.qpic.cn/mmbiz_png/zpom4BeZSicbOer7sORfP56oLOmr1sTRrndAcCABVojOfiaVwfqjwV8Gtdmgcg275ryX8aYk8Tl3cgibxPAogBk6g/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)
```
当存在接口实现时的特殊情况，编译器会通过添加一个桥接方法来满足语法上的要求
```
```java
public interface Fruit<T> {
    T get(T param);
}

public class Apple implements Fruit<Integer> {
    @Override
    public Integer get(Integer param) {
        return param;
    }
}

//*************理论上泛型擦除后会是这样*******************
public interface Fruit {
    Object get(Object param);
}

public class Apple implements Fruit {
    @Override
    public Integer get(Integer param) {
        return param;
    }
}

```
 ![alt](https://mmbiz.qpic.cn/mmbiz_png/zpom4BeZSicbOer7sORfP56oLOmr1sTRrp2FgxcIsdBiaUq6icTKq4tyA9a4tTmNEvB2l7UjwLsLibXxhDiapA3n4fQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)




## Type类型
```
JDK5之前的版本，Class的实例可以描述所有运行中的Java类型
JDK5中引入泛型后，增加了Type体系来实现泛型，顺便让Class也实现了Type。这样一来Type成了所有类型的最顶层接口
Method的返回值以及参数，Field的类型，除了可获取Class类型外，还统一增加了相关方法来获取Type
```
![alt type继承结构](https://asset-i7.yit.com/URDM/2fdc847427d5a666fbf7e62bf1e9c637_2692X982.png)
```java
/**
 * 泛型类，参数为T 和 V
 */
public class TypeTest<T, V extends  Number & Serializable> {
    private Number number;

    //类型变量 TypeVariable
    public T t;
    public V v;

    //参数类型 ParameterizedType
    public List<T> list = new ArrayList<>();
    public Map<String, T> map = new HashMap<>();

    //泛型数组类型 GenericArrayType
    public T[] tArray;
    public List<T>[] listTArray;

    //参数类型-》通配符类型 WildcardType
    public Map<? super String, ? extends Number> mapWithWildcard;

    public TypeTest testClass;
    public TypeTest<T, Integer> testClass2;

    //泛型构造函数,泛型参数为X
    public <X extends Number> TypeTest(X x, T t) {
        number = x;
        this.t = t;
    }

    //泛型方法，泛型参数为Y
    public <Y extends T> void method(Y y) {
        t = y;
    }
}

//具体运用参照Demo
```



QA：为什么说在生成Class文件时已经发生了泛型擦除，但反编译后依然能看到T等泛型信息？

<p hidden>在Class文件中保存了泛型相关声明信息，在code指令环节进行了擦除</p>

## 参考文档
[你管这破玩意叫 class？](https://mp.weixin.qq.com/s?src=11&timestamp=1646894650&ver=3667&signature=FfWuoQibKKRoAGL2Hc-ief9rvVDW-wPXIzQMn2*vgon8HQHTWEynnIl*K9nfFFSZROQzcVKNqe-vo8YvRfW0QBaVJkfUTzgPgNYZQm7nGlHODLxPA2yHc4jbhrnMiTan&new=1)
[Java中的Type详解](https://mp.weixin.qq.com/s?src=3&timestamp=1646903148&ver=1&signature=b1BrTvreiCRq8cd5MvCp4rtefuzjbTgTPSi6yhsoUR7CBBv-jmxlFRkWpmrVJ-OsgvmZAai2X6hUvlQc8thX5LDuW4xzhxcf-Pd27gnIPu8xMTgS0GGFpEmf03vSyWzZDLt-aJqtFt8hZXP6sjHtng6pnGOv2IHQ6VFlJKu7tWM=)
[聊聊Java泛型擦除那些事](https://zhuanlan.zhihu.com/p/64583822)
[秒懂Java类型（Type）系统](https://blog.csdn.net/ShuSheng0007/article/details/89520530)
[Class文件的结构是怎样的？](https://mp.weixin.qq.com/s?src=11&timestamp=1647936870&ver=3691&signature=ZQzt-xQZKEh3sKnykMzbznb6f*0GxpWjn1fxBgBVDaEzORk5sNZE36dUuWYZLKPNcmuhcFGHE64WWFKqSXbUqZyL9YFTJyg6Nv*kVrFcSUBoKm3dkUA9NwvBwqNXNrw2&new=1)
[一文让你明白 Java 字节码](https://mp.weixin.qq.com/s?src=3&timestamp=1647939690&ver=1&signature=EL6S1xrXesIvsCmRTPSbAQ47E2JuYkfP*68mqy-wDud1TQhQYCa9Qxl7yquMx0Aej36hc*I74S5sx37QkYLelFbFFgal-SeADlC-WE*2cg6b9D*2YooFcGe-S1rXu2XUYJa8bEF2LZybNBBkl7cw8EMq-LGvRcpOsLZJbcBRN70=)
[java字节码指令集](https://www.cnblogs.com/vinozly/p/5399308.html)
