package share.fastjson;

import com.alibaba.fastjson.annotation.JSONCreator;

import java.io.Serializable;

/**
 * @author liyuxiang
 * @date 2022-07-21
 */
public class OrderItem implements Serializable {

	private String spuId;

	private int quantity;

	public String getSpuId() {
		return spuId;
	}

	public void setSpuId(String spuId) {
		this.spuId = spuId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@JSONCreator
	public OrderItem(String spuId, int quantity) {
		System.out.println("JSONCreator");
		this.spuId = spuId;
		this.quantity = quantity;
	}
}
