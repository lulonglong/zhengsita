# User重构

## 积分重构

1、积分扣减，不溯源
直接从即将过期的积分中扣除

2、下单使用积分后退款，归还积分，也不溯源
直接将退款的积分新增到明年过期的积分中

3、积分过期也不溯源
直接将当年需要过期的积分扣除，并将明年过期的积分转移到今年过期的字段中来
优化之后，这种过期速度也是挺慢的

## 购物金优化

购物金业务和积分类似，参考积分过期失败的数据库设计，购物金重新做了表结构设计

每期(10月、11月)积分是一条数据，包含进账、出账、结余

用户的当前购物金，就是近两期的结余总和

过期购物金，直接将10月那期数据过期即可，无需购物金转移的操作

## 开通VIP重构

1、存在购买、业务赠送、客诉赠送等等各种开通VIP的场景，不同的开通方式权益也不相同

2、梳理核心业务逻辑，流程分为

* 开通前的参数校验(抽象方法)
* 开通VIP数据落库
* 保存开通日志(抽象方法)
* 发放开通福利(抽象方法)

3、各个不同的开通方式，去实现抽象方法，写自己的业务逻辑

## 废弃user中接口
1、第一轮，根据codesearch搜索结果，废弃无用的dubbo接口
2、第二轮，根据kibana接口访问日志，近30天无用的和前端确认后删除
3、第三轮，优化功能类似的接口，保留一个，找前端或业务方修改接口


## 缓存优化
之前缓存偶尔出现脏数据，一个是缓存颗粒度不对，一个写脏数据

优化，双删策略
先修改db，在删除缓存，2s后再次删除缓存



## 根据业务拆包
把用户、积分、签到、abtest、VIP、用户地址、用户清关信息分成不同的包
后续重构是往拆服务上走的

## 接手项目后怎么做
1、看error日志，梳理出当前的风险点，优先解决
2、根据接口访问日志，梳理出高访问量和高耗时的接口，解决
3、梳理核心业务的接口，简化分叉，吃不住的逻辑，加日志观察，看看是否还在正常使用
4、对于之前经常计算错误的积分等用户资产，加检查数据的任务，发现有异常，及时发邮件通知自己
5、对已经产生负面的不合理的模块，分批次的重构。从实际的业务考虑，不为了重构而重构

