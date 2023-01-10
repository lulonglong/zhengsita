package share.java.class及泛型;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException {

        System.out.println("#########testTypeVariable########");
        testTypeVariable();

        System.out.println();
        System.out.println("#########testParameterizedType########");
        testParameterizedType();

        System.out.println();
        System.out.println("#########testGenericArrayType########");
        testGenericArrayType();

        System.out.println();
        System.out.println("#########testWildcardType########");
        testWildcardType();

        System.out.println();
        System.out.println("#########testGenericErasure########");
        testGenericErasure();

        System.out.println();
        System.out.println("#########testClassGeneric########");
        testClassGeneric();
    }

    /**
     * 类型变量，指的是T
     * @throws NoSuchFieldException
     */
    public static void testTypeVariable() throws NoSuchFieldException {

        //用反射的方式获取属性 public V v;
        Field v = TypeTest.class.getField("v");

        //获取属性类型
        TypeVariable typeVariable = (TypeVariable) v.getGenericType();
        System.out.println("TypeVariable1:" + typeVariable);

        //获取类型变量上界
        System.out.println("TypeVariable2:" + Arrays.asList(typeVariable.getBounds()));

        //获取类型变量声明载体
        System.out.println("TypeVariable3:" + typeVariable.getGenericDeclaration());

        //1.8 AnnotatedType: 如果这个这个泛型参数类型的上界用注解标记了，我们可以通过它拿到相应的注解
        AnnotatedType[] annotatedTypes = typeVariable.getAnnotatedBounds();
        System.out.println("TypeVariable4:" + Arrays.asList(annotatedTypes) + " : " +
                Arrays.asList(annotatedTypes[0].getAnnotations()));
        System.out.println("TypeVariable5:" + typeVariable.getName());

    }

    /**
     * 参数类型，指的是带类型变量参数T的类型，例如List<T>
     * @throws NoSuchFieldException
     */
    public static void testParameterizedType() throws NoSuchFieldException {

        Field list = TypeTest.class.getField("list");
        Type genericType1 = list.getGenericType();

        //参数类型1:java.util.List<T>
        System.out.println("参数类型1:" + genericType1.getTypeName());

        Field map = TypeTest.class.getField("map");
        Type genericType2 = map.getGenericType();

        //参数类型2:java.util.Map<java.lang.String, T>
        System.out.println("参数类型2:" + genericType2.getTypeName());

        if (genericType2 instanceof ParameterizedType) {

            ParameterizedType parameterizedType = (ParameterizedType) genericType2;

            //参数类型列表:[class java.lang.String, T]
            System.out.println("参数类型列表:" + Arrays.asList(parameterizedType.getActualTypeArguments()));

            //参数原始类型:interface java.util.Map
            System.out.println("参数原始类型:" + parameterizedType.getRawType());

            //参数父类类型:null,因为Map没有外部类，所以为null
            System.out.println("参数父类类型:" + parameterizedType.getOwnerType());
        }
    }

    /**
     * 泛型数组 T[]、List<T>[]
     * @throws NoSuchFieldException
     */
    public static void testGenericArrayType() throws NoSuchFieldException {

        //数组参数类型1:T[]
        Field tArray = TypeTest.class.getField("tArray");
        System.out.println("数组参数类型1:" + tArray.getGenericType());

        //数组参数类型2:java.util.List<T>[]
        Field ltArray = TypeTest.class.getField("listTArray");
        System.out.println("数组参数类型2:" + ltArray.getGenericType());

        if (tArray.getGenericType() instanceof GenericArrayType) {

            GenericArrayType arrayType = (GenericArrayType) tArray.getGenericType();

            //数组参数类型3:T
            System.out.println("数组参数类型3:" + arrayType.getGenericComponentType());
            System.out.println(arrayType.getGenericComponentType().getClass());
        }

        if (ltArray.getGenericType() instanceof GenericArrayType) {

            GenericArrayType arrayType = (GenericArrayType) ltArray.getGenericType();

            //数组参数类型4:T
            System.out.println("数组参数类型4:" + arrayType.getGenericComponentType());
            System.out.println(arrayType.getGenericComponentType().getClass());
        }
    }

    /**
     * 带有?的通配符类型
     * @throws NoSuchFieldException
     */
    public static void testWildcardType() throws NoSuchFieldException {
        Field mapWithWildcard = TypeTest.class.getField("mapWithWildcard");

        //先获取属性的泛型类型 Map<? super String, ? extends Number>
        Type wild = mapWithWildcard.getGenericType();
        if (wild instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) wild;

            //获取<>里面的参数变量 ? super String, ? extends Number
            Type[] actualTypes = pType.getActualTypeArguments();
            System.out.println("WildcardType1:" + Arrays.asList(actualTypes));

            //? super java.lang.String
            WildcardType first = (WildcardType) actualTypes[0];

            //? extends java.lang.Number
            WildcardType second = (WildcardType) actualTypes[1];
            System.out.println("WildcardType2: lower:" + Arrays.asList(first.getLowerBounds()) + "  upper:" + Arrays.asList(first.getUpperBounds()));//WildcardType2: lower:[class java.lang.String]  upper:[class java.lang.Object]
            System.out.println("WildcardType3: lower:" + Arrays.asList(second.getLowerBounds()) + "  upper:" + Arrays.asList(second.getUpperBounds()));//WildcardType3: lower:[]  upper:[class java.lang.Number]
        }
    }

    public static void testClassGeneric() throws NoSuchFieldException {
        TypeVariable[] typeParameters = TypeTest.class.getTypeParameters();
        System.out.println(typeParameters[0].getTypeName());

    }



    public static List<SimpleClass> simpleClassList=new ArrayList<>();
    public static List<String> stringList=new ArrayList<>();

    /**
     * 测试泛型擦除
     * @throws NoSuchFieldException
     */
    public static void testGenericErasure() throws NoSuchFieldException {

        System.out.println(simpleClassList.getClass()==stringList.getClass());
        System.out.println(Main.class.getField("simpleClassList").getClass()==Main.class.getField("stringList").getClass());
        System.out.println(Main.class.getField("simpleClassList").getGenericType()==Main.class.getField("stringList").getGenericType());

        System.out.println(Main.class.getField("simpleClassList").getGenericType().getClass());
        System.out.println(((ParameterizedTypeImpl)Main.class.getField("simpleClassList").getGenericType()).getActualTypeArguments()[0].getTypeName());

    }

}

