package share.mybatis;

import java.io.Serializable;

/**
 * @author liyuxiang
 * @date 2021-12-30
 */
public class UserDTO implements Serializable {

	private int id;

	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
