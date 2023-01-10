package share.fastjson;


import com.alibaba.fastjson.annotation.JSONType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Date: 16/9/19
 * Time: 下午4:21
 * Name: TradeDetail
 * Description:
 */
@JSONType(
        //serializer = DemoObjectSerializable.class,
        //deserializer = DemoObjectDeserializable.class,
        serialzeFilters = {share.fastjson.ValueFilterTest.class})
public class TradeDetailInfo implements Serializable {

    //@JSONField(name = "trade_no", deserialize = false)
    public String tradeNo;

    public String platform;

    public String account;

    public Date payTime;

    public BigDecimal payAmount;

    public BigDecimal refundedAmount;

    public OrderItem orderItem;

    public TradeDetailInfo() {
    }

}
