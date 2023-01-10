# Mybatis

## JDBC 有几个步骤
JDBC 大致可以分为六个步骤：
* 加载驱动程序
* 获得数据库连接
* 创建一个 Statement 对象
* 操作数据库，实现增删改查
* 获取结果集
* 关闭资源

## 什么是 Mybatis
MyBatis 是一款优秀的持久层框架，它支持自定义 SQL、存储过程以及高级映射。
MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。
MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和Java对象为数据库中的记录。

## Mybatis 的优缺点
优点
* 基于 SQL 语句编程，相当灵活，不会对应用程序或者数据库的现有设计造成任何影响，SQL 写在 XML 里，解除 sql 与程序代码的耦合，便于统一管理；提供 XML 标签，支持编写动态 SQL 语句，并可重用。
* 与 JDBC 相比，减少了 50%以上的代码量，消除了 JDBC 大量冗余的代码，不需要手动开关连接；
* 很好的与各种数据库兼容（因为 MyBatis 使用 JDBC 来连接数据库，所以只要 JDBC 支持的数据库 MyBatis 都支持）。
* 能够与 Spring 很好的集成；
* 提供映射标签，支持对象与数据库的 ORM 字段关系映射；提供对象关系映射标签，支持对象关系组件维护。

缺点
* SQL 语句的编写工作量较大，尤其当字段多、关联表多时，对开发人员编写 SQL 语句的功底有一定要求。
* SQL 语句依赖于数据库，导致数据库移植性差，不能随意更换数据库。

## 为什么说 Mybatis 是半自动 ORM 映射工具

ORM: Object Relational Mapping 对象关系映射

Hibernate 属于全自动 ORM 映射工具，使用 Hibernate 查询关联对象或者关联集合对象时，可以根据对象关系模型直接获取，所以它是全自动的。

而 Mybatis 在查询关联对象或关联集合对象时，需要手动编写 sql 来完成，所以，称之为半自动 ORM 映射工具。

## Mybatis 是如何进行分页
* Mybatis 使用 RowBounds 对象进行分页，它是针对 ResultSet 结果集执行的内存分页，而非物理分页，先把数据都查出来，然后再做分页。
* 也可以在sql 内直接书写带有物理分页的参数来完成物理分页功能，也可以使用分页插件来完成物理分页

## #{}和${}的区别
* #{}是占位符，预编译处理，可以防止SQL注入；${}是拼接符，字符串替换，没有预编译处理，不能防止SQL注入
* Mybatis在处理#{}时，#{}传入参数是以字符串传入，会将SQL中的#{}替换为?号，调用PreparedStatement的set方法来赋值；Mybatis在处理${}时，是原值传入，就是把${}替换成变量的值，相当于JDBC中的Statement编译

## 如何获取生成的主键
* 支持主键自增的数据库，加 keyProperty 属性即可
* 不支持主键自增的数据库，可以使用＜selectKey＞标签来获取主键的值，这种方式不仅适用于不提供主键自增功能的数据库，也适用于提供主键自增功能的数据库＜selectKey＞
```
<insert id="insertUser" >
    <selectKey keyColumn="id" resultType="long" keyProperty="userId" order="BEFORE">
    SELECT USER_ID.nextval as id from dual
    </selectKey>
    insert into user(
    user_id,user_name, user_password, create_time)
    values(#{userId},#{userName}, #{userPassword} , #{createTime, jdbcType= TIMESTAMP})
</insert>
```

## MyBatis使用过程？生命周期？
MyBatis基本使用的过程大概可以分为这么几步：

