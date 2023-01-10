# Docker是什么

## 1. 运行环境的一致性问题
```
软件开发最大的麻烦事之一，就是环境配置

用户必须保证两件事：操作系统的设置，各种库和组件的安装。只有它们都正确，软件才能运行。举例来说，安装一个 Java 应用，计算机必须有Java虚拟机，还必须有各种依赖，可能还要配置环境变量

如果某些老旧的模块与当前环境不兼容，那就麻烦了。开发者常常会说："它在我的机器可以跑了"（It works on my machine），言下之意就是，其他机器很可能跑不了

环境配置如此麻烦，换一台机器，就要重来一次，旷日费时。很多人想到，能不能从根本上解决问题，软件可以带环境安装？也就是说，安装的时候，把原始环境一模一样地复制过来
```

### 1.1 虚拟机技术
```
虚拟机（virtual machine）就是带环境安装的一种解决方案。它可以在一种操作系统里面运行另一种操作系统，比如在 Windows 系统里面运行 Linux 系统。应用程序对此毫无感知，因为虚拟机看上去跟真实系统一模一样，而对于底层系统来说，虚拟机就是一个普通文件，不需要了就删掉，对其他部分毫无影响。
```
- 资源占用多
  - 虚拟机会独占一部分内存和硬盘空间。它运行的时候，其他程序就不能使用这些资源了。哪怕虚拟机里面的应用程序，真正使用的内存只有 1MB，虚拟机依然需要几百 MB 的内存才能运行

- 冗余步骤多
  - 虚拟机是完整的操作系统，一些系统级别的操作步骤，往往无法跳过，比如用户登录

- 启动慢
  - 启动操作系统需要多久，启动虚拟机就需要多久。可能要等几分钟，应用程序才能真正运行

