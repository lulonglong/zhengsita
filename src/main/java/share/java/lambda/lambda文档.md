### 1.lambda
- 基于stream管道
- 使用Lambda必须有接口，并且接口中有且仅有一个抽象方法
- @FunctionalInterface注解表明是一个函数式接口

### 2.函数式接口： 有且仅有一个抽象方法的接口
- Supplier<T> 供给型   返回类型为T的对象，方法：T get()
- Consumer<T>消费型   对类型为T的对象应用操作，方法：void accept(T t)
- Predicate<T> 断定型   确定类型为T的对象是否满足某种约束，返回布尔值，方法：boolean test(T t)
- Function<T,R>函数型   对类型为T的对象应用操作，并返回R类型的对象，方法：R apply(T t)

### 3.Stream： 集合包装，stream是管道流只能被消费一遍

### 4.Spliterators： splitable iterator可分割迭代器

### 5.通过对迭代器进行一系列调用后，最后收集结果
- Collector:通过提供一个容器，并提供对每个元素处理的方法，以及合并结果的方法，以及最终呈现结果的方法
- Collectors：生产Collector的工具类
- Optional：一个包装对象的容器


````java
public interface Collector<T, A, R> {
    // supplier参数用于生成结果容器，容器类型为A
    Supplier<A> supplier();
    // accumulator用于消费元素，也就是归纳元素，这里的T就是元素，它会将流中的元素一个一个与结果容器A发生操作
    BiConsumer<A, T> accumulator();
    // combiner用于两个两个合并并行执行的线程的执行结果，将其合并为一个最终结果A
    BinaryOperator<A> combiner();
// finisher用于将之前整合完的结果R转换成为A
    Function<A, R> finisher();
    // characteristics表示当前Collector的特征值，这是个不可变Set
    Set<Characteristics> characteristics();
}

CollectorImpl(Supplier<A> supplier,
              BiConsumer<A, T> accumulator,
              BinaryOperator<A> combiner,
              Function<A,R> finisher,
              Set<Characteristics> characteristics) {
    this.supplier = supplier;
    this.accumulator = accumulator;
    this.combiner = combiner;
    this.finisher = finisher;
    this.characteristics = characteristics;
}


//join collector
return new CollectorImpl<>(
        () -> new StringJoiner(delimiter, prefix, suffix),
        StringJoiner::add, StringJoiner::merge,
        StringJoiner::toString, CH_NOID);
````



## 更多信息请看配套demo
