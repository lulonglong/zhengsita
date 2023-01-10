# Fastjson解析

## 介绍

Fastjson 是一个 Java 库，可以将 Java 对象转换为 JSON 格式，当然它也可以将 JSON 字符串转换为 Java 对象。

## 序列化

使用 JSON 的toJSONString方法，可以将任意java对象序列化为json字符串。

```
    JSON:
    public static String toJSONString(Object object, // 序列化对象
                                      SerializeConfig config, // 全局序列化配置
                                      SerializeFilter[] filters, //  序列化拦截器
                                      String dateFormat, // 序列化日期格式
                                      int defaultFeatures, //  默认序列化特性
                                      SerializerFeature... features // 自定义序列化特性
                                      ) {
                                      
        
        // 初始化序列化writer，用features覆盖defaultFeatures配置                              
        SerializeWriter out = new SerializeWriter(null, defaultFeatures, features);

        try {
        
            // 初始化JSONSerializer，由config查找序列化处理器来处理序列化工作，序列化结果写入out的buffer中
            JSONSerializer serializer = new JSONSerializer(out, config);
            
            if (dateFormat != null && dateFormat.length() != 0) {
                serializer.setDateFormat(dateFormat);
                serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
            }

            // 添加拦截器
            if (filters != null) {
                for (SerializeFilter filter : filters) {
                    serializer.addFilter(filter);
                }
            }
            
            // 由config查找序列化处理器来处理序列化工作，序列化结果写入out的buffer中
            serializer.write(object);

            // 输出序列化结果
            return out.toString();
        } finally {
            out.close();
        }
    }
    
```

1、新产生的一个数据保存器，储存在序列化过程中产生的数据；
2、产生统一的json序列化器，其中使用了SerializeWriter，此类即json序列化的统一处理器；
3、调用序列化方法开始序列化对象，以产生json字符串信息；
4、返回已经储存的json信息。

### SerializeWriter

SerializeWriter是一个用于储存在序列化过程中产生的数据信息，它与jdk中的StringBuiler有着类似的功能，即将不同的数据填充到此容器中。之所以不使用StringBuilder的原因之一在于StringBuilder没有提供一些特别为性能优化的方法，并且StringBuilder在处理过程中增加了多余的操作（如新分配对象）。该容器的主要功能就是接收不同的数据，并将这些数据存储到该内部的一个字符数组当中，同时记录字符总数。

在实现上面，SerializeWriter使用了一个内部的字符数组作为数据的储存器，同时使用了一个计数器计算当前存储的字符量。既然使用了字符数组，那么肯定有相关的操作，如字符扩容等。整个写数据的过程，其实就是往这个字符数组追加数据的过程，需要考虑只是如何追加数据的问题，即上面所列出的这么多些方法。在最终写完数据之后，即可将这个字符数组转为我们所需要的字符串了。


* buf 存储序列化结果buffer
* count buffer中包含的字符数  
* features 序列化的特性，比如写枚举按照名字还是枚举值, 输出格式等
* useSingleQuotes 是否使用单引号输出json

### JSONSerializer

对象序列化入口

JsonSerializer，这只是一个提供对象序列化的一个入口；同时，它持有所有具体负责对象序列化工作类的引用。将这些序列化器集中起来，需要用到哪个对象序列化器时，就取出这个序列化器，并调用相应的序列化方法。
既然是对象序列化入口，它就需要关注两个事情。一是我们究竟有哪些序列化器可以使用，二是对于一个对象，应该使用哪一个序列化器来进行工作。对于这两个问题，JsonSerializer内部持有一个JSONSerializerMap的属性，即表示应该序列化的对象类型和对应的序列化器的一个映射。我们来看默认的构造方法，它使用了默认的全局对象类型和对象序列化器映射：

