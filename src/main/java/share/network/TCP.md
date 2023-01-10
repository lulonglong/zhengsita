# TCP


## TCP/IP
```
		网络设备之间需要定义一些共通的东西来进行交流，TCP/IP就是为此而生。TCP/IP不是一个协议，而是一个协议族的统称。里面包括了IP协议，IMCP协议，TCP协议，以及我们更加熟悉的http、ftp、pop3协议等等
TCP/IP协议族按照层次由上到下，层层包装

应用层：满足用户间信息传递需求的应用，比如发邮件、浏览网页（HTTP等）
传输层：将信息格式化后按照指定规则发送、还原的协议，根据实际需求场景规划不同的协议，例如可靠传输（TCP、UDP等）
网络层：收到数据发送请求后，将数据装入IP数据报文，填充报头，选择去往信宿机的路径，然后将数据报发往适当的网络接口（路由器等）
网络接口层：负责接收IP数据报文并通过网络发送之，或者从网络上接收物理帧，抽出IP数据报文（交换机、网卡、集线器、网线、光纤等）
```
 ![alt](https://mmbiz.qpic.cn/mmbiz_jpg/LFP9SpGv0PFtlTQaSZvicMpnONIuPibxFibJBk8SIYDPEXQI8nWTrQSr2FhzO6U2Nebr3xG1ZfbF3WCtpFvZ5om6g/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
 ![alt](https://img-blog.csdnimg.cn/b50145ca8f6a4a2f997af74e79d077e2.png)

## 集线器、交换机、路由器
```
集线器：工作在物理层，用广播的形式把数据传播给其他机器，搭建局域网
交换机：工作在数据链路层，可以根据MAC地址发送到指定的机器，搭建局域网
路由器：工作在网络层，通过IP把数据包路由到指定的机器，用于连接局域网
```

```
		用一根网线连接在两台主机的端口，我们称之为网口，每台主机的内部有一个芯片，用来发送和接收数据，我们称之为网卡，这就是最简单的网络连接方式
```
 ![alt](https://pic2.zhimg.com/80/v2-d468b543fe858d2cab309a269e9b8f4c_1440w.webp?source=1940ef5c)
```
		两台主机通信可以直连，如果多台主机通信呢？总不能每台主机设计N个网口，这样肯定是不行的。既然无法每台主机设计多个网口，能否设计一个设备，具备多个网口，所有的主机只要连接在这个设备的网口上，不就可以通信了嘛
		集线器（HUB），将网线集结起来，实现最初级的网络互通，集线器是通过网线传送数据的，所以它工作在物理层
		集线器还有一个作用：将收到的信号放大后再传递出去，扩大网络的传输距离。网线的最大传输距离是100M，有了集线器这个中继设备，可以扩大网络传输距离
		集线器虽然可以提供多个网口和扩大传输距离，但是由于工作在物理层，它并不能分辨具体信息发给谁，只能广播出去。由于处于同一网络，当一台主机发消息时，其他主机不能发送消息，否则信息间会产生碰撞，碰撞后，数据都会粉碎，造成数据丢失
```
![alt](https://pic1.zhimg.com/80/v2-f51aff3f60f0d66c5109600027112c47_1440w.webp?source=1940ef5c)
![alt](https://pic1.zhimg.com/80/v2-00a45bc4c0cd2515e3a51e1e3178c393_1440w.webp?source=1940ef5c)
```
		交换机，在集线器原有的功能上，增加了自动寻址能力和交换作用，交换机不会识别IP地址，但它可以学习MAC地址，并把其和对应的端口存在内部地址表中，通过在发送者和接收者之间建立临时交换路径，实现数据帧直接由源地址到达目的地址
		交换机通过学习MAC地址实现转发，可以看出工作在数据链路层
```
 ![alt](https://pic3.zhimg.com/80/v2-b8b93aa2028217f07a5f325a0fea76d4_1440w.webp?source=1940ef5c)
```
		交换机默认一个广播域，这就要求交换机端口上的所有主机在同一个子网中，那么不同网段的主机要如何连接通信呢？这就是路由器的用途
		路由器（Router），连接不同类型网络并能够选择数据传送路径的设备，所以路由器工作在三层网络层也就是说，不同网段必须用路由器传输数据，否则无法通信，它是很多子网的出口，充当网关的角色，所谓网关，就是连接主机的路由器接口地址是内网地址，外网口为其他网络的地址。如果连接运营商的接口，则路由器主要功能负责让主机连接外网，地址为向运营商申请的地址
```
 ![alt](https://pic1.zhimg.com/80/v2-25d7a302e9e8a0c2c8b45201378df476_1440w.webp?source=1940ef5c)

## TCP数据包
```
		以太网数据包（packet）的大小是固定的，最初是1518字节，后来增加到1522字节。其中， 1500 字节是负载（payload），22字节是头信息（head）。
		IP 数据包在以太网数据包的负载里面，它也有自己的头信息，最少需要20字节，所以 IP 数据包的负载最多为1480字节。
		TCP 数据包在 IP 数据包的负载里面。它的头信息最少也需要20字节，因此 TCP 数据包的最大负载是 1480 - 20 = 1460 字节。由于 IP 和 TCP 协议往往有额外的头信息，所以 TCP 负载实际为1400字节左右。因此，一条1500字节的信息需要两个 TCP 数据包。HTTP/2 协议的一大改进，就是压缩 HTTP 协议的头信息，使得一个 HTTP 请求可以放在一个 TCP 数据包里面，而不是分成多个，这样就提高了速度。
```
![alt](https://mmbiz.qpic.cn/mmbiz_png/IP70Vic417DOLwOabaCOabBpZGS1DzN8Pl2jt8wUfpjFggIjH6OEOE21WCjJAkw8IZZZHuowuYSwHV3TBZ3Cyhw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

## TCP三次握手和四次挥手
```
在TCP层，数据包有个FLAGS字段，这个字段有以下几个标识：SYN(synchronous建立联机)，ACK(acknowledgement 确认)，PSH(push传送)，FIN(finish结束)，RST(reset重置)，URG(urgent紧急)。这几种标识也代表数据包的几种类型，一个包中可能会同时出现多个标识。
发送方在发数据包时会给包做一个编号，后续的数据包会占用递增编号，接收方收到数据包要做ACK操作，并表明下一个包的编号应该是几（接收到的编号+len，特殊情况是客户端、服务端握手时，len=0，此时对方就不是ack=seq+0，而是ack=seq+1）。
单纯的ACK类型的包不占用编号，虽然ACK包编号也会+1，但是下个数据包会重用这个编号。

三次握手
		三次握手的过程是TCP在客户端和服务端建立连接的过程。简单的来说三次握手过程，就是客户端先发送一个连接请求给服务端，这是第一次握手。服务端接收到客户端发来的请求，然后在将确认消息发给客户端，这是第二次握手。客户端对服务端发来的确认消息进行确认，然后将确认的消息发给服务端，这就是三次握手。三次握手之后，连接建立。
		为什么一定是三次握手才能建立一个可靠的连接？如果不是三次握手，那么在客户端发送了连接请求之后，服务端对这个请求进行确认，就认定这次的连接已经成功建立，俗称的二次握手。这样的方式的弊端在哪里？
		考虑这么一种情况，当客户端进行第一次握手时，发送了一个报文段，但是这个报文段因为网络的问题，迟迟没有到，这时，客户端又会再一次发送一个连接请求的报文段给服务端，这次成功接收，两者建立连接，并通信结束，关闭连接。这之后，因为网络延迟的那个报文段传到了服务端那里，服务端又以为客户端要建立新的连接，于是就同意了，向客户端发送确认。因为是二次握手，所以服务端后续要做的事情，就是等待客户端发送的消息，但是客户端是不会理会服务端传来的确认，所以服务端就会一直在等待客户端的数据，白白浪费了资源。

四次挥手
		首先，客户端因为应用程序的执行完毕，会主动开始断开链接，这时会发送一个含有FIN标志位的报文段。这表明客户端不会再发送数据给服务端。这是第一次挥手。
		然后，服务端接收到这个报文段，就会发送一个含有ACK标志的报文段给客户端，表示确认收到了关闭的报文段。这是第二次挥手。
		然后，服务端在处理完服务端的事情后，也会发送一个含有FIN的报文段给客户端，表示服务端不会再发送数据给客户端。这是第三次挥手。
		最后，客户端收到这个报文段后，就会发送一个含有ACK的报文段给服务端，表示确认收到了关闭的报文段。这是第四次挥手。至此，连接全部关闭
```
![alt](https://mmbiz.qpic.cn/mmbiz_jpg/j3gficicyOvasF6pzMRVlOnbpMNic3XqHBPBQInmfEBgvdVd8WDPLodCiaCIHy1ib2mSLCMUGrf3pTric60MJeIzHlbg/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

```
第一次握手，客户端发送一个报文段给服务端，该报文段中标志有SYN标志，该标志表示建立连接，以及一个初始的序列号
```
 ![alt](http://mmbiz.qpic.cn/mmbiz_jpg/XCETLoXzTr8FN5KnurIfrycKglWJ1Itib6JFtgIdiaN2zeU9QnAZtqcopDlPo1FbQDgrakEib1AgTcgVFewhkm5rA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

```
第二次握手时，服务端发送一个报文段给客户端，该报文段中标志有SYN标志，和ACK标志。ACK标志的值是客户端发来的初始序号值+ 1，表示对客户端进行确认，报文段中还有服务端自己的初始序号
```
 ![alt](http://mmbiz.qpic.cn/mmbiz_jpg/XCETLoXzTr8FN5KnurIfrycKglWJ1ItibUYC6hR3t9TdCY9zebFrSOib4H2P5UMnoFdpCerTmWv5eEbxAr0TaaPA/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

```
第三次握手时，客户端发送一个报文段给服务端，该报文段中标志只有ACK标志，该ACK值是服务端的初始序列化的值 + 1，表示对服务端进行确认，以及还有一个序号值，该序号值为客户端第一次握手时的序号值 + 1。
三次握手建立连接结束
```
 ![alt](http://mmbiz.qpic.cn/mmbiz_jpg/XCETLoXzTr8FN5KnurIfrycKglWJ1ItibLy1GWUs6zObmxLhenz2T5CCHx0YEUKH6vdGq37E9KB2icWOgHDIAZibw/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)

**QA**
```
在建立连接的时候，初始序列号是随机产生的。如果不这样，A:p1=>B:p2上个连接丢失的包。可能在A:p1=>B:p2下个连接的时候正好到达，序列号正好在相同的范围内会引起误读，初始随机的话会减小碰撞的概率。但是从上图中可以看到，seq是从0开始的，这个seq是软件为了增强可读性刻意为之，括号中标记了是本次连接的相对序号，实际上还是一个随机的值。
```




## 数据包的遗失处理

```
TCP 协议可以保证数据通信的完整性，这是怎么做到的？

前面说过，每一个ACK数据包都带有下一个要接收的数据包的编号。如果下一个数据包没有收到，那么 ACK 的编号就不会发生变化。

举例来说，现在收到了4号包，但是没有收到5号包。ACK 就会记录，期待收到5号包。过了一段时间，5号包收到了，那么下一轮 ACK 会更新编号。如果5号包还是没收到，但是收到了6号包或7号包，那么 ACK 里面的编号不会变化，总是显示5号包。这会导致大量重复内容的 ACK。

如果发送方发现收到三个连续的重复 ACK，或者超时了还没有收到任何 ACK，就会确认丢包，即5号包遗失了，从而再次发送这个包。通过这种机制，TCP 保证了不会有数据包丢失。
```
 ![alt](https://mmbiz.qpic.cn/mmbiz_png/IP70Vic417DOLwOabaCOabBpZGS1DzN8PfiavfHxkbZjbed4fSLzkmEKzbic7ZHnyBXAygTd3Mm21K8t23jzUCLmg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)


## 慢热启动特性
```
		慢启动体现了一个试探的过程，刚接入网络的时候先发包慢点，探测一下网络情况，然后在慢慢提速。不要一上来就拼命发包，这样很容易造成链路的拥堵，出现拥堵了在想到要降速来缓解拥堵这就有点成本高了，毕竟无数的先例告诫我们先污染后治理的成本是很高的
```

## 延迟确认机制
```
当发送没有携带数据的 ACK，它的网络效率也是很低的，因为它也有 40 个字节的 IP 头 和 TCP 头，但却没有携带数据报文。
为了解决 ACK 传输效率低问题，所以就衍生出了 TCP 延迟确认。

TCP 延迟确认的策略：
1.当有响应数据要发送时，ACK 会随着响应数据一起立刻发送给对方
2.当没有响应数据要发送时，ACK 将会延迟一段时间，以等待是否有响应数据可以一起发送
3.如果在延迟等待发送 ACK 期间，对方的第二个数据报文又到达了，这时就会立刻发送 ACK
```
 ![alt](https://mmbiz.qpic.cn/mmbiz_png/J0g14CUwaZeCfmfFfz8T6ngiafWRdcLtqoexLQeRzR1uwXIKrH3jJ9b0NNcrbxEKo7uvPp3eZLiafZkVfiaiaibVulg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

## 参考文档
[TCP协议详解](https://mp.weixin.qq.com/s?src=3&timestamp=1664520212&ver=1&signature=ON8SvFlblOuDaTEuLb6-TxryhkbaExU89r6H*VH*fmErCgOCLVQdUz52sTT-QIRkPbS1rXS*sINRQ*10d95*rfhsjdneqsJZSSBik36lPOnuHa2-blh7RIFD38V3hCqyn7SlT5tUrfKpXhaBf-2za5BjKLGeY98BXQCSeumnjCc=)
[彻底弄懂TCP协议：从三次握手说起](https://mp.weixin.qq.com/s?src=11&timestamp=1664520212&ver=4075&signature=Ecw*byLNc8ExNzOfgOM0R4R-wc*eP0EzG6NcBSPECSjq3e1Z54veOTL7DCBafy0KOO9zbkoHcLVfe1moM5Od8XAa9VFAm9Xs6qDk7QUIJnE0wLfnnRqHLyiouDkEgn0Q&new=1)
[图文并茂，5分钟让你搞懂TCP协议](https://mp.weixin.qq.com/s?src=11&timestamp=1664520212&ver=4075&signature=lnTW0bztVjhU-AdDXYpgj3ZQE6zd*iqNTkqSEMWFLGIs0mF3jLP9adJwKvM6pgDTyg5BhwPF8nKmLlZzNsCnx7W5FDs8d5Zy0VQsP0BGGSvRark4MdG-jwWss3lE2ZQZ&new=1)
[互联网协议入门（一）](https://www.ruanyifeng.com/blog/2012/05/internet_protocol_suite_part_i.html)
[互联网协议入门（二）](https://www.ruanyifeng.com/blog/2012/06/internet_protocol_suite_part_ii.html)
