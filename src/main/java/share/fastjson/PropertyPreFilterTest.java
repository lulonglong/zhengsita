package share.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

/**
 * @author liyuxiang
 * @date 2022-07-18
 */
public class PropertyPreFilterTest implements PropertyPreFilter {
	@Override
	public boolean apply(JSONSerializer serializer, Object object, String name) {

		if(name.equals("account")){
			return true;
		}

		return false;
	}
}
