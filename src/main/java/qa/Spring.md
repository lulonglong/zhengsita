# 容器

1. Spring是什么
```
是一个管理对象及其依赖关系的框架。在此基础上还提供了AOP，并由此衍生出了事务管理等特性。
```

2. 怎么理解控制反转(IOC)/依赖注入(DI)
```
把复杂系统的对象通过引入第三方组件封装以后，将内部实现对外部透明，从而降低了对象间的耦合，这个组件就是Spring
对象A获得依赖对象B的过程，由主动行为变为了被动行为，”控制权“颠倒过来了，这就是“控制反转”这个名称的由来。哪些方面的控制被反转了呢？获得"依赖对象的过程"被反转了。控制被反转之后，获得依赖对象的过程由自身管理变为了由IOC容器主动注入。于是，可以给“控制反转”取一个更合适的名字叫做“依赖注入“。
所以，依赖注入(DI)和控制反转(IOC)是从不同的角度的描述的同一件事情，就是指通过引入IOC容器，利用依赖关系注入的方式，实现对象之间的解耦。
```
![alt](https://img-blog.csdnimg.cn/d737d7c75749465da206ed7c4f06c061.png)
![alt](https://img-blog.csdnimg.cn/564a2e2fff9347a6a72cbfafd622a90d.png)

3. BeanFactory和FactoryBean的区别
```
BeanFactory：根据BeanDefinition创建Bean，管理项目中被定义的Spring Bean，是一个Bean工厂
FactoryBean：仅仅是一个可以创建对象的工厂，当定义一个Bean的时候，如果类型指定的是FactoryBean，那么实际的对象实例不是FactoryBean，而是通过它的getObject方法创建的对象，如下所示，用于创建比较复杂的对象
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="configLocation" value="classpath:mybatis-config.xml"/>
</bean>
##dubbo consumer的代理对象就是通过FactoryBean创建出来的代理对象
```

4. BeanFactory和ApplicationContext有什么区别
```
BeanFactory和ApplicationContext是Spring的两大核心接口，都可以当做Spring的容器
BeanFactory是Spring里面最底层的接口，是IoC的核心，定义了IoC的基本功能，包含了各种Bean的定义、加载、实例化，依赖注入和生命周期管理。ApplicationContext接口作为BeanFactory的子类，除了提供BeanFactory所具有的功能外，还提供了更完整的框架功能。
ApplicationContext内部持有BeanFactory的实例
```

5. BeanDefinition是什么
```
bean定义信息，包括class、构造函数以及属性等
```

6. BeanPostProcessor是干嘛的，有哪些应用场景，还有哪些BeanPostProcessor的子类
```
创建bean的后置处理器，监听bean的初始化，对其做一些操作

BeanPostProcessor常用子类：
InstantiationAwareBeanPostProcessor：实例化前后的处理
MergedBeanDefinitionPostProcessor：实例化时对bd的进一步丰富，@Autowired的应用
```
 ![alt](https://image-static.segmentfault.com/347/041/3470413121-2abed99e085c254a)

7. BeanFactoryPostProcessor是干嘛的，有哪些应用场景
```
BeanFactory的后置处理器，可以对BeanFactory进行自定义的扩展，例如增加或修改BeanDefinition

BeanFactoryPostProcessor常用子类实现：
BeanDefinitionRegistryPostProcessor，spring-mybatis的mapper加载就是通过这个实现的
```

8. Aware接口有什么特性

```
Aware是感知的意思，让实现Aware的接口可以感知到某样东西
ApplicationContextAware让实现类感知并拿到ApplicationContext实例
BeanFactoryAware让实现类感知并拿到BeanFactory实例
BeanNameAware让实现类感知并拿到BeanName
```

9. 简要介绍下Spring的启动流程

```
次要流程省略，重要流程如下几步：
1.prepareRefresh，加载环境变量
2.obtainFreshBeanFactory，创建BeanFactory，加载BeanDefinition
3.prepareBeanFactory，完善BeanFactory，添加容器内置实例
4.invokeBeanFactoryPostProcessors，调用BeanFactoryPostProcessor
5.registerBeanPostProcessors，实例化BeanPostProcessor
6.finishBeanFactoryInitialization，实例化剩余的&非懒加载的单例bean
```

10. Spring Bean的生命周期

```
简单来说，Spring Bean的生命周期只有四个阶段：
实例化 Instantiation --> 属性赋值 Populate  --> 初始化 Initialization（执行初始化方法、执行BeanPostProcessor）  --> 销毁 Destruction
```
 ![alt](https://img-blog.csdnimg.cn/img_convert/84341632e9df3625a91c3e2a1437ee65.png)

11. Spring中bean的作用域

```
（1）singleton：默认作用域，单例bean，每个容器中只有一个bean的实例
（2）prototype：为每一个bean请求创建一个实例
以下跟Web相关的，选择性关注
（3）request：为每一个request请求创建一个实例，在请求完成以后，bean会失效并被垃圾回收器回收
（4）session：与request范围类似，同一个session会话共享一个实例，不同会话使用不同的实例
（5）global-session：全局作用域，所有会话共享一个实例。如果想要声明让所有会话共享的存储变量的话，那么这全局变量需要存储在global-session中
```
12. @Autowired和@Resource之间的区别

```
(1) @Autowired默认是按照类型装配注入的，默认情况下它要求依赖对象必须存在（可以设置它required属性为false）。类型存在多个实例的情况，可结合@Qualifier指定名称
(2) @Resource默认是按照名称来装配注入的，只有当找不到与名称匹配的bean才会按照类型来装配注入
```

13. 如何解析自定义标签

```
Spring在解析xml时，会读取所有Jar包下的META-INF/spring.handlers文件。我们只需要定义自己的Namespace并配置相应的NamespaceHandler解析类即可。如下所示：
http\://www.springframework.org/schema/context=org.springframework.context.config.ContextNamespaceHandler
```

14. @Component、@Service 是怎么被解析的

```
spring-context模块在META-INF/spring.handlers文件中定义了ContextNamespaceHandler来解析<context:component-scan base-package="com.xxx.crm"/>标签
其中ComponentScanBeanDefinitionParser会扫描包内的class文件，使用ASM找到有@Component注解的Class解析成BeanDefinition即可
==为什么这里要用ASM解析而不用ClassLoader加载出Class直接拿Class注解岂不是更简单？
==答：因为被扫描的类不一定会被用到，如果直接用ClassLoader加载进JVM，会加重JVM的负担
```

15. @Autowired和@Resource是怎么被解析的（以@Autowired为代表，两者是一个流程）

```
@Autowired与@Component等注解是一套配合使用的注解。支持<context:component-scan base-package="com.xxx.crm"/>默认就支持@Autowired，所以在ComponentScanBeanDefinitionParser的逻辑中注册了@Autowired的发现类AutowiredAnnotationBeanPostProcessor，此类是一个MergedBeanDefinitionPostProcessor。
在创建Bean时会解析@Autowired注解并放入bd的ExternallyManagedConfigMember中。
在填充属性时会注入属性
```
16. Spring是怎么解决循环依赖的，构造函数的循环依赖解决了吗

```
Bean的创建过程会把实例化后的Bean通过ObjectFactory预先暴露出来
在doGetBean时，会依次经过三级缓存来获取实例
	1.优先从singletonObjects缓存中获取已创建的Bean
	2.通过earlySingletonObjects获取早期实例
	3.通过singletonFactory暴露，这时候是个ObjectFactory对象，get到对象后缓存到earlySingletonObjects中（第二级缓存）
	
由于是把实例化后的Bean通过ObjectFactory预先暴露出来，所以如果是构造函数的循环依赖并未解决。A-》B-》A，因为整个依赖过程A的构造函数并未执行完，所以实例也就提前暴露不了
```

17. InitializingBean有什么作用
```
实现了InitializingBean的类在初始化完毕后会被调用afterPropertiesSet。
1.实例类可以检查属性完整性
2.也可以作为组件功能的启动入口
```

18. 列举下Spring提供的供开发者扩展的接口
```
>Aware接口
		1.可以拿到ApplicationContext、BeanFactory等

>BeanPostProcessor接口
		1.可以监听Bean的创建，在Bean的实例化过程对Bean做操作
		
>BeanFactoryPostProcessor接口
		1.Bean工厂初始化后置处理，通常用在容器初始化完毕后，需要对当前容器内的BeanDefinition做处理或者对当前容器增加BeanDefinition
		2.disconf的ReloadingPropertyPlaceholderConfigurer会扫描有占位符的bd
		3.mybatis的MapperScannerConfigurer会向容器动态加入Mapper的bd
		
>InitializingBean接口
		1.提供Bean的属性初始化完毕后的回调方法，可以在Bean实例化完成后自动启动后续流程，可能是某个组件的重要入口
		
>MergedBeanDefinitionPostProcessor接口
		1.派生自BeanPostProcessor
		2.在创建Bean时可对BeanDefinition进一步修改，@Autowired的属性发现就是利用这点

>InstantiationAwareBeanPostProcessor接口
		1.派生自BeanPostProcessor
		2.在创建Bean时可修改bean的属性
		3.在Bean的实例化前后做处理，AOP的实现就是借用此类返回了代理类
```

19. Spring的自动装配
```
我们常用的装配方式是注解或者xml中配置属性依赖
自动装配可以在配置bean时如下使用，支持byName、byType，Spring会遍历bean中的所有set方法，根据名字或者类型自动注入，此种方式不太可控，不建议使用
<bean id="people" class="com.lisi.pojo.People" autowire="byName"></bean>
<bean id="people" class="com.lisi.pojo.People" autowire="byType"></bean>
```

20. Spring的异步支持
```
1. 使用@Async注解,在xml中开启 <task:annotation-driven>
如果在@Aysnc中没有指定线程池（可以指定），会默认使用spring提供的默认线程池SimpleAsyncTaskExecutor，线程池为每个任务都单独创建一个线程，不会重用线程
2. 使用Spring内置线程池ThreadPoolTaskExecutor
```

21. Spring为什么推荐使用构造器注入
Spring 中有这么3种依赖注入的方式
* 基于 field 注入（属性注入） 
* 基于 setter 注入
* 基于 constructor 注入（构造器注入

* 基于field注入
  * 容易违背了单一职责原则 使用这种基于 field 注入的方式，添加依赖是很简单的，就算你的类中有十几个依赖你可能都觉得没有什么问题，普通的开发者很可能会无意识地给一个类添加很多的依赖。
  * 依赖注入与容器本身耦合

* 基于 setter 注入
  * 基于 setter 的注入，则只应该被用于注入非必需的依赖，同时在类中应该对这个依赖提供一个合理的默认值。如果使用 setter 注入必需的依赖，那么将会有过多的 null 检查充斥在代码中。使用 setter 注入的一个优点是，这个依赖可以很方便的被改变或者重新注入。

* 基于 constructor 注入
  * 将各个必需的依赖全部放在带有注解构造方法的参数中，并在构造方法中完成对应变量的初始化，这种方式，就是基于构造方法的注入
  * 当使用构造器方式注入，到了某个特定的点，构造器中的参数变得太多以至于很明显地发现 something is wrong。拥有太多的依赖通常意味着你的类要承担更多的责任，明显违背了单一职责原则（SRP：Single responsibility principle）。

简单来说，强制依赖就用构造器方式。
可选、可变的依赖就用 setter 注入。


# AOP
1. 什么是AOP？
```
		直译：面向切面编程。
		把代码塞到若干处执行，这散乱的若干处连起来犹如一个面，把编码关注点放到这个面上就叫面向切面编程。更通俗的解释是“写拦截器”，目的是把关注点剥离，使主体业务流程更聚焦
```
 ![alt](https://pics3.baidu.com/feed/80cb39dbb6fd5266d7c5c32d3d06e223d50736c7.png@f_auto?token=d2756a3d7daf1bc4eba3c4efba4b639a)

2. 连接点（Join point）、切点（Pointcut）、切面（Aspect）、通知（Advice）分别是什么意思？
```
JoinPoint：连接点，可以理解成拦截器可拦截的目标，在Spring中就是目标方法
Pointcut：切点，提供 ClassFilter 类过滤器和 MethodMatcher 方法匹配器支持对类和方法进行筛选。从JoinPoint中筛选出的AOP子集就是切点
Advice：增强处理(书面意思：通知)
Aspect：对切点和增强的整体定义作为一个完整的切面定义
```

3. Spring是怎么实现AOP的？
```
1.通过AopNamespaceHandler解析<aop:aspectj-autoproxy />，注册自动代理创建类AbstractAutoProxyCreator，实现了InstantiationAwareBeanPostProcessor接口
2.Bean实例化时，解析全部@Aspect注解类，提取PointCut和Advice信息
3.每个Bean实例化后，过滤Advice看是否需要对该Bean做AOP处理，如果需要则生成代理对象
```

4. Advice类型有哪些？
 ![alt](https://img-blog.csdnimg.cn/2020120700443256.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2E3NDUyMzM3MDA=,size_16,color_FFFFFF,t_70)

5. Advice的执行顺序
```
1）没有异常情况下的执行顺序：
		around before advice
		before advice
		target method 执行
		after advice
		around after advice
		afterReturning advice

（2）出现异常情况下的执行顺序：
		around before advice
		before advice
		target method 执行
		after advice
		around after advice
		afterThrowing advice
		java.lang.RuntimeException：异常发生
```
6. 在项目中有没有使用过AOP，什么场景做了什么功能？
```
   在支付项目的支付接口中用到了，用户在某些场景下可能会重复提交支付请求，为了防止出现过多的冗余支付单，我们利用AROUND类型的通知在接口请求时把参数md5后与生成的支付信息做了5分钟缓存。
```
7. AOP支持哪几种代理
```
   JDK动态代理和CGLIB动态代理
```

   

# 事务
1. Spring事务是怎么实现的？
```
1.通过TxNamespaceHandler解析<tx:annotation-driven />，注册Advisor和处理事务的方法拦截器。
2.Bean实例化时，通过AOP筛选带有@Transactional注解的类，应用Advisor生成代理实例
3.执行事务方法时生成事务信息，并与当前线程绑定
4.在方法正常执行的情况下提交事务，执行异常时回滚事务
```

2. Spring的事务传播机制？
```
spring事务的传播机制说的是，当多个事务同时存在的时候，spring如何处理这些事务的行为。事务传播机制实际上是使用简单的ThreadLocal实现的，所以，如果调用的方法是在新线程调用的，事务传播实际上是会失效的。
```
 ![alt 事务传播机制](https://asset-stage.yit.com/URDM/be0dcbd60e9c67ef1047f0614dff8770_673X909.jpeg)

3. Spring提供的事务隔离级别和数据库提供的事务隔离级别是什么关系，两者不一致时会怎样
```
Spring中每个事务都可以指定不同的隔离级别，底层利用了数据库隔离级别的支持。数据库设置的隔离级别作为默认级别，当Spring事务设置的隔离级别与数据库的隔离级别不一致时，以Spring设置的为准
```
4. @Transactional失效的情况有哪些
```
@Transactional 应用在非 public 修饰的方法上
被同一个类中的方法调用，导致 @Transactional 失效
```
5. 为什么@Transactional建议标记在方法上而不是类或者接口上

```
在Spring 事务的实现中，方法开始时就拿到连接开启事务。如果加到类上会导致没有出现数据库操作的方法也会占用数据库连接。
```

   

# Spring-Mybatis
1. 启动流程
```
1.BeanFactory创建后，MapperScannerConfigurer扫描包，查找Mapper接口,注册对应代理bd
2.创建Mapper实例时生成代理对象
```

# 高级篇

1. 说下JDK动态代理和CGLIB的实现
