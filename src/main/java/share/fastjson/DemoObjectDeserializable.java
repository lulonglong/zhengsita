package share.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

import java.lang.reflect.Type;

/**
 * @author liyuxiang
 * @date 2022-07-21
 */
public class DemoObjectDeserializable implements ObjectDeserializer {

	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		System.out.println("DemoObjectDeserializable");
		return parser.parseObject(type);
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}
}
