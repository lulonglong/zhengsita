
# Zookeeper

## 谈下你对 Zookeeper 的认识

ZooKeeper 是一个分布式的，开源的分布式应用程序协调服务。它是一个为分布式应用提供一致性服务的软件，提供的功能包括：配置维护、域名服务、分布式同步、组服务等。

ZooKeeper 的目标就是封装好复杂易出错的关键服务，将简单易用的接口和性能高效、功能稳定的系统提供给用户。

## Zookeeper 都有哪些功能
* 数据发布/订阅
  * 发布者将数据发布到 ZooKeeper 上一个或多个节点上，订阅者从中订阅数据，从而动态获取数据的目的，实现配置信息的集中式管理和数据动态更新。
  * 发布者push数据，订阅者pull数据
* 负载均衡
  * 服务器上线运行的时候，通过在 ZooKeeper 服务器中创建临时节点，向 ZooKeeper 的服务列表进行注册，表示本台服务器已经上线可以正常工作。
  * 通过删除临时节点或者在与 ZooKeeper 服务器断开连接后，删除该临时节点。
  * 通过采用“最小连接数”算法，来确定究竟如何均衡地分配网络会话请求给后台客户端。
    * zk各个临时节点(服务)都存储有当前服务器的连接数。通过判断选择最少的连接数作为当前会话的处理服务器，并通过 setData 方法将该节点连接数加 1。最后，当客户端执行完毕，再调用 setData 方法将该节点信息减 1。
* 命名服务
  * 利用 ZooKeeper 数据模型中的顺序节点作为 ID 编码。
  * 客户端通过调用 create 函数创建顺序节点。服务器成功创建节点后，会响应客户端请求，把创建好的节点信息发送给客户端。客户端用数据节点名称作为 ID 编码 
* 分布式协调/通知
  * watch 
* 集群管理
* Master 选举
* 分布式锁
* 分布式队列

## 谈下你对 ZAB 协议的了解

ZAB 协议是为分布式协调服务 Zookeeper 专门设计的一种支持崩溃恢复的原子广播协议。
ZAB 协议包括两种基本的模式：崩溃恢复和消息广播。
当整个 Zookeeper 集群刚刚启动或者Leader服务器宕机、重启或者网络故障导致不存在过半的服务器与 Leader 服务器保持正常通信时，所有服务器进入崩溃恢复模式，首先选举产生新的 Leader 服务器，然后集群中 Follower 服务器开始与新的 Leader 服务器进行数据同步。

当集群中超过半数机器与该 Leader 服务器完成数据同步之后，退出恢复模式进入消息广播模式，Leader 服务器开始接收客户端的事务请求生成事物提案来进行事务请求处理。

### ZAB 三个阶段

ZAB协议要求每个leader都要经历三个阶段，即发现，同步，广播。
* 发现
  * 即要求zookeeper集群必须选择出一个leader进程，同时leader会维护一个follower可用列表。将来客户端可以这follower中的节点进行通信。
* 同步
  * leader要负责将本身的数据与follower完成同步，做到多副本存储。这样也是体现了CAP中高可用和分区容错。follower将队列中未处理完的请求消费完成后，写入本地事物日志中。
* 广播
  * leader可以接受客户端新的proposal请求，将新的proposal请求广播给所有的follower。

* zk 从节点，怎么做同步的
  * leader节点，发布提案，follower节点回复ack。
  * 一半以上的follower节点回复ack后，leader节点会发布commit命令，各个节点会更新本地的数据
  * 那些未收到commit命令的节点，由于leader和follower的数据通信是FIFO队列，保证顺序的，所以在一下次leader请求通信之前，follower肯定要处理之前的commit请求，比如中间超时断联了 重新连接后，和leader重新同步数据，会严格按照顺序
  * zk 在读方面，不是强一直性的，如果要求读的内容和leader节点是一致的，读取之前要使用sync方法，强行和leader节点保持一致。

