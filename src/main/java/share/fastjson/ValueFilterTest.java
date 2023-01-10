package share.fastjson;

import com.alibaba.fastjson.serializer.ValueFilter;

/**
 * @author liyuxiang
 * @date 2022-07-18
 */
public class ValueFilterTest implements ValueFilter {

	@Override
	public Object process(Object object, String name, Object value) {

		if(String.valueOf(value).equals("lyx")){
			return "LYX";
		}
		return value;
	}
}
