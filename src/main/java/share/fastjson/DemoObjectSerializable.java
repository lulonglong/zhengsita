package share.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author liyuxiang
 * @date 2022-07-21
 */
public class DemoObjectSerializable implements ObjectSerializer {
	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
		System.out.println(object.toString());
	}
}
