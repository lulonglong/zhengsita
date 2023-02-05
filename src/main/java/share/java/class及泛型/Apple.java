package share.java.class及泛型;

public class Apple implements Fruit<Integer> {
    @Override
    public Integer get(Integer param) {
        return param;
    }

    public static void main(String[] args) {
        Apple apple=new Apple();
        System.out.println(apple.get(1));
    }
}
