package share.java.class及泛型;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 泛型类，参数为T 和 V
 */
public class TypeTest<T, V extends @Custom Number & Serializable> {
    private Number number;

    //类型变量
    public T t;
    public V v;

    //参数类型
    public List<T> list = new ArrayList<>();
    public Map<String, T> map = new HashMap<>();

    //泛型数组类型
    public T[] tArray;
    public List<T>[] listTArray;

    //参数类型-》通配符类型
    public Map<? super String, ? extends Number> mapWithWildcard;

    public TypeTest testClass;
    public TypeTest<T, Integer> testClass2;

    //泛型构造函数,泛型参数为X
    public <X extends Number> TypeTest(X x, T t) {
        number = x;
        this.t = t;
    }

    //泛型方法，泛型参数为Y
    public <Y extends T> void method(Y y) {
        t = y;
    }

}