```
    public JSONSerializer(SerializeWriter out){
        this(out, SerializeConfig.getGlobalInstance());
    }
    
    public final void write(Object object) {
    
        // 如果对象为空，直接输出 "null" 字符串
        if (object == null) {
            out.writeNull();
            return;
        }

        // 根据对象的Class类型查找具体序列化实例
        Class<?> clazz = object.getClass();
        ObjectSerializer writer = getObjectWriter(clazz);

        try {
        
            // 使用具体serializer实例处理对象
            writer.write(this, object, null, null, 0);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

```

这里使用了全局的一个对象序列化器映射，加上后面在getObjectWriter中追加的对象序列化器映射

这些序列化器，覆盖了基本数据，字符串类型，日期，以及集合，map，以及javaBean的所有序列化器。因为不存在没有匹配不了的序列化器。既然有个序列化器，就可以执行序列化工作了。即到了序列化入口应该做的工作了。


### ObjectSerializer

序列化过程
```
    void write(JSONSerializer serializer, Object object) throws IOException
    
```
该方法在抽象类（可以说是接口）ObjectSerializer中定义，即所有的序列化器都继承了此类，并实现了此方法用于处理不同的情形。对于上层调用（如JsonSerializer)，不需要考虑每一个类型的序列化工作是如何实现的，只需要针对不同的类型找到正确的序列化器，进行序列化工作即可，这样即完成了一个完整的序列化工作

#### StringCodec
* 要序列化的内容为字符串，则选用此处理器
* write 序列化方法，将内容写到到SerializeWriter中

```
    public void write(JSONSerializer serializer, String value) {
        SerializeWriter out = serializer.out;

        if (value == null) {
            out.writeNull(SerializerFeature.WriteNullStringAsEmpty);
            return;
        }

        out.writeString(value);
    }
```

