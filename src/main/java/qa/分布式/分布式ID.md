# 分布式ID

## 概念
```
传统的自增id方式一般依赖数据库的自增id。这种方式的可移植性较差，扩展能力也较差。虽然在单库单表的情况下可用，但是在任何重要业务中最好都不要使用这种方式。此时一个能够生成全局唯一ID的系统是非常必要的。那么这个全局唯一ID就叫分布式ID。

分布式ID需满足以下条件
全局唯一：基本要求就是必须保证ID是全局性唯一的
高性能：高可用低延时，ID生成响应要快
高可用：无限接近于100%的可用性
好接入：遵循拿来主义原则，在系统设计和实现上要尽可能的简单
趋势递增：最好趋势递增，这个要求就得看具体业务场景了，一般不严格要求
```



## Redis模式

```
利用Redis的自增，实现简单，但是强依赖Redis的持久化，并且每次生成id需要一次网络交互
```



## 号段模式

```sql
号段模式可以理解为从数据库批量的获取自增ID，每次从数据库取出一个号段范围，例如 (1,1000] 代表1000个ID，每台机器都从数据库里拉取一个号段，快要用完时提前再拉取下一个号段。
逻辑sql如下，先更新max_id，再把记录查询出来，(max_id-step,max_id]就是这次拉取的号段
update leaf_alloc set max_id = {max_id+step} where biz_tag = 'order_id';
select max_id,step from leaf_alloc where biz_tag = 'order_id';
```
```sql
CREATE TABLE `leaf_alloc` (
  `biz_tag` varchar(128)  NOT NULL DEFAULT '' COMMENT '业务key',
  `max_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '当前已经分配了的最大id',
  `step` int(11) NOT NULL COMMENT '初始步长，也是动态调整的最小步长',
  `description` varchar(256)  DEFAULT NULL COMMENT '业务key的描述',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据库维护的更新时间',
  PRIMARY KEY (`biz_tag`)
) ENGINE=InnoDB;
```



## 雪花算法(Snowflake)

```
Snowflake算法，是 Twitter 开源的分布式 id 生成算法。其核心思想就是：使用一个 64 bit 的 long 型的数字作为全局唯一 id。在分布式系统中的应用十分广泛，且ID 引入了时间戳。这 64 个 bit 中，其中 1 个 bit 是不用的，然后用其中的 41 bit 作为毫秒数，用 10 bit 作为工作机器 id，12 bit 作为序列号。

第一个部分是1个bit：0，这个是无意义的。因为二进制里第一个 bit 位如果是 1，那么都是负数，但是我们生成的 id 都是正数，所以第一个 bit 统一都是0。

第二个部分是41个bit：表示的是时间戳。单位是毫秒。41bit可以表示的数字多达 2^41 - 1，也就是可以标识2^41 - 1 个毫秒值，换算成年就是表示69年的时间。

第三个部分是5个 bit：表示的是机房id，5个bit代表机器id。意思就是最多代表2^5个机房（32 个机房）。

第四个部分是5个bit：表示的是机器id。每个机房里可以代表2^5个机器（32台机器），也可以根据自己公司的实际情况确定。

第五个部分是12个bit：表示的序号，就是某个机房某台机器上这一毫秒内同时生成的id的序号。12 bit可以代表的最大正整数是 2^12 - 1 = 4096，也就是说可以用这个 12 bit 代表的数字来区分同一个毫秒内的4096个不同的id。

以上各部分中主要有三部分，第一段时间戳，第二段机器编号，第三段机器内的自增。在实际使用场景中可以根据自己的业务随意调整更改。
通常配合zk使用，注册获取机器编号。没有zk的话也可以把机器编号硬配置到配置文件中
```
 ![alt](https://mmbiz.qpic.cn/mmbiz_png/wJvXicD0z2dUyAByrX07E8icI6ichu8DK2cBbtKaicJX5hNYym2unMhWFt6Q4HLJFGFnkKq2XrqZoplrAd0VoU1JoQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)



## 开源产品

```
如果需要分布式ID，不要重复造轮子。市面上有一些成熟的分布式ID工具。
市面上的开源产品都是基于号段模式和雪花算法来实现的。

①百度（uid-generator）：基于雪花算法实现的，与原始的snowflake算法不同在于，uid-generator支持自定义时间戳、工作机器ID和 序列号等各部分的位数，而且uid-generator中采用用户自定义workId的生成策略
②美团（Leaf）：Leaf同时支持号段模式和雪花算法模式，可以切换使用
③滴滴（Tinyid）：号段模式
```

## 参考文档
[9 种分布式 ID 生成方式，总有一款适合你！](https://mp.weixin.qq.com/s?src=11&timestamp=1667879503&ver=4153&signature=bd8D0thBfI31QtGppL3TbReUTe5Uy6fZh*wCznIg7XLxgoNX8n2YNEr08HjqQTGnB6fDl59mNRIRDPEH97nzE18pF8csKtIxQk9FV*NfObkRJBrW4tKaQwUzemzwMuVv&new=1)