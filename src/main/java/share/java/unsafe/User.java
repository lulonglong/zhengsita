package share.java.unsafe;

import java.io.Serializable;

/**
 * @author liyuxiang
 * @date 2022-03-02
 */
public class User implements Serializable {

	private String name;

	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
