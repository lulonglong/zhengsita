# 架构
1. mysql架构是怎样的
![alt](https://picx.zhimg.com/80/v2-24438b709c82559e03b59e17027a34d6_720w.webp?source=1940ef5c)
![alt](https://picx.zhimg.com/80/v2-78b5ae7f89cb277a30e6f494a953fe96_720w.webp?source=1940ef5c)

2. myisam和innodb有什么区别
```
1. InnoDB支持事务，MyISAM不支持
2. 索引存储上的区别，InnoDB的主键索引存储数据，MyISAM的索引全部存储的是行数据地址。可以理解为MyISAM不支持聚簇索引
3. InnoDB不保存表的具体行数，执行select count(*) from table时需要全表扫描。而MyISAM用一个变量保存了整个表的行数，执行上述语句时只需要读出该变量即可，速度很快
		※3.1为什么innodb不能存储表行数？
			因为InnoDB的事务特性，在同一时刻表中的行数对于不同的事务而言是不一样的，因此count统计会计算对于当前事务而言可以统计到的行数，而不是将总行数储存起来方便快速查询
4. InnoDB支持表、行(默认)级锁，而MyISAM支持表级锁
5. InnoDB表必须有主键（用户没有指定的话会自己找或生产一个主键），而Myisam可以没有
6. 5.5版本开始（2010年），mysql的默认存储引擎从myisam改为innodb
```

3. myisam看上去如此不堪，它有什么相对优势
```
设计相对简单，不需要考虑事务和行锁相关检测，单纯查询大多会快一点
```

4. 数据页是什么
```
为了避免一条一条读取磁盘数据，InnoDB采取页的方式，作为磁盘和内存之间交互的基本单位，一个页的大小一般是16KB。我们往 MySQL 插入的数据最终都是存在页中的。在 InnoDB 中的设计中，页与页之间是通过一个双向链表连接起来
```
![alt 数据页](https://mmbiz.qpic.cn/mmbiz_png/AZHyCoMMOC8wv6zgWc2pJM6QFTqWAfzBNATXjz9ZqFBxaTDC6ZvjI64dFicA1xwdnWfVWG3QMj1w4xkchCvcPEg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

5. 缓冲池（Buffer Pool）是什么
```
Buffer Pool 是一个内存区，存的就是一页一页的数据。读取数据时先从 Buffer Pool 中找，没有命中再去硬盘加载，减少硬盘 IO 开销，提升性能。当写数据时也是更改Buffer Pool而不是直接改磁盘，后续再进行刷盘操作。
```

------
# 索引

1. 为什么需要索引
```
为了减少逐行扫描，加快查询速度
```
2. innodb索引的数据结构
```
B+树
```
3. 为什么选用B+树
```
为什么选用B类树而不是二叉树，因为B类树是多叉的，减少了树的层级，减少磁盘IO次数
为什么选用B+树而不是B树，B+树的叶子结点冗余了所有索引key，每个节点间都有首尾引用，方便范围查找
```
4. myisam和innodb在索引存储上的区别
```
在文件存储上myisam把索引文件和数据文件分开的，innodb是合在一起的。innodb的主键索引即数据，myisam的主键索引就只是索引，指向的数据行地址。myisam根据索引查到的是数据的地址，innodb的二级索引查到的是主键
```
5. hash索引
```
memory存储引擎支持的索引
对索引列做hash，直接拿hash值做匹配
缺点是范围查找不支持了
```
6. 二级索引的非叶子结点内有无主键id？
```
也有，但是即便在非叶子节点正好碰到索引，也会继续查询到叶子结点才返回，目的是让每次查询的性能都是稳定的，方便性能评估
```
7. 一级（聚簇、主键）索引和二级（非聚簇、辅助）索引
```
主键是一级索引、其他的是二级索引
```
8. 使用索引需要注意什么
```
往被查询和排序的字段上加
要建索引的数据类型尽量小,索引值过长时使用前缀索引
列值要尽量稀疏分散
非频繁改动
切勿过度索引
尽量不为null
表记录极少的情况不要建索引
```
9. 什么是回表
```
当使用二级索引匹配到记录后，再根据主键值到一级索引中查找，这个过程叫回表
查询二级索引是顺序查找，回表是随机查找。如果回表的记录太多就意味着有太多的随机IO，这个时候查询优化器可能直接选择全表扫描而不使用索引
```
10. 没有给表显式设置主键时innodb是怎么处理
```
先找一个非NULL的唯一索引，否则用6Byte 的自增主键
```
11. 索引失效的情况
```
回表过多时，优化器选择不使用索引
前缀模糊，不符合最左匹配
参与表达式运算、函数计算
如果or前边的列有索引，后面的列没有索引，那么涉及到的索引都不会被用到
负向条件：!=、、not in、not exists、not like等
隐式数据类型转换。例如status是varchar类型，查询时用status=1
is null 可以使用索引，is not null无法使用索引
查询的结果集，超过了总数行数25%，优化器觉得就没有必要走索引了
```
12. 联合/组合索引的匹配原则
```
多列按指定顺序作为一个索引叫组合索引
最左匹配原则
```
13. 组合索引失效的情况
```
第一个key未出现（完全失效）
第一个key出现了，某一个key没出现，则后续key不管出不出现都不走索引（部分失效）
出现某个key是范围查询的，则后续key不管出不出现都不走索引（部分失效）
某一个key出现索引失效的情况，后续key都不走索引（部分或全部失效）
```
14. 索引下推是什么
```
索引下推是 MySQL 5.6 及以上版本推出的，目的是减少回表次数
建立了A、B、C组合索引，A生效、B导致失效、C虽然有值但不能走索引，在组合索引部分失效的情况下，之前方案是回表再比较C和B。索引下推是命中A索引的Key时，直接比较B、C的值，减少回表次数
```
15. 覆盖索引是什么
```
innodb 1.0以后的版本实现的
如果一个索引包含（或者说覆盖）所有需要查询的字段，就称之为“覆盖索引”。在 InnoDB 中，如果不是主键索引，叶子节点存储的是主键+列值。最终还是要“回表”，也就是要通过主键再查找一次。这样就会比较慢，覆盖索引就是索引中包含要查询的列，不做回表操作！
```
16. merge index是什么
```
5.1版本之前一个查询只能使用一个索引，5.1开始可以同时使用多个二级索引。先通过索引取出主键整合后再回表
```
17. 前缀索引，优缺点
```
减少了长度，增加了匹配速度，减少磁盘页的占用。
不能达到覆盖索引的效果，因为索引值是不完整的
```
18. 全文索引
```
全文索引与普通的索引不是一回事，在查找方面其效率是普通模糊（like）查询的N倍，是MySQL专门提供用作搜索引擎的
Mysql 5.6之前版本，只有MyISAM支持全文索引，5.6之后，Innodb和MyISAM均支持全文索引
只有字段的数据类型为 char、varchar、text才可以建全文索引
底层实现原理也是倒排索引
ALTER TABLE table_name ADD FULLTEXT (column1)
select * from table_name where MATCH(column1,column2) AGAINST('Lenovo')
```
19. 索引分裂是怎么发生的
```
当索引B+树节点的key数量超过数据页的容量就会分裂。一般是按照50%的分裂法则，索引单方向递增插入的时候会触发走优化策略，老页面不动或者基本不动，新数据进入新页面
```
20. 索引提示
```
use index：建议用某个索引，但是mysql不一定会采用
force index：强制使用某个索引
ignore index：ignore index告诉mysql不要使用某些索引去做本次查询
use index FOR JOIN：索引提示用于查找行或者用于表的连接
use index FOR ORDER BY：索引提示用于排序
use index FOR GROUP BY：索引提示用于分组
以上关键字使用时都是跟在表名后边，https://www.cnblogs.com/jkin/p/12868434.html
```
21. 唯一索引能存在多个null吗
```
能
```
22. explain性能分析
```
在select语句之前增加explain关键字，执行后MySQL就会返回执行计划的信息，而不是执行sql。但如果from中包含子查询，MySQL仍会执行该子查询，并把子查询的结果放入临时表中

各列含义如下：

id： id列的编号是select的序列号，有几个select就有几个id，并且id是按照select出现的顺序增长的，id列的值越大优先级越高，id相同则是按照执行计划列从上往下执行，id为空则是最后执行

select_type：表示简单查询还是复杂查询
		1）simple：简单查询，不包含子查询和union里的简单查询
		2）primary：复杂查询中最外层的select
		3）subquery：包含在select中的子查询（不在from的子句中）
		4）derived：包含在from子句中的子查询。mysql会将查询结果放入一个临时表中，此临时表也叫衍生表
		5）union：在union中的第二个和随后的select，UNION RESULT为合并的结果

table：表示当前行访问的是哪张表。当from中有子查询时，table列的格式为<derivedN>，表示当前查询依赖id=N行的查询，所以先执行id=N行的查询。当有union查询时，UNION RESULT的table列的值为<union1,2>，1和2表示参与union的行id

partitions：查询将匹配记录的分区。 对于非分区表，该值为 NULL

type(重要)：查询类型。依次从最优到最差分别为：NULL > system > const > eq_ref > ref > range > index > all。
		1）NULL：MySQL能在优化阶段分解查询语句，在执行阶段不用再去访问表或者索引。例如explain select 1;explain select min(id) from crm_black_list;
		2）system：表示表里只有一条数据
		3）const：使用主键或者唯一索引
		4）eq_ref：join表时使用了主键或者唯一索引
		5）ref：使用普通索引
		6）range：范围查找，不管是命中索引还是未命中索引
		7）index：直接扫描二级索引，a是索引列，explain select a from t limit 100;
		8）ALL：全表扫描

possible_keys：此列显示在查询中可能用到的索引。如果该列为NULL，则表示没有相关索引

key：此列显示MySQL在查询时实际用到的索引。在执行计划中可能出现possible_keys列有值，而key列为null，这种情况可能是表中数据不多，MySQL认为索引对当前查询帮助不大而选择了全表查询。如果想强制MySQL使用或忽视possible_keys列中的索引，在查询时可使用force index、ignore index

key_len：此列显示MySQL在索引里使用的字节数，通过此列可以算出具体使用了索引中的那些列。索引最大长度为768字节，当长度过大时，MySQL会做一个类似最左前缀处理，将前半部分字符提取出做索引。当字段可以为null时，还需要1个字节去记录
		char(n)：n个数字或者字母占n个字节，汉字占3n个字节
		varchar(n)：  n个数字或者字母占n个字节，汉字占3n+2个字节。+2字节用来存储字符串长度
		tinyint：1字节     smallint：2字节        int：4字节         bigint：8字节
		date：3字节        timestamp：4字节       datetime：8字节
		
ref：此列显示索引筛选的条件，是常量或者列。where a=1，匹配值就是1；on t1.a=t2.b，匹配列就是t2.b

rows：此列是MySQL在查询中估计要读取的行数。注意这里不是结果集的行数

Extra(重要)：此列是一些额外信息。常见的重要值如下
		1）Using index：使用了覆盖索引，只要使用了覆盖索引就出现，不管其他的
		2）Using index condition：这个是指用到了索引下推（参见索引下推），非ref访问方法，例如range还会算作索引下推条件再在引擎层比较一遍，只要使用了索引下推就会出现，不管其他的
		3）Using where：代表在server层需要做过滤才能筛选出数据。这里涉及到server层和存储引擎层的职责，server层在执行sql时会把sql分解成执行计划去调用存储引擎的api，我们提交的一条sql，实际上并不是全部由存储引擎层完整执行完毕。而是把索引查询（包含全表扫描）交给存储引擎层，数据返回给server层之后，server层再根据条件过滤，由server层再过滤就是Using where的含义。using index;using where会同时出现，目前的理解是非唯一索引返回给server的数据会在server层再次过滤，不一定对，需要再深入研究
		4）Using temporary：MySQL需要创建一张临时表来处理查询。出现这种情况一般是要进行优化的
		5）Using filesort：将使用外部排序而不是索引排序，数据较小时从内存排序，否则需要在磁盘完成排序
		6）Select tables optimized away：使用某些聚合函数（比如 max、min）来访问存在索引的某个字段时
```

------

# 事务
1. 如何理解事务，以及ACID特性

- 原子性(Atomicity)
  - 指一个事务是一个不可分割的工作单位，其中的操作要么都做，要么都不做。mysql通过undo log实现
- 隔离性(Isolation)
  - 多个事务并发执行的时候，事务内部的操作与其他事务是隔离的。隔离性有不同的隔离等级要求，mysql通过锁和MVCC机制实现
- 持久性(Durability)
  - 事务一旦提交，它对数据的改变就应该是永久性的。mysql通过redo log实现
- 一致性(Consistency)
  - 指事务执行前后，数据处于一种符合逻辑/合法的状态。这里的逻辑根据业务场景来定义，是指业务上的正常逻辑  
  - 一致性是事务的终极目标，mysql通过原子性、隔离性、持久性来保证数据一致性，AID是达成一致性的手段  
    举例：A账户有200元，转账300元出去，此时A账户余额为-100元。此时数据是不合逻辑的，这就是不一致性，为什么呢？因为你定义了一个状态，余额不能小于0。不但需要数据库层面做到一致性，逻辑层面也要一致，才能最终保证事务的一致性。 数据库保证一致性，不代表整个业务也会一致性，数据库没有一致性，上层业务就没有一致性可言

2. 事务隔离级别
```
- 读未提交（READ UNCOMMITTED）
- 读已提交 （READ COMMITTED）（我们生产环境的级别）
- 可重复读 （REPEATABLE READ）（mysql默认级别）
- 串行化 （SERIALIZABLE）
SQL标准中不同隔离级别的问题(Innodb里的可重复读解决了幻读问题)
```
![alt 不同隔离级别的问题](https://pic4.zhimg.com/80/v2-2e1a7203478165890e2d09f36cb39857_1440w.jpg)

3. 什么是记录锁(Record Lock)
```
记录锁也属于行锁中的一种，只不过记录锁的范围只是表中的某一条记录，事务在加锁后仅锁住表的一条记录（注意：只锁一条）  
查询条件必须命中唯一索引才能有记录锁
```

4. 什么是间隙锁(Gap Lock)
```
间隙锁属于行锁中的一种，间隙锁是在事务加锁后其锁住的是表记录的某一个区间，当表的相邻ID之间出现空隙则会形成一个区间  
查询条件必须命中索引、间隙锁只会出现在REPEATABLE_READ（可重复读)的事务级别中，防止幻读问题
```

5. 什么是临键锁(Next-Key Lock)
```
临键锁也属于行锁的一种，并且它是INNODB的行锁默认算法，总结来说它就是记录锁和间隙锁的组合，临键锁会把查询出来的记录锁住，同时也会把该范围查询内的所有间隙空间也会锁住，再之它会把相邻的下一个区间也会锁住。在可重复读中出现
select * from user_info where id>1 and id<=9 for update ;  
会锁住ID为 1,5,9的记录；同时会锁住，1至5,5至9,9至11的区间
```

 ![alt 各种行锁](https://mmbiz.qpic.cn/mmbiz_jpg/55HPQyguvpMjiaoJ2Quu9UPMoKdX5z9aupQuiaGicPz8qWdq0ddjUZTWe5MmjqKL5CL9R5mLNe8X5mLhcibibqRGpKQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

6. 什么是意向锁
```
基于锁的属性分类：共享锁、排他锁  
基于锁的粒度分类：表锁、行锁。记录锁、间隙锁、临键锁（后三者属于行锁的不同实现）  
基于锁的状态分类：意向共享锁、意向排它锁

把意向锁划分为状态锁的一个核心逻辑，是因为这两个锁都是描述是否可以对某一个表进行加表锁的状态，状态锁要锁的对象是表（很重要）
当事务A对行数据加写锁时，会先对表加意向写锁。事务B对表加锁时，发现表有意向写锁，不能加表锁。
引入意向锁的目的是为了解决，在有行锁的情况下再加表锁，防止逐行扫描之后才发现有一行被锁住了而中途放弃。提高对表加锁时的性能，避免一行一行地去遍历这张表的数据有没有被锁住
```
下图的锁都代表的是表锁
![alt 锁冲突表](https://pic4.zhimg.com/80/v2-37761612ead11ddc3762a4c20ddab3f3_1440w.jpg)

7. 不同隔离级别的实现方式
```
读未提交：不加任何锁
读已提交&可重复读：基于锁的并发控制LBCC（读写阻塞），基于多版本的并发控制MVCC（读写不阻塞）
串行化：加表锁，读的时候加共享锁，可以并发读。写的时候加排它锁，写的时候不允许读写
```

8. 什么是ReadView（活跃事务列表）
```
ReadView是事务开启时，当前所有活跃事务（还未提交的事务）的一个集合，ReadView数据结构决定了不同事务隔离级别下数据的可见性

有了readview，在访问某条记录时，按照以下步骤判断记录的某个版本是否可见(以可重复读举例，可重复读是开启事务时生成ReadView，读已提交是在每次执行sql的时候生成ReadView)
1. 如果被访问版本的trx_id，与readview中的row_trx_id值相同，表明当前事务在访问自己修改过的记录，该版本可以被当前事务访问；
2. 如果被访问版本的trx_id，小于readview中的min_trx_id值，表明生成该版本的事务在当前事务生成readview前已经提交，该版本可以被当前事务访问；
3. 如果被访问版本的trx_id，大于或等于readview中的max_trx_id值，表明生成该版本的事务在当前事务生成readview后才开启，该版本不可以被当前事务访问；
4. 如果被访问版本的trx_id，值在readview的[min_trx_id和max_trx_id)之间，就需要判断trx_id属性值是不是在m_ids列表中？
  - 如果在：说明创建readview时生成该版本的事务还是活跃的，该版本不可以被访问
  - 如果不在：说明创建readview时生成该版本的事务已经被提交，该版本可以被访问；生成readview时机，RC隔离级别：每次读取数据前，都生成一个readview；RR隔离级别：在第一次读取数据前，生成一个readview；
```
 ![alt readview数据结构](https://pic1.zhimg.com/80/v2-ba01322104c8b77f2ccd56459f351054_720w.jpg)

9. 快照读和当前读的区别
```
在MVCC中，在一个事务的执行过程中，有两种生成ReadView的时机。在开启事务后的第一条sql就生成叫做快照读，后续所有的读操作读到的记录都定格到了那一刻，像是对数据做了个快照。另一种情况，在每条sql查询时都生成ReadView，叫做当前读，每次读都可以读到当前可读的最新数据，所以叫做当前读。
```

------

# 其他
1. NULL值的处理
```
NULL的意义是无值，所以不能用=、!=等判断，NULL占存储空间

在对统计索引列不重复值的数量时如何对待NULL值。innodb提供了innodb_stats_method参数可以配置NULL在二级索引里的统计方式：
		1）nulls_equal：认为所有NULL值都是相等的。这个值也是innodb_stats_method的默认值。如果某个索引列中NULL值特别多的话，这种统计方式会让优化器认为某个列中平均一个值重复次数特别多，所以倾向于不使用索引进行访问。
		2）nulls_unequal：认为所有NULL值都是不相等的。如果某个索引列中NULL值特别多的话，这种统计方式会让优化器认为某个列中平均一个值重复次数特别少，所以倾向于使用索引进行访问。
		3）nulls_ignored：直接把NULL值忽略掉。
在MySQL 5.7.22以后的版本，对这个innodb_stats_method的修改不起作用，MySQL把这个值在代码里写死为nulls_equal

聚合函数会忽略NULL，在使用count时尤其要注意

NULL参与运算会导致结果变为NULL
```

2. binlog（归档日志）和relay log（中继日志）是什么以及区别
```
binlog记录了dml和ddl语句，以二进制的形式保存，常用于主从复制和数据恢复
主从复制时，从服务器I/O线程将主服务器的binlog记录读取过来记录到从服务器本地文件（relay-log），然后SQL线程会读取relay-log日志的内容并应用到从服务器，从而使从服务器和主服务器的数据保持一致
```
![alt](https://img-blog.csdnimg.cn/20210113222241834.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2phdmFhbmRkb25ldA==,size_16,color_FFFFFF,t_70)

3. 什么是脏页
```
当内存数据页和磁盘数据页上的内容不一致时,我们称这个内存页为脏页
```

4. redolog和undolog分别是什么，以及在事务中分别起到什么作用
```
redolog防止在发生故障的时间点，尚有脏页未写入磁盘，确保事务的持久性。redolog要落磁盘
undolog保存了事务发生之前的数据的一个版本，可以用于回滚，保证了事务的原子性。undolog也在数据页中，被当成数据落盘到undo表空间
```

5. binlog的日志格式有哪些
```
> STATMENT：基于SQL语句的复制 (statement-based replication, SBR)，每一条会修改数据的 sql 语句会记录到binlog中。
   - 优点：不需要记录每一行的变化，减少了binlog日志量，节约了IO, 从而提高了性能
   - 缺点：在某些情况下会导致主从数据不一致，比如执行sysdate()、slepp()等

> ROW（默认设置）：基于行的复制 (row-based replication, RBR)，不记录每条 sql 语句的上下文信息，仅需记录哪条数据被修改了
   - 优点：不会出现某些特定情况下的存储过程、或 function、或 trigger 的调用和触发无法被正确复制的问题
   - 缺点：会产生大量的日志，尤其是alter table的时候会让日志暴涨

> MIXED：基于STATMENT和ROW两种模式的混合复制 (mixed-based replication, MBR)，一般的复制使用STATEMENT模式保存binlog，对于STATEMENT模式无法复制的操作使用ROW模式保存binlog
```

6. 了解Online DDL吗
```
https://www.cnblogs.com/rayment/p/7762520.html
```

7. char和varchar，text的区别，是否有长度限制
```
char长度固定，即每条数据占用等长字节空间；适合用在身份证号码、手机号码等定。
varchar可变长度，可以设置最大长度；适合用在长度可变的属性。
text不设置长度，当不知道属性的最大长度时，适合用text。

>char：char(n)中的n表示字符数，最大长度是255个字符； 如果是utf8编码方式，那么char类型占255 * 3个字节。（utf8下一个字符占用1至3个字节）

>varchar：varchar(n)中的n表示字符数，最大空间是65535个字节，存放字符数量跟字符集有关系；varchar实际范围是65532或65533字节， 因为内容头部会占用1或2个字节保存该字符串的长度；如果字段default null（即默认值为空），整条记录还需要1个字节保存默认值null。如果是utf8编码，那么varchar最多存65532/3 = 21844个汉字。
※注意：以上只是理论计算，实际情况是65535个字节是整行的最大空间，也就是所有字段加起来不能超过这么大，另外编码不同字符数也不一样，如果是mb4编码就只有16383字符
     MySQL5.0.3以前版本varchar(n)中的n表示字节数；
     MySQL5.0.3以后版本varchar(n)中的n表示字符数

>text跟varchar基本相同，理论上最多保存65535个字符，实际上text占用内存空间最大也是65535个字节；考虑到字符编码方式，一个字符占用多个字节，text并不能存放那么多字符； 跟varchar的区别是text需要2个字节空间记录字段的总字节数。
PS： 由于varchar查询速度更快，能用varchar的时候就不用text
```

8. 有哪几种常用的join操作，join默认是哪种
```
交叉连接（CROSS JOIN）：使两张表的所有字段直接进行笛卡尔积，假设表1有m条数据，表2有n条数据，则结果数量为m*n条。没有条件约束
内连接（INNER JOIN）：只把满足ON条件的数据相连接，INNER可以省略不写，JOIN代表的就是INNER JOIN
左外连接（LEFT JOIN）：在内连接的基础上，把左表中不满足ON条件的数据也显示出来，但结果中的右表部分中的数据为NULL
右外连接（RIGHT JOIN）：与左外连接相反
全连接（FULL/OUTER JOIN）：左右外连接的结合体，mysql并不支持
```

9. UNION与UNION ALL的区别
```
UNION ALL 只会把多个查询结果整在一起返回，不做处理
UNION 把多个查询结果整在一起，做去重后返回
>这两个语句后可以直接跟limit
(select `status` from `crm_plan` where id>1000 limit 10)
union all
(select `status` from `crm_plan` where id>1000 limit 10) limit 1;
>如果不加括号，最后一个limit是限制的聚合结果数量而不是最后一个查询语句的返回数量
select `status` from `crm_plan` where id>1000 limit 10
union all
select `status` from `crm_plan` where id=1442 limit 100;
```

10. 知道redolog的两阶段提交吗？
```
对于Mysql Innodb存储引擎而言，每次修改提交后，不仅需要记录Redo log还需要记录Binlog，而且这两个操作必须保证同时成功或者同时失败，否则就会造成数据不一致。为此Mysql引入两阶段提交。

1、如果先写redolog再写binlog
```
 ![alt](https://pic3.zhimg.com/80/v2-890a0eb00ebdd76484b44c7c03a8e2b2_720w.webp)
```
2、先写binlog再写redolog
```
 ![alt](https://pic4.zhimg.com/80/v2-e5b058c94f2afdc2ed662b09efbda7cf_720w.webp)
```
3、两阶段提交
```
 ![alt](https://pic1.zhimg.com/80/v2-efebf5ba1e24924a4e1bb6b31ec4d6ec_720w.webp)

11. 如何存储表情符号等特殊字符
```
库表以及连接字符串编码采用utf8mb4即可
MySQL在5.5.3之后增加了这个utf8mb4的编码，mb4就是most bytes 4的意思，专门用来兼容四字节的unicode。
```

12. update的返回值代表啥
```
1.当数据库的url中没有“useAffectedRows=true”参数时，返回匹配行数（默认）
2.当数据库的url中有“useAffectedRows=true”参数时，返回影响行数
```

# 高级版（后续）

redolog的刷盘机制是怎样的（参见技术分享）
binlog的刷盘机制（参见技术分享）
undolog的工作机制（参见技术分享）
页的进一步详解和碎片清理（参见技术分享）
为什么mysql的默认隔离级别是可重复读（参见技术分享）？
死锁相关的
加锁的过程
主从复制https://zhuanlan.zhihu.com/p/533187002
如何理解 MySQL 的边读边发
MySQL 临时表的用法和特性



# 参考资料
[一灯架构](https://zhuanlan.zhihu.com/p/537258392)
[MySQL-Explain详解](https://blog.csdn.net/fsdfkjai/article/details/121770629)
[MySQL explain，Extra分析](https://www.cnblogs.com/myseries/p/11262054.html)
[2022年完全最全MySQL讲解](https://zhuanlan.zhihu.com/p/494715838)
[MySQL 页完全指南——浅入深出页的原理](https://www.jianshu.com/p/e947d7d90747)
