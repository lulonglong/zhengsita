# Spring-mybatis

## 关键接口解释

```
SqlSession：Mybatis提供的基础接口，用来执行SQL命令
DefaultSqlSession：Mybatis对SqlSession的默认实现
SqlSessionTemplate：Spring对SqlSession自动提交事务的封装
SqlSessionFactoryBean：提供SqlSessionFactory实例
MapperScannerConfigurer：扫描sql xml配置以及Mapper接口
```

## mybatis 使用回顾
```
1.配置mybatis-config.xml文件

2.定义Mapper接口
public interface UserMapper {
    //查询所有
    public ArrayList<User> getUserList();
}
		
3.定义SQL命令
<mapper namespace="com.qiaob.mapper.UserMapper">
    <select id="getUserList" resultType="com.qiaob.pojo.User">
    	select * from mybatis.user
  	</select>
</mapper>

4.封装获取SqlSession的方法（非必须）
public class MybatisUtils {
    static {
        //使用mybatis第一步：获取sqlSessionFactory对象
        try {
            InputStream inputStream= Resources.getResourceAsStream("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static SqlSessionFactory sqlSessionFactory;
    
    public static SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }
}

//Demo使用方法
@Test
public void testUserList(){
    //第一步：获得sqlSession对象
    SqlSession sqlSession = MybatisUtils.getSqlSession();
    //第二步：执行sql
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);

    ArrayList<User> userList = mapper.getUserList();
    for (User user : userList) {
        System.out.println(user);
    }

    //第三步：关闭sqlSession
    sqlSession.close();
}

```


## spring-mybatis 使用回顾
```
mybatis使用过程的前三步基本配置不变，然后将加载配置自动化，mapper注入自动化

4.配置SqlSessionFactoryBean可自动获取SqlSessionFactory，相当于mybatis使用过程的第四步
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="configLocation" value="classpath:mybatis-config.xml"/>
</bean>

5.配置MapperScannerConfigurer，扫描sql xml配置以及Mapper接口
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
     <property name="basePackage" value="com.xxx.crm.dao.mapper" />
     <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
</bean>

6.使用方法
public class PlanTest extends BaseTest {
    @Autowired
    private PlanMapper planMapper;

    @Test
    public void testCountAppNotifyToAll(){
        Date startTime=new DateTime().plusDays(1).withMillisOfDay(0).toDate();
        Date endTime=new DateTime(startTime).plusDays(1).toDate();
        int count1=planMapper.countAppNotifyToAll("472",startTime,endTime,"");
    }
}
```

## 实现原理概述
```
1.配置MapperScannerConfigurer，此类是BeanDefinitionRegistryPostProcessor的实现，MapperScannerConfigurer扫描配置包，查找Mapper接口，注册对应代理bd。此时bd的class指定的是MapperFactoryBean
2.由MapperFactoryBean创建对应的Mapper代理实例
3.SqlSessionTemplate封装了事务并执行命令
```

## SqlSessionFactoryBean解析

```sequence
title: 初始化 SqlSessionFactoryBean
participant SqlSessionFactoryBean as ssfb
participant XMLConfigBuilder as xmlcb
participant SqlSessionFactoryBuilder as ssfbuilder

ssfb->ssfb:入口方法：afterPropertiesSet
ssfb->ssfb:buildSqlSessionFactory
ssfb->xmlcb:xmlConfigBuilder.getConfiguration()
xmlcb-->ssfb:
ssfb->xmlcb:xmlConfigBuilder.parse()
xmlcb-->ssfb:
ssfb->ssfbuilder:sqlSessionFactoryBuilder.build(targetConfiguration)
ssfbuilder-->ssfb:
```
## MapperScannerConfigurer 解析
```sequence
title: 初始化MapperScannerConfigurer
participant MapperScannerConfigurer as msc
participant ClassPathMapperScanner as cpms
participant ClassPathBeanDefinitionScanner as cpbds
participant GenericBeanDefinition as gbd

msc->msc:入口方法：postProcessBeanDefinitionRegistry
msc->cpms:scanner.scan(父类ClassPathBeanDefinitionScanner实现)
cpms->cpms:doScan
cpms->cpbds:super.doScan(basePackages)
cpbds-->cpms:
cpms->gbd:definition.getPropertyValues().add("mapperInterface", definition.getBeanClassName())
gbd-->cpms:
cpms->gbd:definition.setBeanClass(MapperFactoryBean.class)
gbd-->cpms:将BeanClass设置为一个FactoryBean来统一生成代理实例
cpms->gbd:definition.getPropertyValues().add("sqlSessionFactory", new RuntimeBeanReference(this.sqlSessionFactoryBeanName))
gbd-->cpms:
cpms-->msc:
```

