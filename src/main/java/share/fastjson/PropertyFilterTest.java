package share.fastjson;
import com.alibaba.fastjson.serializer.PropertyFilter;

/**
 * @author liyuxiang
 * @date 2022-07-18
 */
public class PropertyFilterTest implements PropertyFilter {

	@Override
	public boolean apply(Object object, String name, Object value) {

		if(name.equals("account") && value instanceof String){
			return true;
		}

		return false;
	}
}
