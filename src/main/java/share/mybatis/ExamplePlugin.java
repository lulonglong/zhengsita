package share.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

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
