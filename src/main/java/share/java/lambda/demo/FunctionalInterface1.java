package share.java.lambda.demo;

/**
 * 函数式接口最多只能有一个未实现的抽象方法，否则编译不通过
 * @FunctionalInterface 可以省略，只要符合只有一个未实现的抽象方法即可，最好符合规范加上去
 */
@FunctionalInterface
public interface FunctionalInterface1 {
    void test1();

    //加上第二个方法会编译不通过
    //void test2();
}