#### JavaBeanSerializer
* 没有精准匹配到处理器，则使用此通用序列化处理器，
```

    protected void write(JSONSerializer serializer, //
                      Object object, //
                      Object fieldName, //
                      Type fieldType, //
                      int features,
                      boolean unwrapped
    ) throws IOException {
    
        SerializeWriter out = serializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }

        // 如果开启循环引用检查，输出引用并返回
        if (writeReference(serializer, object, features)) {
            return;
        }

        final FieldSerializer[] getters;

        if (out.sortField) {
            getters = this.sortedGetters;
        } else {
            getters = this.getters;
        }

        SerialContext parent = serializer.context;
        if (!this.beanInfo.beanType.isEnum()) {
            serializer.setContext(parent, object, fieldName, this.beanInfo.features, features);
        }

        final boolean writeAsArray = isWriteAsArray(serializer, features);

        FieldSerializer errorFieldSerializer = null;
        try {
            ...

            final boolean writeClassName = out.isEnabled(SerializerFeature.WriteClassName);
            // 触发序列化BeforeFilter拦截器
            char newSeperator = this.writeBefore(serializer, object, seperator);
            commaFlag = newSeperator == ',';

            final boolean skipTransient = out.isEnabled(SerializerFeature.SkipTransientField);
            final boolean ignoreNonFieldGetter = out.isEnabled(SerializerFeature.IgnoreNonFieldGetter);
  
  
            // 每个字段依次序列化
            for (int i = 0; i < getters.length; ++i) {
                FieldSerializer fieldSerializer = getters[i];

                Field field = fieldSerializer.fieldInfo.field;
                FieldInfo fieldInfo = fieldSerializer.fieldInfo;
                String fieldInfoName = fieldInfo.name;
                Class<?> fieldClass = fieldInfo.fieldClass;

                final boolean fieldUseSingleQuotes = SerializerFeature.isEnabled(out.features, fieldInfo.serialzeFeatures, SerializerFeature.UseSingleQuotes);
                final boolean directWritePrefix = out.quoteFieldNames && !fieldUseSingleQuotes;

                // 忽略配置了transient关键字的字段
                if (skipTransient) {
                    if (fieldInfo != null) {
                        if (fieldInfo.fieldTransient) {
                            continue;
                        }
                    }
                }

                ...
                boolean notApply = false;
                // 触发字段PropertyPreFilter拦截器
                if ((!this.applyName(serializer, object, fieldInfoName)) //
                    || !this.applyLabel(serializer, fieldInfo.label)) {
                    if (writeAsArray) {
                        notApply = true;
                    } else {
                        continue;
                    }
                }

                // 针对属性名字和属性值 触发PropertyFilter拦截器
                if (!this.apply(serializer, object, fieldInfoName, propertyValue)) {
                    continue;
                }

                String key = fieldInfoName;
                // 触发属性名字NameFilter拦截器
                key = this.processKey(serializer, object, key, propertyValue);

                Object originalValue = propertyValue;
                
                // 触发属性值ContextValueFilter拦截器
                propertyValue = this.processValue(serializer, fieldSerializer.fieldContext, object, fieldInfoName,
                                                        propertyValue, features);

                ...

                if (commaFlag) {
                    if (fieldInfo.unwrapped
                            && propertyValue instanceof Map
                            && ((Map) propertyValue).size() == 0) {
                        continue;
                    }

                    out.write(',');
                    // 启用装饰格式化，换行
                    if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                        serializer.println();
                    }
                }

                if (key != fieldInfoName) {
                    if (!writeAsArray) {
                        out.writeFieldName(key, true);
                    }

                    serializer.write(propertyValue);
                } else if (originalValue != propertyValue) {
                    if (!writeAsArray) {
                        fieldSerializer.writePrefix(serializer);
                    }
                    serializer.write(propertyValue);
                } else {
                    if (!writeAsArray) {
                        boolean isMap = Map.class.isAssignableFrom(fieldClass);
                        boolean isJavaBean = !fieldClass.isPrimitive() && !fieldClass.getName().startsWith("java.") || fieldClass == Object.class;
                        if (writeClassName || !fieldInfo.unwrapped || !(isMap || isJavaBean)) {
                            if (directWritePrefix) {
                                out.write(fieldInfo.name_chars, 0, fieldInfo.name_chars.length);
                            } else {
                                fieldSerializer.writePrefix(serializer);
                            }
                        }
                    }

                    if (!writeAsArray) {
                        JSONField fieldAnnotation = fieldInfo.getAnnotation();
                        if (fieldClass == String.class && (fieldAnnotation == null || fieldAnnotation.serializeUsing() == Void.class)) {
                            if (propertyValue == null) {
                                int serialzeFeatures = fieldSerializer.features;
                                if (beanInfo.jsonType != null) {
                                    serialzeFeatures |= SerializerFeature.of(beanInfo.jsonType.serialzeFeatures());
                                }
                                if ((out.features & SerializerFeature.WriteNullStringAsEmpty.mask) != 0
                                        && (serialzeFeatures & SerializerFeature.WriteMapNullValue.mask) == 0) {
                                    out.writeString("");
                                } else if ((serialzeFeatures & SerializerFeature.WriteNullStringAsEmpty.mask) != 0) {
                                    out.writeString("");
                                } else {
                                    out.writeNull();
                                }
                            } else {
                                String propertyValueString = (String) propertyValue;

                                if (fieldUseSingleQuotes) {
                                    out.writeStringWithSingleQuote(propertyValueString);
                                } else {
                                    out.writeStringWithDoubleQuote(propertyValueString, (char) 0);
                                }
                            }
                        } else {
                            if (fieldInfo.unwrapped
                                    && propertyValue instanceof Map
                                    && ((Map) propertyValue).size() == 0) {
                                commaFlag = false;
                                continue;
                            }

                            fieldSerializer.writeValue(serializer, propertyValue);
                        }
                    } else {
                        fieldSerializer.writeValue(serializer, propertyValue);
                    }
                }

                boolean fieldUnwrappedNull = false;
                if (fieldInfo.unwrapped
                        && propertyValue instanceof Map) {
                    Map map = ((Map) propertyValue);
                    if (map.size() == 0) {
                        fieldUnwrappedNull = true;
                    } else if (!serializer.isEnabled(SerializerFeature.WriteMapNullValue)){
                        boolean hasNotNull = false;
                        for (Object value : map.values()) {
                            if (value != null) {
                                hasNotNull = true;
                                break;
                            }
                        }
                        if (!hasNotNull) {
                            fieldUnwrappedNull = true;
                        }
                    }
                }

                if (!fieldUnwrappedNull) {
                    commaFlag = true;
                }
            }

                    //触发序列化AfterFilter拦截器
            this.writeAfter(serializer, object, commaFlag ? ',' : '\0');

            
        } catch (Exception e) {
            ...
        } finally {
            serializer.context = parent;
        }
    }

```