## 有多个不同的写请求，是并行的还是串行的

我们都知道ZooKeeper保证的最终一致性也叫顺序一致性，即每个结点的数据都是严格按事务的发起顺序生效的。

ZooKeeper的在选举时通过比较各结点的ZXID和机器ID选出新的主结点的。ZXID由Leader节点生成，有新写入事件时，Leader生成新ZXID并随提案一起广播，每个结点本地都保存了当前最近一次事务的ZXID，ZXID是递增的，所以谁的ZXID越大，就表示谁的数据是最新的。

ZXID有两部分组成：
* 任期：完成本次选举后，直到下次选举前，由同一Leader负责协调写入；
* 事务计数器：单调递增，每生效一次写入，计数器加一。

可以看到，ZXID的低32位是计数器，所以同一任期内，ZXID是连续的，每个结点又都保存着自身最新生效的ZXID，通过对比新提案的ZXID与自身最新ZXID是否相差“1”，来保证事务严格按照顺序生效的。

Leader为了保证提案按ZXID顺序生效，使用了一个ConcurrentHashMap，记录所有未提交的提案，命名为outstandingProposals，key为ZXID，Value为提案的信息。对outstandingProposals的访问逻辑如下：
* 每发起一个提案，会将提案的ZXID和内容放到outstandingProposals中，作为待提交的提案；
* 收到Follower的ACK信息后，根据ACK中的ZXID从outstandingProposals中找到对应的提案，对ACK计数；
* 执行tryToCommit尝试将提案提交，判断流程如下：
  * 判断当前ZXID之前是否还有未提交提案，如果有，当前提案暂时不能提交；
  * 判断提案是否收到半数以上ACK，如果达到半数以上则可以提交；
  * 如果可以提交，将当前ZXID从outstandingProposals中清除并向Followers广播提交当前提案；

Leader是如何判断当前ZXID之前是否还有未提交提案的呢？由于前提是保证顺序提交的，所以Leader只需判断outstandingProposals里，当前ZXID的前一个ZXID是否存在，代码如下：
```
if(outstandingProposals.containsKey(zxid-1)){
    return false;
}
```

所以ZooKeeper是通过两阶段提交保证数据的最终一致性，并且通过严格的按照ZXID的顺序生效提案保证其顺序一致性的。

## leader执行commit了，还没来得及给follower发送commit的时候，leader宕机了，这个时候如何保证数据一致性？
当leader宕机以后，ZooKeeper会选举出来新的leader，新的leader启动以后要到磁盘上面去检查是否存在没有commit的消息。
如果存在，就继续检查看其他follower有没有对这条消息进行了commit，如果有过半节点对这条消息进行了ack，但是没有commit，那么新对leader要完成commit的操作。

## 那些没有收到commit消息的follower怎么最终和leader数据保持一致？
由于leader和follower的数据通信是FIFO队列，保证顺序的，所以在一下次leader请求通信之前，follower肯定要处理之前的commit请求，比如中间超时断联了 重新连接后，和leader重新同步数据，会严格按照顺序

## 客户端把消息写到leader了，但是leader还没发送proposal消息给其他节点，这个时候leader宕机了，leader宕机后恢复的时候此消息又该如何处理？
这个时候对于用户来说，这条消息是写失败的。
假设过了一段时间以后leader节点又恢复了，不过这个时候角色就变为了follower了，它在检查自己磁盘的时候会发现自己有一条消息没有进行commit，此时就会检测消息的编号，消息是有编号的，由高32位和低32位组成，高32位是用来体现是否发生过leader切换的，低32位就是展示消息的顺序的。

这个时候当前的节点就会根据高32位知道目前leader已经切换过了，所以就把当前的消息删除，然后从新的leader同步数据，这样保证了数据一致性。

## 多个客户端读到的数据不一致
zookeeper不保证读一致性，因为zookeeper写入成功的条件是一半node成功即成功，所以客户端连接到的node是失败的node则是老数据。如果要保证读到的数据是最新的，读取之前要使用sync方法，强行和主node保持一致。

