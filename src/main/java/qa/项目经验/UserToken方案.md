## 获取token
前端向后端接口请求获取token，user把token用AES加密后经过base64，和动态盐一块下发

## 使用token
1、前端在调用业务接口的时候，把业务参数用动态盐加签后，把加签后的签名、参数和token一块传给网关
2、网关先解析token并拿到动态盐，然后用动态盐验签，验证参数是否合法
3、验证无误后，在发起dubbo调用，调用业务服务

## token续期
1、客户端每天发起一次token续期操作
2、token过期后，调用网关接口会失败，此时需要前端重新登录

## token包含的信息
* appId
* deviceId
* userId
* expireTime 有效期一般是30天
* securityLevel 加密等级 设备、boss、User等
* 动态盐

## JWT
https://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html