![alt 虚拟机](https://mmbiz.qpic.cn/mmbiz_png/8g3rwJPmya2uXNv7y1PGhAFnicqibjhxVfPhwXoLQ67CnLtau9InwNYmY3YBHmHiaXLCcAyUOlEc54kwJJy20WwGQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 1.2 容器技术
```
由于虚拟机存在这些缺点，Linux 发展出了另一种虚拟化技术：Linux 容器（Linux Containers，缩写为 LXC）

Linux 容器不是模拟一个完整的操作系统，而是对进程进行隔离。或者说，在正常进程的外面套了一个保护层。对于容器里面的进程来说，它接触到的各种资源都是虚拟的，从而实现与底层系统的隔离

由于容器是进程级别的，相比虚拟机有很多优势
```
- 启动快
   - 容器里面的应用，直接就是底层系统的一个进程，而不是虚拟机内部的进程。所以，启动容器相当于启动本机的一个进程，而不是启动一个操作系统，速度就快很多

- 资源占用少
   - 容器只占用需要的资源，不占用那些没有用到的资源；虚拟机由于是完整的操作系统，不可避免要占用所有资源。另外，多个容器可以共享资源，虚拟机都是独享资源

- 体积小
   - 容器只要包含用到的组件即可，而虚拟机是整个操作系统的打包，所以容器文件比虚拟机文件要小很多

![alt ](https://mmbiz.qpic.cn/mmbiz_png/8g3rwJPmya2uXNv7y1PGhAFnicqibjhxVfFZic9eibC3h4HFiaexZ5GsCT1tq0x29ujlFArWyd2OUy25u49S2GxbKibA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 1.3 docker
```
Docker 属于 Linux 容器的一种封装，提供简单易用的容器使用接口。它是目前最流行的 Linux 容器解决方案

Docker 将应用程序与该程序的依赖，打包在一个文件里面。运行这个文件，就会生成一个虚拟容器。程序在这个虚拟容器里运行，就好像在真实的物理机上运行一样。有了 Docker，就不用担心环境问题

总体来说，Docker 的接口相当简单，用户可以方便地创建和使用容器，把自己的应用放入容器。容器还可以进行版本管理、复制、分享、修改，就像管理普通的代码一样
```

![alt ](http://mmbiz.qpic.cn/mmbiz_png/Hia4HVYXRicqEeM5mjUiaIq7PqdKT1lQicapoyHBLrBBLFSb7aJooFryX0dZlAiaIiaQ0pFjCNK4OnNWu96PYjtibfaoA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

## 2. docker架构

![alt ](https://pic1.zhimg.com/v2-527e3be84dc0fbfaa7d2868abf8cbac9_1440w.jpg?source=172ae18b)

```
registry相当于APP商店，image相当于APP安装包，container相当于安装好的APP，docker daemon相当于JRE
```

### 2.1 image镜像

```
Docker 把应用程序及其依赖，打包在 image 文件里面。只有通过这个文件，才能生成 Docker 容器

image 文件是通用的，一台机器的 image 文件拷贝到另一台机器，照样可以使用。一般来说，为了节省时间，我们应该尽量使用别人制作好的 image 文件，而不是自己制作。即使要定制，也应该基于别人的 image 文件进行加工，而不是从零开始制作
```

### 2.2 registry
```
为了方便共享，image 文件制作完成后，可以上传到网上的仓库。Docker 的官方仓库 Docker Hub 是最重要、最常用的 image 仓库。此外，出售自己制作的 image 文件也是可以的
```

### 2.3 container容器
```
image 文件生成的容器实例，本身也是一个文件，称为容器文件。也就是说，一旦容器生成，就会同时存在两个文件： image 文件和容器文件。而且关闭容器并不会删除容器文件，只是容器未运行而已
```

### 2.4 docker client
```
通过docker命令，将操作转交给docker daemon执行
```

### 2.5 docker daemon

```
docker daemon是服务器组件，以Linux后台服务的方式运行。负责创建、运行、监控容器、构建、存储镜像
```


### 2.6 docker file
```
学会使用 image 文件以后，接下来的问题就是，如何可以生成 image 文件？如果你要推广自己的软件，势必要自己制作 image 文件。

这就需要用到 Dockerfile 文件。它是一个文本文件，用来配置 image。Docker 根据该文件生成二进制的 image 文件。docker file相当于编写image的源代码文件
```

### 2.7 docker底层技术
```
前边提到了Linux Containers技术，可以实现资源的隔离和控制，也就是对 Cgroup 和 Namespace 两个属性的控制

docker 出现之初，便是采用了 lxc 技术作为 docker 底层，对容器虚拟化的控制。后来随着 docker 的发展，它自己封装了 libcontainer （golang 的库）来实现 Cgroup 和 Namespace 控制，从而消除了对 lxc 的依赖
```
- NameSpace
  - Linux中的PID、IPC、网络等资源是全局的，而NameSpace机制是一种资源隔离方案，各个NameSpace下的资源互不干扰，这就使得每个NameSpace看上去就像一个独立的操作系统一样，但是只有NameSpace是不够
- Control groups
  - 虽然有了NameSpace技术可以实现资源隔离，但进程还是可以不受控的访问系统资源，比如CPU、内存、磁盘、网络等，为了控制容器中进程对资源的访问，Docker采用control groups技术(也就是cgroup)，有了cgroup就可以控制容器中进程对系统资源的消耗了，比如你可以限制某个容器使用内存的上限、可以在哪些CPU上运行等等


## 3. docker演示

### 3.1 docker安装
```
Docker社区版 的安装请参考官方文档
Mac：https://docs.docker.com/docker-for-mac/install/
Windows：https://docs.docker.com/docker-for-windows/install/
Ubuntu：https://docs.docker.com/install/linux/docker-ce/ubuntu/
Debian：https://docs.docker.com/install/linux/docker-ce/debian/
CentOS：https://docs.docker.com/install/linux/docker-ce/centos/
Fedora：https://docs.docker.com/install/linux/docker-ce/fedora/
其他 Linux 发行版：https://docs.docker.com/install/linux/docker-ce/binaries/

安装完毕后，打开命令行窗口，通过 docker -v 查看是否安装成功
```

### 3.2 基础docker命令
- docker pull [imageName]:把image拉到本地，例：docker pull hello-world
- docker image ls：查看本地镜像
- docker container run：通过image新建一个容器并启动，例：docker container run hello-world
- docker container ls：查看正在运行的容器
- docker container ls -all：查看所有容器，包含未运行状态的
- docker container start [containerID]：启动已有容器
- docker container stop [containerID]：停止运行容器
- docker container restart [containerID]：重启运行容器

### 3.3 制作docker镜像
```
第一步 新建index.html
<h1 align="center">Hello,Welcome to Docker World</h1>

第二步 打包成war
jar -cvf javaweb.war index.html

第三步 编写dockerfile文件，输入如下内容
FROM tomcat
ADD *.war /usr/local/tomcat/webapps/javaweb.war
EXPOSE 8080
ENTRYPOINT catalina.sh run

第四步，编译镜像
docker build -f dockerfile -t java-web .

第五步，运行
docker run -it -p 8080:8080 java-web

第六步 浏览器打开 http://localhost:8080/javaweb/index.html 
程序已成功运行
```

## 4.  docker进阶
### 4.1 镜像分层
```
在构建镜像的dockerfile文件时，有FROM tomcat这么一行，意思是我们构建的镜像是在tomcat镜像的基础上构建的。相当于我们的镜像包含了tomcat镜像，所以直接运行我们镜像的容器即可，不需要再去下载tomcat运行。同理，tomcat镜像其实包含了jdk的镜像。在实际应用过程中，可能会出现多个上层镜像包含相同的底层镜像的情况。
镜像分层就是来解决重复包含的问题，每个镜像内部可能嵌套几层基础镜像，每个基础镜像其实是独立的镜像，当多个上层镜像同时依赖一个底层镜像时，发现已经存在相同的底层镜像，就不再重复拉取了。但是镜像本身还是包含了所有的内容，只是可以按需拉取。
```
![alt](https://mmbiz.qpic.cn/mmbiz_png/kYCUF3DUwREf3dwBHH5sVdm2mTLL3vF4ulnIGXiaM21N45ORLHXl3ibQSicSvjxibTpeczE0F37Hmr5SqT5DpH1R0A/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

### 4.2 copy-on-write

```
如果上层镜像A、B都依赖镜像C，但是A和B都需要修改镜像C目录下的文件内容。那么岂不是会造成冲突？
copy-on-write模式就是解决这个问题的，底层镜像的文件只能读不能写，如果要写则在上层镜像的相同目录下写入，真正运行的时候是从上往下读，上层镜像有的以上层为准，相当于覆盖了下层镜像的文件。这样就可以多个上层镜像写入底层镜像同一目录且不冲突。
```

![alt](https://mmbiz.qpic.cn/mmbiz_png/XgwOJXeCnnLtozoUJOxuNickW1MYibAGhZt7wTvV9vaIkcRFltnItoia8fb28sWhTHgns7jgIbRvY6icuoLrYUos0A/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)


```
因为每个容器的资源都是隔离的，所以网络、内存、CPU、文件系统等资源都是彼此独立的
这样就牵扯出了docker的网络问题，相关部分如果感兴趣可以自行阅读
```

## 参考文档
[Docker 入门教程](https://mp.weixin.qq.com/s?__biz=Mzg5Mjc3MjIyMA==&mid=2247543115&idx=1&sn=6e59259841a2303f320b3275f3891b9d&source=41#wechat_redirect)
[什么是Docker？看这一篇干货文章就够了！](https://mp.weixin.qq.com/s?src=11&timestamp=1658804857&ver=3943&signature=SUp6Bi0VjIzZcviyFXc-*K26YJgEFxJx1kfrt8LOc5ivrDTu3Py-pJr5hCuhJuvwZ1SMIQLe*gWIkvpNi0dOU4QxPdATBHCY0lYaaRDalTaPDTWAeS1gh9Y08FJnElH1&new=1)