## ZAB 协议下，数据不一致的情况有哪些？
1、当 leader 推送一个 commit 提交数据，刚自己提交了，但是他还没有吧 commit 提交给 follower 的时候，就已经挂掉了？

  * 众多的 follower,开始半数选举机制，选出新的 leader 之后，发现本地磁盘上有没有提交的 proposal，然后查看别的 follower 也存在这样   的情况，这里的新 leader(是之前未接收到 commit 消息的 follower),然后我们开始发送 commit 给其他 follower，将数据写入 znode 节点中 解决客户端读取数据不一致的情况；

2、请求写数据操作的 leader 机器上，然后 leader，发送一个 proposal 提议，但是还没发出去，就挂了；导致本地磁盘日志文件中存在一个 proposal；但是其他的 follower 中没有这个数据；

* 已经宕机的 leader 存在一个 proposal 的提议，但是其他的 follow 没有收到，所以在恢复模式之后，新的 leader 被选择出来，开展写操作，但是发优先去查询一下本地磁盘中是否有之前遗留的 proposal 提议，查询到一个 follower(之前宕机的发送的一个 proposal 的 leader)磁盘数据与其他的不一致，然后现在的新 leader 将会同步数据给其他的 follower，之前宕机的 leader 存在一个的提议（proposal 提议）会被舍弃掉


## Zookeeper 怎么保证主从节点的状态同步
Zookeeper 的核心是原子广播机制，这个机制保证了各个 server 之间的同步。

实现这个机制的协议叫做 Zab 协议。Zab 协议有两种模式，它们分别是恢复模式和广播模式。

### 恢复模式
在正常情况下运行非常良好，一旦 Leader 出现崩溃或者由于网络原因导致 Leader 服务器失去了与过半 Follower 的联系，那么就会进入崩溃恢复模式。为了程序的正确运行，整个恢复过程后需要选举出一个新的 Leader,因此需要一个高效可靠的选举方法快速选举出一个 Leader。

### 广播模式
类似一个两阶段提交过程，针对客户端的事务请求， Leader 服务器会为其生成对应的事务 Proposal,并将其发送给集群中的其余所有机器，再分别收集各自的选票，最后进行事务提交。

## 哪些情况会导致 ZAB 进入恢复模式并选取新的 Leader
1、集群启动，这个时候需要选举出新的Leader
2、Leader服务器宕机
3、Follow服务器宕机后，Leader服务器发现自己已经没有过半的Follow跟随自己了，不能对外提供服务（领导者选举）

## Zookeeper 有几种部署模式
Zookeeper 有三种部署模式：
* 单机部署：一台机器上运行； 
* 集群部署：多台奇迹运行；
* 伪集群部署：一台机器启动多个 Zookeeper 实例运行。

## 说一下 Zookeeper 的通知机制

client 端会对某个 znode 建立一个 watcher 事件，当该 znode 发生变化时，这些 client 会收到 zk 的通知，然后 client 可以根据 znode 变化来做出业务上的改变等。

