package share.fastjson;

import com.alibaba.fastjson.serializer.BeforeFilter;

/**
 * @author liyuxiang
 * @date 2022-07-18
 */
public class BeforeFilterTest extends BeforeFilter {

	@Override
	public void writeBefore(Object object) {
		System.out.println("这是前置处理过滤器");
		writeKeyValue("name", "zhangsan");
	}


}
