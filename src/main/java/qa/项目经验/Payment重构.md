
# Payment重构

## 梳理逻辑，简化调用链路

### 框架层面
定义支付能力相关接口类：
BaseService

  * app支付，调用微信或支付宝sdk，发起支付单
  * 条码支付
  * 扫码支付

定义请求实现抽象类
AbstractPayBusiness

  * 请求支付逻辑固定下来

定义支付回调实现抽象类
AbstractNotifyBusiness

  * 支付回调逻辑固定下来

### 支付宝
AliService实现BaseService接口

AliPayBusiness实现AbstractPayBusiness抽象类，并实现抽象类中的相关抽象方法，比如过去请求参数、发送查询支付结果消息等

AliService.requestPay()中，调用AliPayBusiness父类AbstractPayBusiness的requestPay方法。

## 支持多商户

重构前是以json的形式维护配置的

多商户数据扩库，并降配置缓存到内存中，支持运营在管理页面中手动更改配置

缓存刷新，双重if校验

```if (DateUtil.intervalSecond(this.refreshDate, new Date()) >= commonConfig.getPayConfigCacheSecond()) {

        //未超过缓存时间的，不刷新缓存
        if (DateUtil.intervalSecond(this.refreshDate, new Date()) >= commonConfig.getPayConfigCacheSecond()) {

            synchronized (lock) {

                //此处做双重判断是为了避免刷新缓存重复执行
                if (DateUtil.intervalSecond(this.refreshDate, new Date()) >= commonConfig.getPayConfigCacheSecond()) {
                    setRefreshDate(new Date());
                    executor.execute(new RefreshMchCacheRunner());
                }

            }
        }

```