### ASMSerializerFactory
* 通过ASM，会为每个 JavaBean 生成一个独立的 JavaBeanSerializer 子类, 并重写write方法。
* JavaBeanSerializer中的write办法会应用反射从JavaBean中获取相干信息，而通过ASM生成的ASMSerializer_1_Person，是针对Person独有的序列化工具类
* 因为是仅是Person的序列化工具，可以强转Object为Person，通过间接调用的形式获取Person的相干信息，替换了反射的应用，间接调用的性能比应用反射强很多
```
    public void write(JSONSerializer jSONSerializer, Object object, Object object2, Type type, int n) throws IOException {
        ...
        Person person = (Person)object;
        ...
    }
```

## 反序列化

反序列化的含义是将跨语言的json字符串转换成java对象。

### 词法分析器
#### JSONToken

先看下token的定义

```
public final static int ERROR                = 1;
    //
    public final static int LITERAL_INT          = 2;
    //
    public final static int LITERAL_FLOAT        = 3;
    //
    public final static int LITERAL_STRING       = 4;
    //
    public final static int LITERAL_ISO8601_DATE = 5;

    public final static int TRUE                 = 6;
    //
    public final static int FALSE                = 7;
    //
    public final static int NULL                 = 8;
    //
    public final static int NEW                  = 9;
    //
    public final static int LPAREN               = 10; // ("("),
    //
    public final static int RPAREN               = 11; // (")"),
    //
    public final static int LBRACE               = 12; // ("{"),
    //
    public final static int RBRACE               = 13; // ("}"),
    //
    public final static int LBRACKET             = 14; // ("["),
    //
    public final static int RBRACKET             = 15; // ("]"),
    //
    public final static int COMMA                = 16; // (","),
    //
    public final static int COLON                = 17; // (":"),

```

#### JSONLexerBase
* token
  * int
  * 词法标记, 用于标识当前在解析过程中解析到的对象的一个标记
  * 比如 {，即表示当前正在解析的是一个对象的格式，而}，则表示当前对象已经到底了
* ch
  * char
  * 当前字符
  * 用于表示当前已经读取到的字符是什么，如 abc，当位置为1时，则当前字符为 b
* bp
  * int
  * 解析字符位置
  * 用于表示当前字符所位于原始字符串中的哪一个位置，与ch是相对应的，它始终表示最新的一个位置，如果需要记录一些历史位置。如字符串起始位置，数字起始位置等，则需要使用其它标记，如np
* sbuf
  * char[]
  * 字符缓冲
  * 在解析字符串时的特殊存储区域，主要是用于解析转义字符时的临时存储区。即如果原字符串为 a\\t，则实际解析的字符串应该为a\t，那么原字符串为3位长，解析之后为2位长。即需要另行存储。字符缓冲区如名所示，为一个字符数组
* sp
  * int
  * 字符缓冲区位置
  * 这个用于表示在字符缓冲区之间记录当前字符串(或数字串)等的长度信息，同时也等同于当前的一个位置(如果坐标从0开始)。
* np
  * int
  * 数字解析位置
  * 用于实际表示在解析到常量信息时起始点的标记位置。通过np + sp,即计算得出相应的区间值了

#### 词法规则
贪婪的匹配规则，一旦满足一个匹配规则，那么这个匹配就要继续下去，直到当前规则不能完成时，同时在下一个规则之间，使用特定的分隔符作连接。
字符串 {"a":"123", "b":[1,2,3]},即按以下规则进行

