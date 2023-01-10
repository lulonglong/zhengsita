# DB和缓存一致性

###### 1. 解决缓存一致性的思路
```
要朝着最终一致性的方向思考，不要考虑强一致性
```

###### 2. 缓存不一致的来源
```
在写的情况下
①操作DB和操作缓存其中一步失败
②并发导致
```

###### 3. 删除缓存好还是更新缓存好
```
不推荐使用更新缓存，原因如下
①相对更容易出现数据不一致
②更新的数据不一定有用，浪费缓存空间
③更新缓存可能需要对现有数据做整合，还可能牵扯到其他数据，浪费计算资源降低性能

一切落地都要根据具体场景来，规则不适用于特殊场景，要灵活运用。例如购买会员成功，马上拉取会员信息可能拉不到，缓存也没了，从库数据也不是最新的。我们可以在缓存上做处理，写事务提交后立刻更新缓存而不是常用的直接删除缓存，2s后再删除缓存防止更新的数据是错的。
```

###### 4. 如果是写业务，如何防止读到缓存脏数据后把数据库的数据覆盖了
```
可能会出现某种写业务，会先查到之前的数据，基于之前的数据修改后再入库。这个时候如果查的是缓存并且缓存数据是脏的，那问题就严重了。
为了防止这种问题的出现，有两个方面要改进：
①尽量不要出现先查再改，而是直接update，通过sql函数把值算出来，例如增加一年vip有效期，不应该先查出来加一年再写回去，而是直接expireTime=add_date(expireTime,加一年)
②如果必须要先查后写，那必须要从主库查询，不能走从库更不能走缓存
```

## 双删延迟策略

```
时间线：
①A写数据库
②A提交事务
③A删缓存
存在两种情况导致数据不一致：
1）③失败了会导致不一致
2）当有从库时在③之后立刻查询，从库中的数据可能还不是新的，会把旧数据写入缓存导致不一致

双删延迟策略就是删除缓存后休眠一段时间再次删除缓存，如下图所示的5。具体延迟多久需要评估从库延迟和业务流程需要。
下图画的是先删缓存再更新数据库的情况，这种情况更容易出现数据不一致，还是建议先更新数据库，再删缓存，再延时删除。在延时删除时建议缓冲后批量删除。
```
 ![alt](https://mmbiz.qpic.cn/mmbiz_png/TZ5PslE0ZOCQeA7zWxOj6BbfozzJRvPH5cVyFIqnlOhm1tXLtHuGhdSa6dWgvXut03mBkUbqxCNyEuDxLyR69A/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)



## binlog异步删除
```
我们的业务应用在修改数据时，「只需」修改数据库，无需操作缓存。
那什么时候操作缓存呢？这就和数据库的「变更日志」有关了。
拿 MySQL 举例，当一条数据发生修改时，MySQL 就会产生一条变更日志（Binlog），我们可以订阅这个日志(阿里的canal等组件支持)，拿到具体操作的数据，然后再根据这条数据，去删除对应的缓存。
※注意这里也是删除而不是更新，其实更新并不会引起一致性方面的问题。而是考虑到相对好操作，还有就是考虑到上边说的更新缓存的'后两个'坏处。
```
 ![alt](https://mmbiz.qpic.cn/mmbiz_png/TZ5PslE0ZOCQeA7zWxOj6BbfozzJRvPHuzpBjYcQjCE1VBwNSWlF0mIDQtibM5dmticocnAibwibIL4Xfiaafk1LiaIA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)



## 保底方案
```
什么情况下都不要忘记对缓存设置合理的过期时间，在能顶住流量的情况下，缓存时效尽可能短。可以根据用户的在线时长设置合理的过期时间，保证一次上线只访问一次数据库即可。
```

## 参考文档
[缓存一致性问题](https://mp.weixin.qq.com/s?src=11&timestamp=1667898767&ver=4154&signature=E0fAW4kYXZ*g4YPfEKf-n4Yf1h1FrfVk9iJ5YD1u5VeRC7DeDXVhjVmfeLZXzakXm98pG6OL-oaEXEVqKbFDDbO0fJqO2ffFtjpWETpwgwr5NaYIRGYqjMytum8jL9zQ&new=1)
[缓存和数据库一致性问题，看这篇就够了](https://mp.weixin.qq.com/s?src=11&timestamp=1667898767&ver=4154&signature=-pVTczqy*CyaA7XQhrKYidB-saOMjkKU4i5Vj6Bd3bpJ8GWTZfj2e0F-*pKw9I5lmt*ltM8w2SbW0JqTTcDBqnp*62c4IQzLgISaiUsfL2b7LLdSO8BJQxFLR5avUDLr&new=1)







