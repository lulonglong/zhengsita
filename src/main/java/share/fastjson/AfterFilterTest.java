package share.fastjson;

import com.alibaba.fastjson.serializer.AfterFilter;

/**
 * @author liyuxiang
 * @date 2022-07-18
 */
public class AfterFilterTest extends AfterFilter {

	@Override
	public void writeAfter(Object object) {
		System.out.println("这是后置处理过滤器");
		writeKeyValue("name", "lisi");
	}
}