对象开始:{
对象key(即字段):a
分隔符: :
对象value开始:
字符串开始: "
字符串:123
字符串结束: "
对象value:结束：
对象间分隔符:,
对象key: b
分隔符: :
对象value开始:
数组开始: [
数组值1数字开始: 1
数组值1数字结束: 1,
数组分隔符: ,
…
数组结束: ]
对象结束: }

#### 词法解析

整个词法，即TOKEN流，是由类JSONLexerBase来负责完成的，其负责提供主要词法单元的解析和结果取值操作。相应方法对应关系如下所示

* 数字
  * scanNumber
  * numberString
  * intValue
  * longValue
  * floatValue
  * doubleValue
* 字符串
  * scanString
  * stringVal
* NULL值
  * scanNULL
  * scanNullOrNew
* Boolean值
  * scanTrue
  * scanFalse


### JSON

  入口也在JSON类中，一般使用JSON.parseObject(text, clazz) 来实现反序列

```
    
    public static <T> T parseObject(String text, Class<T> clazz) {
        return parseObject(text, clazz, new Feature[0]);
    }
  
    public static <T> T parseObject(String input, Type clazz, ParserConfig config, ParseProcess processor,
                                          int featureValues, Feature... features) {
        
        if (input == null || input.isEmpty()) {
            return null;
        }

        if (features != null) {
            for (Feature feature : features) {
                featureValues |= feature.mask;
            }
        }


        // 初始化DefaultJSONParser，由config查找反序列化处理器来处理反序列化工作
        DefaultJSONParser parser = new DefaultJSONParser(input, config, featureValues);

        // 添加拦截器
        if (processor != null) {
            if (processor instanceof ExtraTypeProvider) {
                parser.getExtraTypeProviders().add((ExtraTypeProvider) processor);
            }

            if (processor instanceof ExtraProcessor) {
                parser.getExtraProcessors().add((ExtraProcessor) processor);
            }

            if (processor instanceof FieldTypeResolver) {
                parser.setFieldTypeResolver((FieldTypeResolver) processor);
            }
        }

        // 反序列化
        T value = (T) parser.parseObject(clazz, null);

        // 处理json内部引用协议格式对象
        parser.handleResovleTask(value);

        parser.close();

        return (T) value;
    }
    
```

### DefaultJSONParser

负责具体类型查找反序列化实例，执行反序列化转换

```
    public <T> T parseObject(Type type, Object fieldName) {
        
        // 获取json串第一个有效token
        int token = lexer.token();
        if (token == JSONToken.NULL) {
            lexer.nextToken();
            return null;
        }
  
        // 判定token属于字符串, 直接输出字符串对象，解析结束      
        if (token == JSONToken.LITERAL_STRING) {
            if (type == byte[].class) {
                byte[] bytes = lexer.bytesValue();
                lexer.nextToken();
                return (T) bytes;
            }

            if (type == char[].class) {
                String strVal = lexer.stringVal();
                lexer.nextToken();
                return (T) strVal.toCharArray();
            }
        }

        // 托config进行特定类型查找反序列化实例
        ObjectDeserializer deserializer = config.getDeserializer(type);

        try {
        
            // 执行反序列化
            if (deserializer.getClass() == JavaBeanDeserializer.class) {
                if (lexer.token()!= JSONToken.LBRACE && lexer.token()!=JSONToken.LBRACKET) {
                throw new JSONException("syntax error,except start with { or [,but actually start with "+ lexer.tokenName());
            }
                return (T) ((JavaBeanDeserializer) deserializer).deserialze(this, type, fieldName, 0);
            } else {
                return (T) deserializer.deserialze(this, type, fieldName);
            }
        } catch (JSONException e) {
            throw e;
        } catch (Throwable e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

```
  
### ObjectDeserializer

在实际的使用场景，我们均会使用到如 parseObject(String text, Class<T> clazz) 来期望返回具体的类型，这里实际上就会调用到了不同的类型反序列化器了。
fastjson根据这里的类型，调用相应的序列化对象来完成不同的对象解析工作。

