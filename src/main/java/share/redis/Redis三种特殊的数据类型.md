# Redis三种特殊的数据类型

## geo

Redis 在 3.2 版本中增加了 GEO 类型用于存储和查询地理位置，关于 GEO 的命令不多，主要包含以下 6 个：

### 常用命令
* geoadd
  * 添加地理位置
  * geoadd key longitude latitude member [longitude latitude member ...]
    * longitude 表示经度
    * latitude 表示纬度
    * member 是为此经纬度起的名字
    * 支持批量添加  
  * geoadd site 116.36 39.922461 yuetan
  * geoadd site 116.404269 39.913164 tianan  

* geopos
  * 查询位置信息
  * geopos key member [member ...]

* geodist
  * 距离统计
  * geodist key member1 member2 [unit]
    * unit 参数表示统计单位，它可以设置以下值
      * m：以米为单位，默认单位；
      * km：以千米为单位；
      * mi：以英里为单位；
      * ft：以英尺为单位。
  * geodist site tianan yuetan km
    * "3.9153"

* georadius
  * 查询某位置内的其他成员信息
  * georadius key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count] [ASC|DESC]
    * 可选参数说明
    * WITHCOORD
      * 返回满足条件位置的经纬度信息
      * georadius site 116.405419 39.913164 5 km withcoord  
      * tianan  
      * "116.40426903963088989"
      * "39.91316289865137179"
    * WITHDIST      
      * 返回满足条件位置与查询位置的直线距离
      * georadius site 116.405419 39.913164 5 km withcoord  
      * tianan
      * 0.0981
    * WITHHASH
      * 返回满足条件位置的哈希信息
      * georadius site 116.405419 39.913164 5 km withhash
      * tianan
      * 4069885552230465
    * COUNT
      * 指定返回满足条件位置的个数
      * georadius site 116.405419 39.913164 5 km count 1
      * tianan
    * ASC|DESC
      * 从近到远|从远到近排序返回
      * georadius site 116.405419 39.913164 5 km desc
      * yuetan
      * tianan
  
* geohash：
  * 查询位置的哈希值
  * geohash key member [member ...]
  * 支持查询一个或多个地址的哈希值
  
* zrem
  * 删除地理位置
  * zrem key member [member ...]  

### 使用场景
附近的人、地点
计算相关的距离信息

### 实现原理

底层是利用zset实现的

