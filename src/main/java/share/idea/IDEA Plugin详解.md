# IDEA Plugin详解

## 基本介绍

### 插件分类

插件主要分为如下几类：
* UI Themes（UI主题）
* Custom language support 自定义编程语言支持，如Kotlin
* Framework integration 框架集成，例如[Spring Assistant](https://plugins.jetbrains.com/plugin/17911-spring-assistant)
* Tool integration 工具集成，如Maven、Gradle、Git
* User interface add-ons 用户界面组件，例如[Random Background](https://plugins.jetbrains.com/plugin/9692-random-background)

技术栈
* Java & Kotlin
* IntelliJ SDK
* Gradle：依赖管理、sandbox、打包发版
* Swing：是的，整个IDEA界面组件用的都是Swing

## 新建插件

* 通过模板项目创建
  * 项目地址：https://github.com/JetBrains/intellij-platform-plugin-template
  * 使用说明：参照README中的步骤使用。

* 使用Gradle
  * 参考教程：https://plugins.jetbrains.com/docs/intellij/gradle-build-system.html
  * 在Idea中 File > New > Project:

* 使用DevKit
  * 参考教程：https://plugins.jetbrains.com/docs/intellij/using-dev-kit.html

工程结构
```
idea-plugin-demo
├── resources
│   └── META-INF
│       └── plugin.xml 
└── src
    └── cn.bugstack.guide.idea.plugin
        └── MyAction.java  
```

plugin.xml
```
<idea-plugin>
  <id>cn.bugstack.guide.idea.plugin</id>
  <name>idea-plugin-demo</name>
  <version>1.0</version>
  <vendor email="xxxx@xxx.com" url="https://xxx">plugin</vendor>

  <description>
  <![CDATA[
      基于IDEA插件模板方式创建测试工程<br>
      <em>1. 学习IDEA插件工程搭建</em>
      <em>2. 验证插件基础功能实现</em>
    ]]></description>

  <change-notes><![CDATA[
      插件开发学习功能点<br>
      <em>1. 工程搭建</em>
      <em>2. 菜单读取</em>
      <em>3. 获取配置</em>
      <em>4. 回显页面</em>
    ]]>
  </change-notes>

  <!-- please see http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/build_number_ranges.html for description -->
  <idea-version since-build="173.0"/>

  <!-- please see http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/plugin_compatibility.html
       on how to target different products -->
  <depends>com.intellij.modules.platform</depends>

  <extensions defaultExtensionNs="com.intellij">
    <!-- Add your extensions here -->
  </extensions>

  <actions>
    <!-- Add your actions here -->
    <action id="MyAction" class="cn.bugstack.guide.idea.plugin.MyAction" text="MyAction" description="MyAction">
      <add-to-group group-id="FileMenu" anchor="first"/>
    </action>
  </actions>

</idea-plugin>
```

### 发布插件
* 发布到 [JetBrains插件存储库](https://plugins.jetbrains.com/)
* 发布到 [自定义插件存储库中](https://plugins.jetbrains.com/docs/intellij/custom-plugin-repository.html#describing-your-plugins-in-updatepluginsxml-file)
* run 生成jar包，手动导入到idea中

## IntelliJ SDK

### PSI

程序结构接口，通常简称为 PSI，是 IntelliJ 平台中负责解析文件和创建为平台众多功能提供支持的句法和语义代码模型的层。

主要负责解析文件、创建语法、语义代码。

IDEA （有很多内置的插件），它们把整个工程的所有元素解析成了它们设计实现的PsiElement，你可以用它们提供的API很方便的去CURD所有元素。

### PsiFile

com.intellij.psi.PsiFile是文件结构的根，表示文件的内容为特定语言中元素的层次结构。它是所有PSI文件的公共基类，而在特定的语言文件通常是由它的子类来表示。例如，PsiJavaFile该类表示Java文件，而，XmlFile该类表示XML文件。

一个文件就是一个PsiFile，也是一个文件的结构树的根节点，PsiFile是一个接口，如果文件是一个.java文件，那么解析生成的PsiFile就是PsiJavaFile对象，如果是一个Xml文件，则解析后生成的是XmlFile对象。

#### PsiElement

Class文件结构包含字段表、属性表、方法表等，每个字段、方法也都有属性表，但在PSI中，总体上只有PsiFile和PsiElement。

Element即元素，一个PsiFile（本身也是PsiElement）由许多的PsiElement构成，每个PsiElement也可以由许多的PsiElement构成。

PsiElement用于描述源代码的内部结构，不同的结构对应不同的实现类。

对应Java文件的PsiElement种类有：PsiClass、PsiField、PsiMethod、PsiCodeBlock、PsiStatement、PsiMethodCallExpression等等。

其中，PsiField、PsiMethod都是PsiClass的子元素，

PsiCodeBlock是PsiMethod的子元素，

PsiMethodCallExpression是PsiCodeBlock的子元素，正是这种关系构造成了一棵树。

#### 查看psi结构
解析一个Java文件有上百种类型的PsiElement，对于一个新手，我们如何才能快速的认识对应Java代码文件中的每行代码都会解析生成呢？好在IDEA提供了PSI视图查看器。

配置文件在IDEA安装路径的bin目录下，找到idea.properties文件，加入一行配置 idea.is.internal=true

添加配置后重启IDEA就能看到tools菜单下新加了两个选择，如下图所示。
![alt 查看psi结构](https://img-stage.yit.com/CMSRESQN/c892469ff05653f4af74e4d0b1ad5583_762X392.png)

### Virtual Files

虚拟文件VirtualFile（VF）是在IntelliJ的虚拟文件系统（VFS)中的文件表示。虚拟文件即本地文件系统中的文件。

最常见的是，虚拟文件是本地文件系统中的文件。但是，IntelliJ 平台支持多种可插拔文件系统实现，因此虚拟文件也可以表示 JAR 文件中的类、从版本控制存储库加载的文件的旧修订等。

PsiFIle 和 VF 互转
```

PsiFile psiFile = PsiManager.getInstance(serverModule.getProject()).findFile(virtualFile);

VirtualFile virtualFile = psiFile.getVirtualFile();

```

### Action

com.intellij.openapi.actionSystem.AnAction是所有Action的基类。

action是开发插件功能最常用的方式，自定义 Action 类继承AnAction类，主要实现下面两个方法

* actionPerformed()
  * 在菜单栏中点击我们定义的action时，就会执行具体Action类中的actionPerformed函数。当回调actionPerformed()方法时，就相当于当前的Action被点击了一次。

* update()
  * 有时候我们定义的插件只在某些场景中才可以使用，比如说我们编写自动生成代码的插件时，只有当文件打开且是相应的类型时才能正常执行；如果不符合条件，就应该将插件按钮置为不能点击。
  
```
    // 将按钮设置为不可见
    @Override
    public void update(@NotNull final AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setEnabledAndVisible(false);
    }
```

plugin.xml定义action注册
```

<group id="LombokActionGroup" text="Lombok" description="Refactor code with lombok annotations" icon="/icons/lombok.png" popup="true"> 

      <action id="defaultLombokData" class="de.plushnikov.intellij.plugin.action.lombok.LombokDataAction" 
              text="Default @Data" description="Action to replace getter/setter/equals/hashcode/toString methods with lombok @Data annotation"> 
      </action> 

      <action id="defaultLombokGetter" class="de.plushnikov.intellij.plugin.action.lombok.LombokGetterAction" 
              text="Default @Getter" description="Action to replace all getter methods with lombok @Getter annotation"> 
      </action>

      <add-to-group group-id="RefactoringMenu" anchor="last"/>
</group>

```

* group id：多个Action的组合，id需要唯一
* group text：工具栏中group显示的信息
* group icon：group图标
* group popup：action是否为弹出
* action id：action ID，需要唯一
* action class：具体的Action实现类的全限定名
* action text：当前action在工具栏中的展示信息
* add-to-group：这个是比较关键的信息，group-id决定着当前group或者action显示在工具栏的具体地方，anchor决定着该action或者group显示在该工具栏的具体地方

![action效果图](https://img-stage.yit.com/CMSRESQN/ab2f10f6f68bb263d42374bd7cc2b9bb_980X702.png)

### Service

可以理解成一个普通的工具类。

调用ServiceManager.getService() 获取服务实例。IntelliJ平台可确保仅加载一个服务实例，即使它被多次调用也是如此。

service 有三种级别，分别是
* 应用级
* 项目级
* 模块级

```
  <extensions defaultExtensionNs="com.intellij">
   
    <!-- 应用级的service注册 -->
    <applicationService serviceImplementation="cn.yzstu.demo.service.HiServiceImpl" id="hiService"/>

    <!-- 项目级的service注册 -->
    <projectService serviceImplementation="cn.yzstu.demo.service.AuthorInfo" id="authorInfo"/>
  </extensions>

```

## 扩展能力

### CompletionContributor

自动补全代码

### Intention action

IDEA里有一个Intention action（代码推测）功能，快捷键是shift+enter。我们尝试新增一种代码推测，在输入类名后，通过推测自动生成new语句。例如，输入“User”，生成“User user = new User();”。

要新增Intention action功能，就要实现IntentionAction接口，实际代码里是用PsiElementBaseIntentionAction这个抽象类，它已经继承了IntentionAction。

IntentionAction接口里的核心方法是isAvailable()和invoke()方法。isAvailable()是在敲下shift+enter时，判断当前action是否满足执行条件；invoke()是选择action后，需要执行的逻辑。

### GotoDeclarationHandler

GoToImplementation 是一款跳转到各类实现的 IntelliJ IDEA 插件。

日常开发中常见的跳转有接口到实现跳转、 Mybatis 的 Mapper 到 xml 跳转

### InspectionToolProvider

开发自定义的live template的接口

### LocalInspectionEP

对静态代码进行检查

自定义的代码检测实现类需实现LocalInspectionEP接口

## 持久化配置

使用@State注解

@State主要有以下几个属性：

* name：指定state的名字，并将作为xml文档的根标签
* storages：支持一个或者多个@com.intellij.openapi.components.Storage注解来指定具体位置，对于project级别的项目，可以不指定，这种情况下会自动使用.ipr文件替代
* reloadable：可选，如果设置为false，那么当存储文件被外部修改（比如通过版本控制系统update）或者state有所改变的时候，整个项目都将reload

## Plugin UI

### Tool Windows

工具窗口是 IDE 的子窗口，用于显示信息。这些窗口通常在主窗口的外边缘有自己的工具栏（称为工具窗口栏），其中包含一个或多个工具窗口按钮，它们可以激活显示在主 IDE 窗口左侧、底部和右侧的面板。

```

<extensions defaultExtensionNs="com.intellij">
    <!-- 窗体 (IDEA 界面右侧) -->
    <toolWindow id="Read-Book" secondary="false" anchor="right" icon="/icons/logo.png" factoryClass="cn.bugstack.guide.idea.plugin.factory.ReadFactory"/>
</extensions>
```

```

public class ReadFactory implements ToolWindowFactory {

    private ReadUI readUI = new ReadUI();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // 获取内容工厂的实例
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        // 获取 ToolWindow 显示的内容
        Content content = contentFactory.createContent(readUI.getComponent(), "", false);
        // 设置 ToolWindow 显示的内容
        toolWindow.getContentManager().addContent(content);
        // 全局使用
        Config.readUI = readUI;
    }
}

```

### DialogWrapper

DialogWrapper 是应用于 IntelliJ 平台中显示的所有模式对话框（和一些非模式对话框）的基类。

它提供一下功能：
* 按钮布局（OK/cancel 按钮的平台特定顺序）
* 上下文帮助
* 记住对话框的大小
* 非模态验证（当输入到对话框中的数据无效时显示错误消息文本）
* 键盘快捷键（ESC/left/right/Y/N）
* 可选的不再询问复选框

### Swing UI Designer

利用Swing UI Designer设计界面：New —> Swing UI Designer —>Create Dialog Class

## 参考
[IntelliJ 平台 SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
[IntelliJ IDEA 插件开发指南](https://zhuanlan.zhihu.com/p/400059601)
[配置窗体和侧边栏窗体的使用](https://bugstack.cn/md/assembly/idea-plugin/2021-11-03-%E3%80%8AIntelliJ%20IDEA%20%E6%8F%92%E4%BB%B6%E5%BC%80%E5%8F%91%E3%80%8B%E7%AC%AC%E4%BA%8C%E8%8A%82%EF%BC%9A%E9%85%8D%E7%BD%AE%E7%AA%97%E4%BD%93%E5%92%8C%E4%BE%A7%E8%BE%B9%E6%A0%8F%E7%AA%97%E4%BD%93%E7%9A%84%E4%BD%BF%E7%94%A8.html)