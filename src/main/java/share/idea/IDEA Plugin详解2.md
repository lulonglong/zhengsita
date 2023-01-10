# IDEA Plugin详解 第二篇

## 插件监听器

监听器允许插件以声明方式订阅通过 消息总线传递的事件。您可以定义应用程序级别和项目级别的监听器。

定义应用级监听器
```

<applicationListeners>
  <listener class="myPlugin.MyListenerClass" topic="BaseListenerInterface"/>
</applicationListeners>

public class MyVfsListener implements BulkFileListener {
    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        // handle the events
    }
}

```

定义项目级监听器

项目级监听器的注册方式相同，只是顶级标签是 <projectListeners>. 它们可用于监听项目级事件，例如工具窗口操作：

```

<projectListeners>
    <listener class="MyToolwindowListener" topic="com.intellij.openapi.wm.ex.ToolWindowManagerListener" />
</projectListeners>

public class MyToolwindowListener implements ToolWindowManagerListener {
    private final Project project;

    public MyToolwindowListener(Project project) {
        this.project = project;
    }

    @Override
    public void stateChanged() {
        // handle the state change
    }
}

```

### 常用的监听点

* PsiTreeChangeListener
  * 监听文件编辑事件
* ModuleListener
  * 项目结构变更的通知  

## 插件扩展

扩展是插件扩展 IntelliJ 平台功能的最常见方式

* <extensions>如果您的 plugin.xml 中尚不存在该元素，请将其添加到该元素。将defaultExtensionNs属性设置为以下值之一：
  * com.intellij，如果您的插件扩展了 IntelliJ 平台核心功能。
  * {ID of a plugin}, 如果您的插件扩展了另一个插件的功能。
* 向元素添加一个新的子<extensions>元素。子元素名称必须与您希望扩展访问的扩展点的名称匹配。
* 根据扩展点的类型，执行以下操作之一：
  * 如果扩展点是使用interface属性声明的，对于新添加的子元素，将implementation属性设置为实现指定接口的类的名称。
  * 如果扩展点是使用beanClass属性声明的，则对于新添加的子元素，设置所有带有注释的属性@Attribute指定 bean 类中的注释。

### 插件扩展点

通过在插件中定义扩展点，您可以允许其他插件扩展您的插件的功能。有两种类型的扩展点：
* 接口扩展点，允许其他插件使用代码扩展您的插件。当您定义接口扩展点时，您指定了一个接口，其他插件将提供实现该接口的类。然后，您将能够在这些接口上调用方法。
* 实体扩展点，允许其他插件使用数据扩展您的插件。您指定扩展类的完全限定名称，其他插件将提供数据，这些数据将转换为该类的实例。

#### 声明扩展点

要在插件中声明扩展点，请<extensionPoints>在plugin.xml. 然后插入一个子元素<extensionPoint>，该子元素定义扩展点名称和允许扩展插件功能的 bean 类或接口的名称，分别在name,beanClass和interface属性中

```
<idea-plugin>
  
  <extensionPoints>
    <extensionPoint name="myExtensionPoint1" beanClass="com.myplugin.MyBeanClass"/>
    <extensionPoint name="myExtensionPoint2" interface="com.myplugin.MyInterface" area="IDEA_PROJECT"/>
  </extensionPoints>

</idea-plugin>

```
* name属性为这个扩展点分配一个唯一的名称，它会<id>自动加上插件的前缀。
* beanClass属性设置一个 bean 类，该类指定一个或多个用@Attribute注解。
* interface属性设置了一个接口，该接口有助于扩展点的插件必须实现。
* area属性决定了扩展将被实例化的范围。
  * IDEA_APPLICATION应用程序（默认）
  * IDEA_PROJECT项目
  * IDEA_MODULE对于模块

#### 使用扩展点

要在运行时引用所有已注册的扩展实例，请声明ExtensionPointName传入与其声明plugin.xml匹配的完全限定名称。

```
    <extensions defaultExtensionNs="com.intellij">
        <!-- Add your extensions here -->
        <completion.contributor language="JAVA" implementationClass="io.volantis.plugin.better.coding.model.repository.EntityToDTOCompletionContributor"/>
    </extensions>
```

### 常用的扩展点

#### LangExtensionPoints.xml

* programRunner 
  * 执行过程
  * canRun 根据运行类型（Debug、Run） 和 RunProfile 类型判断是否由该配置 Runner 运行
  * execute 真正的执行函数

#### PlatformExtensionPoints.xml

* appStarter

* keymapExtension

* applicationConfigurable

* editor.linePainter

## 参考
[plugin-listeners](https://plugins.jetbrains.com/docs/intellij/plugin-listeners.html)
[Extension Points](https://plugins.jetbrains.com/docs/intellij/plugin-extension-points.html)
[idea扩展点集合](https://plugins.jetbrains.com/docs/intellij/extension-point-list.html)
[Java Instrumentation 和 Agent](https://leokongwq.github.io/2017/12/21/java-agent-instrumentation.html)