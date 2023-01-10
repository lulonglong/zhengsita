# Mybatis实现原理

## 简要介绍
MyBatis 是一款基于ORM半自动轻量级的持久层框架。Mybatis可以使用简单的XML或注解来配置和映射原生类型、接口和Java的POJO为数据库中的记录。
ORM 全称Object Relation Mapping，表示对象-关系映射的缩写。采用ORM框架，程序不在直接操作数据库，而是以操作对象的形式来操作数据库，ORM框架将面向对象的操作转换成底层的SQL。

## 实现原理

### 架构设计
![avator](https://img-stage.yit.com/CMSRESQN/f5cb72fcfcf42dabf9c3b7c168102adb_1812X1208.png)

### 总体流程
* 加载并初始化配置
  * mybatis.xml 框架层面的一些配置，加载到Configuration中
  * UserMapper.xml 映射关系配置，每一条Sql都会加载为MappedStatement对象，并放入到Configuration的mappedStatements中
  * mapper也支持注解形式的配置  
  
* 接收调用请求
  * 调用SqlSession的接口的方法
  * 调用UserMapper接口的方法
  
* 处理请求操作
	* 根据sql的id找到MappedStatement对象
	* 传入参数，得到最终要执行的sql
	* 获取数据库连接，并执行sql
	* 对得到的结果进行转换，并返回
  
* 总体流程图

![avator](https://img-stage.yit.com/CMSRESQN/e6d4416469c187b04cc570031ba5625c_1742X2238.jpeg)

* 初始化配置核心源码
```

    /**
	 * 解析核心配置文件
	 */
	private void parseConfiguration(XNode root) {
		try {
			// issue #117 read properties first
			propertiesElement(root.evalNode("properties"));
			Properties settings = settingsAsProperties(root.evalNode("settings"));
			loadCustomVfs(settings);
			loadCustomLogImpl(settings);
			typeAliasesElement(root.evalNode("typeAliases"));
			
			// 配置插件到 interceptorChain
			pluginElement(root.evalNode("plugins"));

			objectFactoryElement(root.evalNode("objectFactory"));
			objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
			reflectorFactoryElement(root.evalNode("reflectorFactory"));
			settingsElement(settings);
			// read it after objectFactory and objectWrapperFactory issue #631
			environmentsElement(root.evalNode("environments"));
			databaseIdProviderElement(root.evalNode("databaseIdProvider"));
			typeHandlerElement(root.evalNode("typeHandlers"));

			// 加载Mapper文件，初始化 mapperRegistry、mappedStatements等容器
			mapperElement(root.evalNode("mappers"));

		} catch (Exception e) {
			throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
		}
	}
```

### Configuration
  * loadedResources: 是否已加载映射文件，如果没有加载的话，在addMapper的时候，会尝试去加载

### SqlSession
* 是Mybatis工作的主要API，表示和数据库交互的会话
* 可以理解为 SqlSession 是对 JDBC操作的connection对象进行了一次封装。
* DefaultSqlSession，Mybatis 默认提供的SqlSession，是线程不安全的
* SqlSessionManager，Mybatis 默认提供的SqlSession，是线程安全的
* SqlSessionTemplate，是Mybatis与Spring 整合时的线程安全SqlSession
* 缓存管理
  * 一级缓存，默认是开启的，没找到能关掉的地方。作用在同一个SqlSession
  * 二级缓存，可配置，默认是关闭的，作用在不同的SqlSession中

### Executor
* Mybatis的执行器，是Mybatis调度的核心，负责SQL语句的生成和查询缓存的维护
  * SimpleExecutor
	  * 默认处理器，为每个sql都创建一个执行语句 	

  * BatchExecutor
	  * 批量执行更新处理器
	  * 利用JDBC对批处理的支持
	  
  * ReuseExecutor
	  * 重复使用处理器
	  * 在传统的 JDBC 编程中，重用 Statement 对象是常用的一种优化手段，该优化手段可以减少 SQL 预编译的开销以及创建和销毁 Statement 对象的开销，从而提高性能。ReuseExecutor 提供了 Statement 重用功能，ReuseExecutor 中通过 statementMap 字段 缓存使用过的 Statement 对象，key 是 SQL 语句，value 是 SQL 对应的 Statement 对象

* SimpleExecutor
```
    @Override
	public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
		Statement stmt = null;
		try {

		    // 获取配置
			Configuration configuration = ms.getConfiguration();

			// 获取StatementHandle
			StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);

			// 准备Statement
			stmt = prepareStatement(handler, ms.getStatementLog());

			// 执行sql并封装返回结果
			return handler.query(stmt, resultHandler);
			
		} finally {
			closeStatement(stmt);
		}

```

### MappedStatement

* 把每一条sql(select | update | delete | insert) 节点封装为一个 MapperStatement
* 主要属性
  * SqlSource
  * useCache 默认是false。为true的话，则开始的是二级缓存
  * timeout 超时时间，单位秒	

### StatementHandler
* 封装了JDBC Statement的操作，负责对JDBC Statement操作，如设置参数、转换结果集
* SimpleStatementHandler : 对应JDBC中常用的Statement接口，用于简单SQL的处理
* PreparedStatementHandler : 对应JDBC中的PreparedStatement，预编译SQL的接口
* CallableStatementHandler : 对应JDBC中CallableStatement，用于执行存储过程相关的接口
* RoutingStatementHandler : 是以上三个接口的路由，没有实际操作，只是负责上面三个StatementHandler的创建及调用

* SimpleStatementHandler
```
    @Override
	public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {

	    // 获取原始sql
	    String sql = boundSql.getSql();

	    // 执行sql语句
		statement.execute(sql);

		// 封装执行结果
		return resultSetHandler.handleResultSets(statement);
	}

```

### ParameterHandler
* 将参数转换成JDBC Statement所需要的参数
* DefaultParameterHandler 默认实现类

### ResultSetHandler
* 将JDBC返回的ResultSet结果集对象转换成List类型的集合
* DefaultResultSetHandler 默认实现类

### TypeHandler
* 负责Java数据类型和jdbc数据类型之间的转换
* DateTypeHandler
* StringTypeHandler

### SqlSource
* 负责将用户传递的parameterObject，动态的生成的SQL语句，并将信息封装到BoundSql对象中，并返回
* DynamicSqlSource
* ProviderSqlSource

### BoundSql
* 封装动态生成的SQL语句以及相应的参数信息

### Interceptor
* Mybatis的拦截器，是插件的实现方式。
* MyBatis 允许你在已映射语句执行过程中的某一点进行拦截调用。默认情况下，MyBatis 允许使用插件来拦截的方法调用包括
  * 执行器 Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
  * SQL语法构建器 StatementHandler (prepare, parameterize, batch, update, query)
  * 参数处理器 ParameterHandler (getParameterObject, setParameters)
  * 结果集处理器 ResultSetHandler (handleResultSets, handleOutputParameters)
    
## Mapper接口
* 项目中编写的Mapper接口，是并没有实现类，是如何和xml文件的sql对应起来的
* mapper接口注册的时候，通过JDK动态代理，注册MapperProxy代理类，每个方法会生成一个MapperMethod对象
* 通过全限定名，也就是statementId，确定映射关系

### MapperProxy
* Mapper接口的实现类
* 通过 MapperMethod，执行SQL语句

### MapperMethod
* 把接口的每个具体的方法都封装为一个MapperMethod对象
* 通过SqlSession，执行具体的SQL语句

## 自定义插件
* share.mybatis.ExamplePlugin
* mybatis-config.xml中要注册插件
```
    <plugins>
        <plugin interceptor="share.mybatis.ExamplePlugin"/>
    </plugins>
```

* com.github.pagehelper.PageInterceptor 分页插件

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
    * JdbcTransaction的commit

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

* mybatis 自动提交事务实现
```

  @Override
  public void afterPropertiesSet() throws Exception {
    notNull(dataSource, "Property 'dataSource' is required");
    notNull(sqlSessionFactoryBuilder, "Property 'sqlSessionFactoryBuilder' is required");
    state((configuration == null && configLocation == null) || !(configuration != null && configLocation != null),
              "Property 'configuration' and 'configLocation' can not specified with together");

    this.sqlSessionFactory = buildSqlSessionFactory();
  }

 
  protected SqlSessionFactory buildSqlSessionFactory() throws Exception {

    ...

    // 设置环境，包括数据源，事务管理器
    targetConfiguration.setEnvironment(new Environment(this.environment,
        this.transactionFactory == null ? new SpringManagedTransactionFactory() : this.transactionFactory,
        this.dataSource));
        
    ...    

    return this.sqlSessionFactoryBuilder.build(targetConfiguration);
  }
  
  SqlSessionTemplate
  private class SqlSessionInterceptor implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      SqlSession sqlSession = getSqlSession(SqlSessionTemplate.this.sqlSessionFactory,
          SqlSessionTemplate.this.executorType, SqlSessionTemplate.this.exceptionTranslator);
      try {
        Object result = method.invoke(sqlSession, args);
        if (!isSqlSessionTransactional(sqlSession, SqlSessionTemplate.this.sqlSessionFactory)) {
          // force commit even on non-dirty sessions because some databases require
          // a commit/rollback before calling close()
          
          // 非提交事务方法，每次执行完，都提交一次。如果是事务的话，靠spring去提交
          sqlSession.commit(true);
        }
        return result;
      } catch (Throwable t) {
        Throwable unwrapped = unwrapThrowable(t);
        if (SqlSessionTemplate.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {
          // release the connection to avoid a deadlock if the translator is no loaded. See issue #22
          closeSqlSession(sqlSession, SqlSessionTemplate.this.sqlSessionFactory);
          sqlSession = null;
          Throwable translated = SqlSessionTemplate.this.exceptionTranslator
              .translateExceptionIfPossible((PersistenceException) unwrapped);
          if (translated != null) {
            unwrapped = translated;
          }
        }
        throw unwrapped;
    }
  

```


## 和spring结合
* 引入mybatis-spring pom
  
* 配置org.mybatis.spring.SqlSessionFactoryBean
  * 需指定 dataSource 和 configLocation(mybatis-xml的路径)
  * 用来获取 SqlSessionFactory
  * 如配置 mapperLocations 参数，则会加载mapper.xml，解析出MapperStatement并放入到配置中
  
* MapperScannerConfigurer
  * 扫描basePackage指定路径下的文件，创建mapper接口的代理类并注入到 spring bean的容器中
  * 同时会检查是否已加载mapper.xml到配置中，若无加载，则会通过org.mybatis.spring.mapper.MapperFactoryBean.checkDaoConfig尝试加载  

## 设计模式

### Builder构建者模式
* XMLConfigBuilder
* MapperAnnotationBuilder

### 工厂模式
* MapperProxyFactory
* DefaultSqlSessionFactory

### 代理模式
* MapperProxy

#### 动态代理
* JDK动态代理 java.lang.reflect.InvocationHandler
* cglib ?

###参考文档
[MyBatis拦截器机制](https://www.cnblogs.com/54chensongxia/p/11850626.html)
[MyBatis原理系列](https://www.jianshu.com/p/4e268828db48)
[Mybatis源码](https://github.com/lyxiang/mybatis-3)

