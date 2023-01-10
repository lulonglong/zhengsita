package share.fastjson;


import com.alibaba.fastjson.serializer.NameFilter;

/**
 * @author liyuxiang
 * @date 2022-07-18
 */
public class NameFilterTest implements NameFilter {

	@Override
	public String process(Object object, String name, Object value) {

		if(name.equals("account")){
			return "ACCOUNT";
		}

		return name;
	}
}