反序列化器的工作也并不是进行具体的语法解析，而是提供相应的类型信息，以期望jsonParser进行正常的解析工作。
即具体的解析工作仍是由jsonParser来完成，ObjectDeserializer只不过提供一些上下文信息，以及对流程进行控制

#### CollectionCodec
集合类型的反序列化器

```
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        if (parser.lexer.token() == JSONToken.NULL) {
            parser.lexer.nextToken(JSONToken.COMMA);
            return null;
        }
        
        if (type == JSONArray.class) {
            JSONArray array = new JSONArray();
            parser.parseArray(array);
            return (T) array;
        }

        Collection list = TypeUtils.createCollection(type);

        Type itemType = TypeUtils.getCollectionItemType(type);
        parser.parseArray(itemType, list, fieldName);

        return (T) list;
    }

```

#### JavaBeanDeserializer
自定义对象一般使用此反序列化器

JavaBean的过程可以理解为，先创建对象，然后每于{}中的每一项，先匹配key值，然后根据key值查找到相应的字段信息，根据不同的字段再解析该字段值

### ASMDeserializerFactory

ASMDeserializerFactory是用来动态使用ASM生成JavaBean的Deserializer，针对每个类的特点进行特别优化，以获得最快的性能。

会为每个Class生成一个JavaBeanDeserializer子类，命令为FastjsonASMDeserializer_1_UserDTO

## 扩展功能
### JsonField 
作用在字段或方法上
用于描述字段的序列信息及反序列信息，如重新设定name值，是否需要反序列化等
    
### JSONType
定制序列化功能，作用在类上

sam 是否支持asm，默认是支持的
ignores, 要忽略的序列化的字段名数组
serializer 自定义序列化工具
deserializer 自定义反序列化工具
serialzeFilters 配置过滤器

### JSONCreator
  没有缺省构造方法，@JSONCreator可以用来指定构造方法来创建Java对象

### SerializeFilter

通过SerializeFilter可以使用扩展编程的方式实现定制序列化

* BeforeFilter
  * 序列化时在最前添加内容
  * test2
* AfterFilter
  * 序列化时在最后添加内容
  * test3
* NameFilter
  * 修改key的值。如果需要修改key, process返回值则可
  * test4
* ValueFilter
  * 修改返回的value值
  * test5
* PropertyFilter
  * 根据key和value来判断是否序列化
  * test6
* PropertyPreFilter
  * 根据key判断是否序列化
  * test7

## 速度快的原因
* 自行编写类似StringBuilder的工具类SerializeWriter
  * 减少了字符数组内存的开辟。每次new一个SerializeWriter类时都会尝试从ThreadLocal这个缓存中查看是否有缓存起来的char数组，如果有就直接使用，减少了内存分配的开销
  * 减少了剩余容量的检查。在使用StringBuilder时，其内部也是一个char数组，在每一次append字符串时会检查是否有剩余容量可以分配，但如果我们已经知道一次要写入几个值，比如写一个开头 ‘{’ 紧接着就是双引号的属性名 “propertiesName” 然后就是一个冒号 ‘:’ ，这样连续写入3个类型，只需要一次的容量检查
* 使用ThreadLocal来缓存buf
  * 这个办法能够减少对象分配和gc，从而提升性能。SerializeWriter中包含了一个char[] buf，每序列化一次，都要做一次分配，使用ThreadLocal优化，能够提升性能。
* 使用asm避免反射
  * 获取java bean的属性值，需要调用反射，fastjson引入了asm的来避免反射导致的开销。使用了ASM框架自己编写字节码，然后使用ClassLoader将自定义的字节码加载成为类，变为特定POJO的序列化器，属性名和属性value将不需要反射获取，减少了这部分的反射开销

## 参考
[fastjson 反序列化源码解析](https://www.iflym.com/index.php/code/201508160001.html#more-794)
[fastjson-source-code-analysis](https://zonghaishang.gitbooks.io/fastjson-source-code-analysis/content/)
[分析FastJSON为什么那么快与字节码加强技术揭秘](http://www.javashuo.com/article/p-gbzfqcff-cs.html)