```sequence
title: 初始化MapperFactoryBean
participant MapperFactoryBean as mfb
participant SqlSessionTemplate as sst
participant Configuration as config

mfb->mfb:入口方法：父类DaoSupport的afterPropertiesSet
mfb->mfb:checkDaoConfig
mfb->sst:getSqlSession().getConfiguration()
sst-->mfb:
mfb->config:configuration.addMapper(this.mapperInterface)
config-->mfb:
```

## Mapper接口代理类生成及接口方法执行
```
整个过程涉及到了两个产生代理实例的地方，一个是生成Mapper接口的代理实例，另一个是SqlSessionTemplate执行命令时，统一转给了SqlSession接口的一个代理实例，做了事务封装
```
```sequence
title: 获取Mapper接口代理实例
participant MapperFactoryBean as mfb
participant SqlSessionTemplate as sst
participant Configuration as config
participant MapperRegistry as mr
participant MapperProxyFactory as mpf
participant MapperProxy as mp
participant Proxy as proxy

mfb->mfb:入口方法：getObject
mfb->sst:getMapper
sst->config:getMapper
config->mr:getMapper
mr->mpf:mapperProxyFactory.newInstance(sqlSession)
mpf->mp:new MapperProxy<>(sqlSession, mapperInterface, methodCache)
mp-->mpf:
mpf->proxy:Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
proxy-->mpf:
mpf-->mr:
mr-->config:
config-->sst:
sst-->mfb:
```

```sequence
title: 生成SqlSessionTemplate代理实例
participant SqlSessionTemplate as sst
participant Proxy as proxy

sst->sst:入口方法1：构造函数
sst->proxy: (SqlSession) newProxyInstance(., ., new SqlSessionInterceptor())
proxy-->sst:
```
```sequence
title: 方法执行
participant MapperProxy as mp
participant MapperMethod as mm
participant SqlCommand as sc
participant MethodSignature as ms
participant SqlSession as ss
participant SqlSessionInterceptor as ssi

mp->mp:入口方法：invoke
mp->mm:new MapperMethod
mm->sc:new SqlCommand
sc-->mm:
mm->ms: new MethodSignature
ms-->mm:
mm-->mp:
mp->mm:execute(sqlSession, args)
mm->ss:insert、update等指令方法
ss->ssi:invoke代理逻辑
ssi-->ss:
ss-->mm:
mm-->mp:
```
* spring-mybatis 自动提交事务实现
```java
    
/****org.mybatis.spring.SqlSessionTemplate.SqlSessionInterceptor****/
  private class SqlSessionInterceptor implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      SqlSession sqlSession = getSqlSession(SqlSessionTemplate.this.sqlSessionFactory,
          SqlSessionTemplate.this.executorType, SqlSessionTemplate.this.exceptionTranslator);
      try {
        Object result = method.invoke(sqlSession, args);
        //判断事务是否由Spring托管
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
    
    
    public static boolean isSqlSessionTransactional(SqlSession session, SqlSessionFactory sessionFactory) {
    notNull(session, NO_SQL_SESSION_SPECIFIED);
    notNull(sessionFactory, NO_SQL_SESSION_FACTORY_SPECIFIED);

    // 从 TransactionSynchronizationManager 中，获得 SqlSessionHolder 对象
    SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);

    // 如果相等，说明在 Spring 托管的事务中
    return (holder != null) && (holder.getSqlSession() == session);
  }
```

## 参考文档
[MyBatis 源码分析 - MyBatis-Spring 源码分析](https://www.cnblogs.com/lifullmoon/p/14015235.html)

