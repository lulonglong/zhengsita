package share.java.lambda.demo;

import com.alibaba.fastjson.JSON;
import org.assertj.core.util.Lists;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class LambdaDemo {
    public static void main(String[] args) {

        testFunctionalInterface1(() -> System.out.println(1));
        testSupplier(() -> "abcd");
        testConsumer("abcd", i -> System.out.println(i));
        testConsumer("abcd", System.out::println);

        testPredicate("abcd",i->i.contains("a"));

        testFunction("abcd",Function.identity());

        testSpliterator();

        testCollector();

        testCustomCollector();
    }


    /**
     * 试验点
     * 1.是否可以有两个抽象方法：不可以，编译报错，如果有多个抽象方法，那么lambda表达式的函数段到底是哪个抽象方法的实现就判断不了了
     * 2.不加@FunctionalInterface注解行不行:只要符合函数接口的规范，也可以
     *
     * @param funcInterface1
     */
    private static void testFunctionalInterface1(FunctionalInterface1 funcInterface1) {
        funcInterface1.test1();
    }

    /**
     * 无输入，有输出
     * @param supplier
     */
    private static void testSupplier(Supplier<String> supplier) {
        System.out.println(supplier.get());
    }

    /**
     * 有输入，无输出
     * @param param
     * @param consumer
     */
    private static void testConsumer(String param, Consumer<String> consumer) {
        consumer.accept(param);
    }

    /**
     * 有输入，输出Boolean
     * @param param
     * @param predicate
     */
    private static void testPredicate(String param,Predicate<String> predicate){
        System.out.println(predicate.test(param));
    }

    /**
     * 有输入，有输出
     * @param param
     * @param func
     */
    private static void testFunction(String param,Function<String,String> func){
        System.out.println(func.apply(param));
    }

    /**
     * 直接引用其他类的方法相当于代码块的复用
     */
    private static void testMethodCode(){
        List<String> arrs= Lists.newArrayList("a","b","c","d","e","f","g","h");
        arrs.stream().forEach(System.out::print);
    }



    private static void testCollector(){
        String json="[{\"articleId\":\"1anasfuwei2i12hh1dshu\",\"index\":0,\"createTime\":\"2021-11-01 00:00:00\"},{\"articleId\":\"32i12hh1anasfuwedsh\",\"index\":0,\"createTime\":\"2021-11-03 00:00:00\"},{\"articleId\":\"1anasfuwei2i12hh1dshu\",\"index\":2,\"createTime\":\"2021-11-01 00:00:00\"},{\"articleId\":\"32i12hh1anasfuwedsh\",\"index\":4,\"createTime\":\"2021-11-03 00:00:00\"},{\"articleId\":\"32i12hh1anasfuwedsh\",\"index\":3,\"createTime\":\"2021-11-03 00:00:00\"},{\"articleId\":\"32i12hh1anasfuwedsh\",\"index\":2,\"createTime\":\"2021-11-03 00:00:00\"},{\"articleId\":\"1anasfuwei2i12hh1dshu\",\"index\":1,\"createTime\":\"2021-11-01 00:00:00\"},{\"articleId\":\"2hh1dshuanasfuwei2i12\",\"index\":2,\"createTime\":\"2021-11-02 00:00:00\"},{\"articleId\":\"1anasfuwei2i12hh1dshu\",\"index\":3,\"createTime\":\"2021-11-01 00:00:00\"},{\"articleId\":\"2hh1dshuanasfuwei2i12\",\"index\":0,\"createTime\":\"2021-11-02 00:00:00\"},{\"articleId\":\"2hh1dshuanasfuwei2i12\",\"index\":1,\"createTime\":\"2021-11-02 00:00:00\"},{\"articleId\":\"32i12hh1anasfuwedsh\",\"index\":1,\"createTime\":\"2021-11-03 00:00:00\"}]";
        List<Article> articleList=JSON.parseArray(json,Article.class);
        List<Article> list= articleList.stream().sorted(Comparator.comparing(Article::getCreateTime).reversed().thenComparing(Article::getIndex)).collect(Collectors.toList());
        System.out.println(list);
    }

    /**
     * 自定义Collector参考
     */
    private static void testCustomCollector(){
        List<String> arrs=Lists.newArrayList("a","b","c","d","e","f","g","h");
        //串行计算时，只创建一个实例，遍历元素做accumulator操作，这里的accumulator是指add方法，串行操作用不到merge
        String result=arrs.stream().collect(Collector.of(CharBuilder::new, CharBuilder::add, CharBuilder::merge, CharBuilder::result, Collector.Characteristics.CONCURRENT));
        System.out.println(result);

        //并行运算时每个accumulator操作都会创建一个实例，再merge
        result=arrs.parallelStream().collect(Collector.of(CharBuilder::new, CharBuilder::add, CharBuilder::merge, CharBuilder::result, Collector.Characteristics.UNORDERED));
        System.out.println(result);
    }

    private static class CharBuilder{
        public StringBuilder stringBuilder=new StringBuilder();

        public CharBuilder() {
            System.out.println(Thread.currentThread().getName()+" 构造一个CharBuilder");
        }

        public CharBuilder add(String str){
            System.out.println(Thread.currentThread().getName()+" add str:"+str);
            stringBuilder.append(str).append("|");
            return this;
        }

        public CharBuilder merge(CharBuilder charBuilder){
            if (charBuilder.stringBuilder.toString().contains("b")){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName()+" merge :"+charBuilder.stringBuilder.toString()+" to :"+stringBuilder.toString());
            this.stringBuilder.append(charBuilder.stringBuilder);
            return this;
        }

        public String result(){
            System.out.println(Thread.currentThread().getName()+" result :"+this.stringBuilder.toString());
            return "niubi:"+this.stringBuilder.toString();
        }

    }
    /**
     * 分隔迭代器
     * 内部对iterator进行了封装，可以进行切分，以支持stream的并行处理
     */
    private static void testSpliterator(){
        List<String> arrs=Lists.newArrayList("a","b","c","d","e","f","g","h");
        Spliterator<String> spliterator= arrs.spliterator();
        System.out.println(spliterator.estimateSize());
        System.out.println(spliterator.getExactSizeIfKnown());
        //尝试遍历一个
        spliterator.tryAdvance(System.out::println);
        spliterator.tryAdvance(System.out::println);
        //预计剩余数量
        System.out.println(spliterator.estimateSize());
        //准确剩余数量，如果可以准确得出的话
        System.out.println(spliterator.getExactSizeIfKnown());
        //遍历剩余元素
        spliterator.forEachRemaining(System.out::println);
        //spliterator.forEachRemaining(System.out::println);
        System.out.println("----");
        Spliterator<String> spliterator1= arrs.spliterator();
        System.out.println(spliterator1.estimateSize());
        Spliterator<String> spliterator2= spliterator1.trySplit();
        Spliterator<String> spliterator3= spliterator1.trySplit();
        System.out.println(spliterator1.estimateSize());
        System.out.println(spliterator2.estimateSize());
        System.out.println(spliterator3.estimateSize());
        System.out.println("----");
        Spliterator<String> spliterator4= spliterator2.trySplit();
        System.out.println(spliterator2.estimateSize());
        System.out.println(spliterator4.estimateSize());
    }


    private static class Article{
        private String articleId;
        private int index;
        private Date createTime;

        public String getArticleId() {
            return articleId;
        }

        public void setArticleId(String articleId) {
            this.articleId = articleId;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        @Override
        public String toString(){
            return JSON.toJSONStringWithDateFormat(this,"yyyy-MM-dd HH:mm:ss");
        }
    }
}
