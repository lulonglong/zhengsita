package share.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author liyuxiang
 * @date 2022-07-14
 */
public class Demo {

	public static void main(String[] args) {

		test1();

		test2();

		test3();

		test4();

		test5();

		test6();

		test7();

		test8();

		/*int a = 3;
		int b = 5;
		System.out.println(a|=b);
		System.out.println(3<<2);


		TradeDetailInfo tradeDetailInfo = new TradeDetailInfo();
		tradeDetailInfo.account = "1";
		tradeDetailInfo.tradeNo = "2";
		System.out.println(JSON.toJSONString(tradeDetailInfo));
        *//*System.out.println(JSON.toJSONString(tradeDetailInfo, SerializerFeature.PrettyFormat));
        System.out.println(JSON.toJSONString(tradeDetailInfo, SerializerFeature.WriteClassName));
        System.out.println(JSON.toJSONString(tradeDetailInfo, SerializerFeature.WriteClassName, SerializerFeature.PrettyFormat));
        System.out.println(JSON.toJSONString(tradeDetailInfo, SerializerFeature.BeanToArray, SerializerFeature.PrettyFormat));*//*

		String json = "{\"account\":\"1\",\"tradeNo\":\"2\"}";
		TradeDetailInfo tradeDetailInfo1 = JSON.parseObject(json, TradeDetailInfo.class);
		System.out.println(tradeDetailInfo1);

		int i = 0;
		if (i =='{'){

		}

		char c = '{';
		char baa= 1;
		System.out.println(Integer.valueOf(c));*/
	}

	/**
	 * 输出格式
	 */
	public static void test1(){

		share.fastjson.TradeDetailInfo tradeDetailInfo = new share.fastjson.TradeDetailInfo();
		tradeDetailInfo.account = "1";
		tradeDetailInfo.tradeNo = "2";
		//System.out.println(JSON.toJSONString(tradeDetailInfo, SerializerFeature.QuoteFieldNames, SerializerFeature.PrettyFormat));
		System.out.println(JSON.toJSONString(tradeDetailInfo, SerializerFeature.UseSingleQuotes, SerializerFeature.PrettyFormat));
		System.out.println(JSON.toJSONString(tradeDetailInfo, SerializerFeature.WriteClassName, SerializerFeature.PrettyFormat));
		System.out.println();

	}

	public static void test2(){

		share.fastjson.TradeDetailInfo tradeDetailInfo = new share.fastjson.TradeDetailInfo();
		tradeDetailInfo.account = "1";
		tradeDetailInfo.tradeNo = "2";

		System.out.println("test2");
		System.out.println(JSON.toJSONString(tradeDetailInfo, new BeforeFilterTest()));

		System.out.println();
	}

	public static void test3(){

		share.fastjson.TradeDetailInfo tradeDetailInfo = new share.fastjson.TradeDetailInfo();
		tradeDetailInfo.account = "1";
		tradeDetailInfo.tradeNo = "2";

		System.out.println("test3");
		System.out.println(JSON.toJSONString(tradeDetailInfo, new AfterFilterTest()));

		System.out.println();
	}

	public static void test4(){

		share.fastjson.TradeDetailInfo tradeDetailInfo = new share.fastjson.TradeDetailInfo();
		tradeDetailInfo.account = "1";
		tradeDetailInfo.tradeNo = "2";

		System.out.println("test4");
		System.out.println(JSON.toJSONString(tradeDetailInfo, new share.fastjson.NameFilterTest()));

		System.out.println();
	}

	public static void test5(){

		share.fastjson.TradeDetailInfo tradeDetailInfo = new share.fastjson.TradeDetailInfo();
		tradeDetailInfo.account = "lyx";
		tradeDetailInfo.tradeNo = "2";

		System.out.println("test5");
		System.out.println(JSON.toJSONString(tradeDetailInfo, new share.fastjson.ValueFilterTest()));

		System.out.println();
	}

	public static void test6(){

		share.fastjson.TradeDetailInfo tradeDetailInfo = new share.fastjson.TradeDetailInfo();
		tradeDetailInfo.account = "lyx";
		tradeDetailInfo.tradeNo = "2";

		System.out.println("test6");
		System.out.println(JSON.toJSONString(tradeDetailInfo, new share.fastjson.PropertyFilterTest()));

		System.out.println();
	}

	public static void test7(){

		share.fastjson.TradeDetailInfo tradeDetailInfo = new share.fastjson.TradeDetailInfo();
		tradeDetailInfo.account = "lyx";
		tradeDetailInfo.tradeNo = "2";

		System.out.println("test7");
		System.out.println(JSON.toJSONString(tradeDetailInfo, new share.fastjson.PropertyPreFilterTest()));

		System.out.println();
	}

	public static void test8(){

		String json = "{\"account\":\"lyx\"}";

		String json2 = "{\"account\":\"1\",\"orderItem\":{\"quantity\":2,\"spuId\":\"1231\",\"name\":\"lisi\"},\"trade_no\":\"2\"}";

		System.out.println("test8");

		share.fastjson.TradeDetailInfo tradeDetailInfo = JSON.parseObject(json, share.fastjson.TradeDetailInfo.class);
		System.out.println(tradeDetailInfo);

		share.fastjson.TradeDetailInfo tradeDetailInfo2 = JSON.parseObject(json2, share.fastjson.TradeDetailInfo.class);
		System.out.println(tradeDetailInfo2);

		System.out.println();
	}
}