## Watcher监听机制的工作原理
![alt](https://img-stage.yit.com/CMSRESQN/26144e42687eb7e83970728c382e9fd3_1492X1022.png)

ZooKeeper的Watcher机制主要包括客户端线程、客户端 WatcherManager、Zookeeper服务器三部分。

客户端向ZooKeeper服务器注册Watcher的同时，会将Watcher对象存储在客户端的WatchManager中。

当zookeeper服务器触发watcher事件后，会向客户端发送通知， 客户端线程从 WatcherManager 中取出对应的 Watcher 对象来执行回调逻辑。

### Watcher特性总结
* 一次性，一个Watch事件是一个一次性的触发器。一次性触发，客户端只会收到一次这样的信息。
* 异步的，Zookeeper服务器发送watcher的通知事件到客户端是异步的，不能期望能够监控到节点每次的变化，Zookeeper只能保证最终的一致性，而无法保证强一致性。
* 轻量级，Watcher 通知非常简单，它只是通知发生了事件，而不会传递事件对象内容。
* 客户端串行，执行客户端 Watcher 回调的过程是一个串行同步的过程。
* 注册watcher，用getData、exists、getChildren方法
* 触发watcher，用create、delete、setData方法

## 什么是会话 Session
指的是客户端会话，客户端启动时，会与服务器建议 TCP 链接，连接成功后，客户端的生命周期开始，客户端和服务器通过心跳检测保持有效的的会话以及发请求并响应、监听 Watch 事件等。

[Zookeeper Session机制](https://jimmy2angel.github.io/2019/01/12/Zookeeper-Session%E6%9C%BA%E5%88%B6/)

## 集群中为什么要有主节点
在分布式环境中，有些业务逻辑只需要集群中的某一台机器进行执行，其他的机器可以共享这个结果，这样可以大大减少重复计算，提高性能，于是就需要进行 leader 选举。


## lead选举
数据模型
投票信息中包含两个最基本的信息。

* sid:即server id，用来标识该机器在集群中的机器序号。
* zxid:即zookeeper事务id号。ZooKeeper状态的每一次改变, 都对应着一个递增的Transaction id, 该id称为zxid. 由于zxid的递增性质, 如果zxid1小于zxid2, 那么zxid1肯定先于zxid2发生. 创建任意节点, 或者更新任意节点的数据, 或者删除任意节点, 都会导致Zookeeper状态发生改变, 从而导致zxid的值增加.以（sid，zxid）的形式来标识一次投票信息。例如，如果当前服务器要推举sid为1，zxid为8的服务器成为leader，那么投票信息可以表示为（1，8）

规则
集群中的每台机器发出自己的投票后，也会接受来自集群中其他机器的投票。每台机器都会根据一定的规则，来处理收到的其他机器的投票，以此来决定是否需要变更自己的投票。
规则如下：
* 初始阶段，都会给自己投票。
*  当接收到来自其他服务器的投票时，都需要将别人的投票和自己的投票进行pk，规则如下：
* 优先检查zxid。zxid比较大的服务器优先作为leader。
* 如果zxid相同的话，就比较sid，sid比较大的服务器作为leader。

## 集群中有 3 台服务器，其中一个节点宕机，这个时候 Zookeeper 还可以使用吗
可以继续使用，单数服务器只要没超过一半的服务器宕机就可以继续使用。
集群规则为 2N+1 台，N >0，即最少需要 3 台。

## Zookeeper 宕机如何处理
Zookeeper 本身也是集群，推荐配置不少于 3 个服务器。Zookeeper 自身也要保证当一个节点宕机时，其他节点会继续提供服务。

如果是一个 Follower 宕机，还有 2 台服务器提供访问，因为 Zookeeper 上的数据是有多个副本的，数据并不会丢失；
如果是一个 Leader 宕机，Zookeeper 会选举出新的 Leader。

Zookeeper 集群的机制是只要超过半数的节点正常，集群就能正常提供服务。只有在 Zookeeper 节点挂得太多，只剩一半或不到一半节点能工作，集群才失效。

所以：3 个节点的 cluster 可以挂掉 1 个节点(leader 可以得到 2 票 > 1.5)2 个节点的 cluster 就不能挂掉任何1个节点了(leader 可以得到 1 票 <= 1)

## 说下四种类型的数据节点 Znode
* PERSISTENT：持久节点，除非手动删除，否则节点一直存在于 Zookeeper 上。
* EPHEMERAL：临时节点，临时节点的生命周期与客户端会话绑定，一旦客户端会话失效（客户端与 Zookeeper连接断开不一定会话失效），那么这个客户端创建的所有临时节点都会被移除。
* PERSISTENT_SEQUENTIAL：持久顺序节点，基本特性同持久节点，只是增加了顺序属性，节点名后边会追加一个由父节点维护的自增整型数字。
* EPHEMERAL_SEQUENTIAL：临时顺序节点，基本特性同临时节点，增加了顺序属性，节点名后边会追加一个由父节点维护的自增整型数字。

## zookeeper的持久化机制

和redis类似，有log和snapshot两个磁盘文件，log日志即每次写请求的数据(aof)，snapshot记录某一时刻的数据快照(rdb)。

* ZK 会持久化到磁盘的文件有两种：log 和 snapshot
* log 负责记录每一个写请求
* snapshot 负责对当前整个内存数据进行快照
* 恢复数据的时候，会先读取最新的 snapshot 文件
* 然后在根据 snapshot 最大的 zxid 去搜索符合条件的 log 文件，再通过逐条读取写请求来恢复剩余的数据
* 写请求的流程：proposal->记录log->commit->更新内存 

## 脑裂

采用主从（master-slave）架构的分布式系统中，出现了多个活动的主节点的情况。但正常情况下，集群中应该只有一个活动主节点

造成脑裂的原因主要是

1. 网络分区（这个词之前在讲CAP理论时就已经出现过了）。由于网络故障或者集群节点之间的通信链路有问题，导致原本的一个集群被物理分割成为两个甚至多个小的、独立运作的集群，这些小集群各自会选举出自己的主节点，并同时对外提供服务。网络分区恢复后，这些小集群再度合并为一个集群，就出现了多个活动的主节点。
2. 另外，主节点假死也有可能造成脑裂。由于当前主节点暂时无响应（如负载过高、频繁GC等）导致其向其他节点发送心跳信号不及时，其他节点认为它已经宕机，就触发主节点的重新选举。新的主节点选举出来后，假死的主节点又复活，就出现了两个主节点。

### 避免脑裂
法定人数/多数机制（Quorum）
  * Quorum一词的含义是“法定人数”，在ZooKeeper的环境中，指的是ZK集群能够正常对外提供服务所需要的最少有效节点数。也就是说，如果n个节点的ZK集群有少于m个节点是up的，那么整个集群就down了。m就是所谓Quorum size，并且： m = n / 2 + 1
  * ZK的Quorum机制其实就是要求集群中过半的节点是正常的，所以ZK集群包含奇数个节点比偶数个节点要更好。显然，如果集群有6个节点的话，Quorum size是4，即能够容忍2个节点失败，而5个节点的集群同样能容忍2个节点失败，所以可靠性是相同的。偶数节点还需要额外多管理一个节点，不划算。

## Zookeeper 和 Dubbo 的关系
* Dubbo 的将注册中心进行抽象，是得它可以外接不同的存储媒介给注册中心提供服务，有 ZooKeeper，Memcached，Redis 等。
* 命名服务，将树状结构用于维护全局的服务地址列表，服务提供者在启动 的时候，向 ZooKeeper 上的指定节点 /dubbo/${serviceName}/providers 目录下写入自己的 URL 地址，这个操作就完成了服务的发布。

[Zookeeper 一致性的保证](https://juejin.cn/post/7023285737139208206)
[ZooKeeper运维——数据备份与恢复](https://blog.51cto.com/stefanxfy/4722107#ZooKeeper_215)
[Zookeeper的Leader选举流程](https://www.cnblogs.com/jojop/p/14319464.html)
[Leader 与 Follower 的数据同步策略](https://learn.lianglianglee.com/%E4%B8%93%E6%A0%8F/ZooKeeper%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90%E4%B8%8E%E5%AE%9E%E6%88%98-%E5%AE%8C/16%20ZooKeeper%20%E9%9B%86%E7%BE%A4%E4%B8%AD%20Leader%20%E4%B8%8E%20Follower%20%E7%9A%84%E6%95%B0%E6%8D%AE%E5%90%8C%E6%AD%A5%E7%AD%96%E7%95%A5.md)