实现过于复杂，有兴趣的可以看看
[Redis地理位置GEO的实现原理](https://cloud.tencent.com/developer/article/1949540)

## HyperLogLog
当我们需要统计一个大型网站的独立访问次数时，该用什么的类型来统计？

如果我们使用 Redis 中的集合来统计，当它每天有数千万级别的访问时，将会是一个巨大的问题。因为这些访问量不能被清空，我们运营人员可能会随时查看这些信息，那么随着时间的推移，这些统计数据所占用的空间会越来越大，逐渐超出我们能承载最大空间。

例如，我们用 IP 来作为独立访问的判断依据，那么我们就要把每个独立 IP 进行存储，以 IP4 来计算，IP4 最多需要 15 个字节来存储信息，例如：110.110.110.110。当有一千万个独立 IP 时，所占用的空间就是 15 bit*10000000 约定于 143MB，但这只是一个页面的统计信息，假如我们有 1 万个这样的页面，那我们就需要 1T 以上的空间来存储这些数据，而且随着 IP6 的普及，这个存储数字会越来越大，那我们就不能用集合的方式来存储了，这个时候我们需要开发新的数据类型 HyperLogLog 来做这件事了。

HyperLogLog（下文简称为 HLL）是 Redis 2.8.9 版本添加的数据结构，它用于高性能的基数（去重）统计功能，它的缺点就是存在极低的误差率。

HLL 具有以下几个特点：
* 能够使用极少的内存来统计巨量的数据，它只需要 12K 空间就能统计 2^64 的数据；
* 统计存在一定的误差，误差率整体较低，标准误差为 0.81%；
* 误差可以被设置辅助计算因子进行降低。

### 命令

* 添加元素
  * pfadd key element [element ...]
  * 支持添加一个或多个元素至 HLL 结构中

* 统计不重复的元素
  * pfcount key [key ...]
  * 支持统计一个或多个 HLL 结构
  
* 合并一个或多个 HLL 至新结构
  * pfmerge destkey sourcekey [sourcekey ...]
  * 新增 k 和 k2 合并至新结构 k3 中，代码如下
    
```
127.0.0.1:6379> pfadd k "java" "sql"
(integer) 1
127.0.0.1:6379> pfadd k2 "redis" "sql"
(integer) 1
127.0.0.1:6379> pfmerge k3 k k2
OK
127.0.0.1:6379> pfcount k3
(integer) 3
```

当我们需要合并两个或多个同类页面的访问数据时，我们可以使用 pfmerge 来操作。

### 实现原理

HLL 算法原理

[hyperloglog原理](https://segmentfault.com/a/1190000040312109?utm_source=sf-similar-article)
[用户日活月活怎么统计 - Redis HyperLogLog 详解](https://mp.weixin.qq.com/s/AvPoG8ZZM8v9lKLyuSYnHQ)

## Bitmap

Bitmap 即位图数据结构，都是操作二进制位来进行记录，只有0 和 1 两个状态。

用来解决什么问题？
比如：统计用户信息，活跃，不活跃！ 登录，未登录！ 打卡，不打卡！ 两个状态的，都可以使用 Bitmap
如果存储一年的打卡状态需要多少内存呢？ 365 天 = 365 bit 1字节 = 8bit 46 个字节左右！

Bitmap不属于Redis的基本数据类型，而是基于String类型进行的位操作。

而Redis中字符串的最大长度是 512M，所以 BitMap 的 offset 值也是有上限的，其最大值是：
8 * 1024 * 1024 * 512  =  2^32

### 命令
* setbit 
  * setbit key offset value
  * 针对key存储的字符串值，设置或清除指定偏移量offset上的位(bit)
  * 如果想要设置Bitmap的非零初值，该怎么设置呢？一种方式就是将每个位挨个设置为0或1，但是这种方式比较麻烦，我们可以考虑直接使用SET命令存储一个字符串。
  * 比如对于字符串‘42’，底层保存数据时，使用0-7位保存‘4’，使用8-15位保存‘2’，‘4’对应的ASCII码为0011 0100，‘2’对应的ASCII码为0011 0010
  
* getbit
  * getbit key offset
  * 返回key对应的字符串，offset位置的位（bit）

* bitcount
  * bitcount key [start end]
  * 统计给定字符串中，比特值为1的数量
  * 默认会统计整个字符串，同时也可以通过指定 start 和 end 来限定范围
  * start 和 end 也可以是负数，-1表示最后一个字节，-2表示倒数第二个字节。注意这里是字节，1字节=8比特

* bitpos
  * 返回字符串中，从左到右，第一个比特值为bit（0或1）的偏移量
  * bitpos mykey 1
  * bitpos mykey 0  
    
## 参考
[优秀的基数统计算法](https://learn.lianglianglee.com/%E4%B8%93%E6%A0%8F/Redis%20%E6%A0%B8%E5%BF%83%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E6%88%98/22%20%E4%BC%98%E7%A7%80%E7%9A%84%E5%9F%BA%E6%95%B0%E7%BB%9F%E8%AE%A1%E7%AE%97%E6%B3%95%E2%80%94%E2%80%94HyperLogLog.md)
[查询附近的人](https://learn.lianglianglee.com/%E4%B8%93%E6%A0%8F/Redis%20%E6%A0%B8%E5%BF%83%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E6%88%98/20%20%E6%9F%A5%E8%AF%A2%E9%99%84%E8%BF%91%E7%9A%84%E4%BA%BA%E2%80%94%E2%80%94GEO.md)
