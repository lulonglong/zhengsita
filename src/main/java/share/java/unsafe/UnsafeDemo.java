package share.java.unsafe;


import com.alibaba.fastjson.JSON;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyuxiang
 * @date 2022-03-02
 */
public class UnsafeDemo {

	public static void main(String[] args) throws Exception{


		Unsafe unsafe = getUnsafe();

		User user = new User();
		System.out.println(user.getAge());
		long ageAddress = unsafe.objectFieldOffset(User.class.getDeclaredField("age"));
		unsafe.putInt(user, ageAddress, 18);


		test1(unsafe);

		test2(unsafe);

		User user2=(User) unsafe.allocateInstance(User.class);
		System.out.println(user2.getAge());
		Map<String,String> map=new HashMap(){
			{
				put("1","2");
			}
		};
		System.out.println(map);
	}

	/**
	 * 数组
	 */
	public static void test1(Unsafe unsafe ){

		int[] arr = {1,2,3,4,5,6,7,8,9,10};

		int b = unsafe.arrayBaseOffset(int[].class);

		int s = unsafe.arrayIndexScale(int[].class);

		// 给索引位置8、9的元素重新赋值
		unsafe.putInt(arr, (long) b + s * 8, 12);
		unsafe.putInt(arr, (long) b + s * 9, 16);

		System.out.println(JSON.toJSONString(arr));
	}

	/**
	 * 系统相关
	 */
	public static void test2(Unsafe unsafe){

		System.out.println(unsafe.addressSize());
		System.out.println(unsafe.pageSize());
	}


	/**
	 * 获取Unsafe实例
	 */
	public static Unsafe getUnsafe() throws Exception {

		Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
		unsafeField.setAccessible(true);
		// 只有静态字段，可以直接get(null)
		Unsafe unsafe =(Unsafe) unsafeField.get(null);
		return unsafe;
	}

}