![mybatis-life](https://img-stage.yit.com/CMSRESQN/c2b0d8b8a56db511609580d7cb5b93a2_1284X980.png)

* 创建SqlSessionFactory
```
String resource = "org/mybatis/example/mybatis-config.xml";
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
```

* 通过SqlSessionFactory创建SqlSession
```
SqlSession（会话）可以理解为程序和数据库之间的桥梁

SqlSession session = sqlSessionFactory.openSession();

SqlSession openSession(boolean autoCommit);
```

* 通过SqlSession执行数据库操作
```
可以通过 SqlSession 实例来直接执行已映射的 SQL 语句：
Blog blog = (Blog)session.selectOne("org.mybatis.example.BlogMapper.selectBlog", 101);

更常用的方式是先获取Mapper(映射)，然后再执行SQL语句：
BlogMapper mapper = session.getMapper(BlogMapper.class);
Blog blog = mapper.selectBlog(101);

```

* 调用session.commit()提交事务
  如果是更新、删除语句，我们还需要提交一下事务。

* 调用session.close()关闭会话

## 在mapper中如何传递多个参数
* 顺序传参法 #{0}, #{1}
* @Param注解传参法
* Map传参法
* Java Bean传参法

## MyBatis支持动态SQL吗
MyBatis中有一些支持动态SQL的标签，它们的原理是使用OGNL从SQL参数对象中计算表达式的值，根据表达式的值动态拼接SQL，以此来完成动态SQL的功能。
<if>
<where>
<set>
<choose>
<trim>
<foreach>
等等

## Mybatis的一级、二级缓存

### 一级缓存

基于 PerpetualCache 的 HashMap 本地缓存，其存储作用域为SqlSession，各个SqlSession之间的缓存相互隔离，当 Session flush 或 close 之后，该 SqlSession 中的所有 Cache 就将清空，MyBatis默认打开一级缓存。

缓存有两个级别，SqlSession和Statement。默认是SqlSession。配置成Statement，可以认为关闭一级缓存。

![redis first cache](https://img-stage.yit.com/CMSRESQN/5007e13190a67aae9ee654e4d679be52_1366X592.png)

### 二级缓存
* 二级缓存与一级缓存其机制相同，默认也是采用 PerpetualCache，HashMap 存储，不同之处在于其存储作用域为 Mapper(Namespace)，可以在多个SqlSession之间共享
* 可自定义存储源，需实现org.apache.ibatis.cache.Cache接口。
* 默认不打开二级缓存，要开启二级缓存，使用二级缓存属性类需要实现Serializable序列化接口(可用来保存对象的状态),可在它的映射文件中配置。
* https://pdai.tech/md/framework/orm-mybatis/mybatis-y-cache-level2.html
* <setting name="cacheEnabled"value="true"/>

## 工作流程
![mybatis-flow](https://img-stage.yit.com/CMSRESQN/5d6ead62ec6c18c288f42ce9178d49e2_1474X1372.png)

1、读取 MyBatis 配置文件——mybatis-config.xml 、加载映射文件——映射文件即 SQL 映射文件，文件中配置了操作数据库的 SQL 语句。最后生成一个配置对象。

2、构造会话工厂：通过 MyBatis 的环境等配置信息构建会话工厂 SqlSessionFactory。

3、创建会话对象：由会话工厂创建 SqlSession 对象，该对象中包含了执行 SQL 语句的所有方法。

4、Executor 执行器：MyBatis 底层定义了一个 Executor 接口来操作数据库，它将根据 SqlSession 传递的参数动态地生成需要执行的 SQL 语句，同时负责查询缓存的维护。

5、StatementHandler：数据库会话器，串联起参数映射的处理和运行结果映射的处理。

6、参数处理：对输入参数的类型进行处理，并预编译。

7、结果处理：对返回结果的类型进行处理，根据对象映射规则，返回相应的对象

## MyBatis的功能架构是什么样的
![mybatis-struct](https://img-stage.yit.com/CMSRESQN/e263ee30e3826cdbf3c7c5e5baf277c4_1320X916.png)

我们一般把Mybatis的功能架构分为三层：
* API接口层：提供给外部使用的接口API，开发人员通过这些本地API来操纵数据库。接口层一接收到调用请求就会调用数据处理层来完成具体的数据处理。
* 数据处理层：负责具体的SQL查找、SQL解析、SQL执行和执行结果映射处理等。它主要的目的是根据调用的请求完成一次数据库操作。
* 基础支撑层：负责最基础的功能支撑，包括连接管理、事务管理、配置加载和缓存处理，这些都是共用的东西，将他们抽取出来作为最基础的组件。为上层的数据处理层提供最基础的支撑。

## 为什么Mapper接口不需要实现类
四个字回答：动态代理，我们来看一下获取Mapper的过程：

## 获取Mapper
我们都知道定义的Mapper接口是没有实现类的，Mapper映射其实是通过动态代理实现的。
```
BlogMapper mapper = session.getMapper(BlogMapper.class);
```

![mybatis-mapper](https://img-stage.yit.com/CMSRESQN/d38b7809130fa4097128ab4afc471b60_1350X1090.png)

获取Mapper的过程，需要先获取MapperProxyFactory——Mapper代理工厂。
```
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory)this.knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
        } else {
            try {
                return mapperProxyFactory.newInstance(sqlSession);
            } catch (Exception var5) {
                throw new BindingException("Error getting mapper instance. Cause: " + var5, var5);
            }
        }
    }
    
    public class MapperProxyFactory<T> {
        private final Class<T> mapperInterface;
        ……
        protected T newInstance(MapperProxy<T> mapperProxy) {
            return Proxy.newProxyInstance(this.mapperInterface.getClassLoader(), new Class[]{this.mapperInterface}, mapperProxy);
        }
    
        public T newInstance(SqlSession sqlSession) {
            MapperProxy<T> mapperProxy = new MapperProxy(sqlSession, this.mapperInterface, this.methodCache);
            return this.newInstance(mapperProxy);
        }
    }
```

这里可以看到动态代理对接口的绑定，它的作用就是生成动态代理对象（占位），而代理的方法被放到了MapperProxy中。

MapperProxy里，通常会生成一个MapperMethod对象，它是通过cachedMapperMethod方法对其进行初始化的，然后执行execute方法。

```
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
          if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
          } else if (isDefaultMethod(method)) {
            return invokeDefaultMethod(proxy, method, args);
          }
        } catch (Throwable t) {
          throw ExceptionUtil.unwrapThrowable(t);
        }
        final MapperMethod mapperMethod = cachedMapperMethod(method);
        return mapperMethod.execute(sqlSession, args);
  }
```

### MapperMethod
MapperMethod里的execute方法，会真正去执行sql。这里用到了命令模式，其实绕一圈，最终它还是通过SqlSession的实例去运行对象的sql。

```
    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result;
        Object param;
        ……
        case SELECT:
            if (this.method.returnsVoid() && this.method.hasResultHandler()) {
                this.executeWithResultHandler(sqlSession, args);
                result = null;
            } else if (this.method.returnsMany()) {
                result = this.executeForMany(sqlSession, args);
            } else if (this.method.returnsMap()) {
                result = this.executeForMap(sqlSession, args);
            } else if (this.method.returnsCursor()) {
                result = this.executeForCursor(sqlSession, args);
            } else {
                param = this.method.convertArgsToSqlCommandParam(args);
                result = sqlSession.selectOne(this.command.getName(), param);
                if (this.method.returnsOptional() && (result == null || !this.method.getReturnType().equals(result.getClass()))) {
                    result = Optional.ofNullable(result);
                }
            }
            break;
           ……
    }

```

## Mybatis都有哪些Executor执行器
Mybatis有三种基本的Executor执行器，SimpleExecutor、ReuseExecutor、BatchExecutor。

* SimpleExecutor：每执行一次update或select，就开启一个Statement对象，用完立刻关闭Statement对象。
* ReuseExecutor：执行update或select，以sql作为key查找Statement对象，存在就使用，不存在就创建，用完后，不关闭Statement对象，而是放置于Map<String, Statement>内，供下一次使用。简言之，就是重复使用Statement对象。
* BatchExecutor：执行update（没有select，JDBC批处理不支持select），将所有sql都添加到批处理中（addBatch()），等待统一执行（executeBatch()），它缓存了多个Statement对象，每个Statement对象都是addBatch()完毕后，等待逐一执行executeBatch()批处理。与JDBC批处理相同。

作用范围：Executor的这些特点，都严格限制在SqlSession生命周期范围内。

## Mybatis的插件运行原理
Mybatis会话的运行需要ParameterHandler、ResultSetHandler、StatementHandler、Executor这四大对象的配合，插件的原理就是在这四大对象调度的时候，插入一些我我们自己的代码。

![mybatis-plugin](https://img-stage.yit.com/CMSRESQN/8e9d08dcbe12fd6a059c7ef2fff0b549_1584X954.png)

Mybatis使用JDK的动态代理，为目标对象生成代理对象。它提供了一个工具类Plugin，实现了InvocationHandler接口。在invoke方法中，执行拦截链的拦截方法。

主线脉络：解析plugin标签 -> 创建代理对象 -> 拦截方法
* 解析plugin标签时，读取interceptor属性值，创建拦截器对象，并添加拦截器链； —— 创建会话工厂过程
* 创建Executor实例对象，然后调用拦截器链的pluginAll方法，执行拦截器的plugin方法，最终使用JDK动态代理，生成执行器的代理对象；—— 创建会话
* 代理对象执行目标方法时，被Plugin#invoke拦截，最终执行拦截器的intercept方法

### 实现原理
从配置文件解析开始分析，我们知道创建会话工厂时，会使用XMLConfigBuilder#parseConfiguration方法解析每个标签，刚刚注册插件时，使用的标签是plugins，我们跟踪下这个标签的解析。

```
private void pluginElement(XNode parent) throws Exception {
    if (parent != null) {
        // 遍历 <plugins /> 的子标签
        for (XNode child : parent.getChildren()) {
            String interceptor = child.getStringAttribute("interceptor");
            Properties properties = child.getChildrenAsProperties();
            // 创建 Interceptor 对象，并设置属性
            Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
            interceptorInstance.setProperties(properties);
            // 添加到拦截器链
            configuration.addInterceptor(interceptorInstance);
        }
    }
}

```

上面的代码主要是解析配置文件的plugin节点，根据配置的interceptor属性实例化Interceptor对象，然后把对象添加到configuration的InterceptorChain

在创建SqlSession时，需要先实例化Executor对象，调用configuration#newExecutor方法，该方法先通过构造方法创建Executor对象，接着调用拦截器链的pluginAll方法，估计此时返回的是执行器代理对象。

```
 private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
    Transaction tx = null;
    try {
      final Environment environment = configuration.getEnvironment();
      final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
      tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
      final Executor executor = configuration.newExecutor(tx, execType);
      return new DefaultSqlSession(configuration, executor, autoCommit);
    } catch (Exception e) {
      closeTransaction(tx); // may have fetched a connection so lets call close()
      throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
    } finally {
      ErrorContext.instance().reset();
    }
  }
  
  public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
    executorType = executorType == null ? defaultExecutorType : executorType;
    executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
    Executor executor;
    if (ExecutorType.BATCH == executorType) {
      executor = new BatchExecutor(this, transaction);
    } else if (ExecutorType.REUSE == executorType) {
      executor = new ReuseExecutor(this, transaction);
    } else {
      executor = new SimpleExecutor(this, transaction);
    }
    if (cacheEnabled) {
      executor = new CachingExecutor(executor);
    }
    
    // 调用拦截器链的pluginAll方法，目标对象插入拦截器
    executor = (Executor) interceptorChain.pluginAll(executor);
    return executor;
  }

```

使用Plugin生成代理对象，代理对象在调用方法的时候，就会进入invoke方法，在invoke方法中，如果存在签名的拦截方法，插件的intercept方法就会在这里被我们调用，然后就返回结果。如果不存在签名方法，那么将直接反射调用我们要执行的方法。

```
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      Set<Method> methods = signatureMap.get(method.getDeclaringClass());
      if (methods != null && methods.contains(method)) {
        return interceptor.intercept(new Invocation(target, method, args));
      }
      return method.invoke(target, args);
    } catch (Exception e) {
      throw ExceptionUtil.unwrapThrowable(e);
    }
  }
```

### 实现一个插件
```
@Intercepts({
		// 可定义多个@Signature对多个接口拦截
		@Signature(
				// 拦截哪个接口
				type = Executor.class,

				// 拦截接口内的什么方法
				method = "query",

				// 拦截接口的方法入参，要按照参数顺序写正确，如果拦截方法重载，是通过方法加参数确定哪个接口的
				args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
		)
})
public class ExamplePlugin implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		long start = System.currentTimeMillis();

		Object proceed = invocation.proceed();

		long end = System.currentTimeMillis();
		System.out.println("耗时:" + (end - start));
		return proceed;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}
}
```

## MyBatis事务管理机制
* Transaction:
  * Mybatis 的事务接口
    * 获取Connection链接，提交事务，关闭事务，回滚

  * JdbcTransaction，单独使用Mybatis时，默认的事务管理实现类。
    * autoCommit：true，自动提交事务，每次执行dml时，都会提交
    * autoCommit：false, 手动提交事务，sql操作完成后，需手动执行sqlSession.commit()

  * ManagedTransaction，含义为托管事务，空壳事务管理器，皮包公司。
    * 仅是提醒用户，在其它环境中应用时，把事务托管给其它框架，比如托管给Spring，让Spring去管理事务

  * SpringManagedTransaction
    * org.mybatis.spring.transaction.SpringManagedTransaction，Spring中Mybatis管理事务的实现类
```
  @Override
  public void commit(boolean required) throws SQLException {
    if (closed) {
    throw new ExecutorException("Cannot commit, transaction is already closed");
    }
    clearLocalCache();
    flushStatements();
    if (required) {
    transaction.commit();
    }
  }

```

## 和Spring融合
* 引入mybatis-spring pom
* 配置org.mybatis.spring.SqlSessionFactoryBean
  * 需指定 dataSource 和 configLocation(mybatis-xml的路径)
  * 用来获取 SqlSessionFactory
  * 如配置 mapperLocations 参数，则会加载mapper.xml，解析出MapperStatement并放入到配置中
* MapperScannerConfigure
  * 扫描basePackage指定路径下的文件，创建mapper接口的代理类并注入到 spring bean的容器中
  * 同时会检查是否已加载mapper.xml到配置中，若无加载，则会通过org.mybatis.spring.mapper.MapperFactoryBean.checkDaoConfig尝试加载

## 数据源与连接池
https://pdai.tech/md/framework/orm-mybatis/mybatis-y-datasource.html



[MyBatis面试题八股文](https://tobebetterjavaer.com/sidebar/sanfene/mybatis.html)

