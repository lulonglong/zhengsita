package share.java.base;

public class TryDemo {

    public static void main(String[] args) {

        Integer a = 128, b = 128, c = 127, d = 127;
        System.out.println(a == Integer.parseInt("128"));
        System.out.println(c == Integer.parseInt("127"));


        System.out.println(test1());
        System.out.println(test2());
    }

    public static int test1() {
        try {
            return 2;
        } finally {
            return 3;
        }
    }

    public static int test2() {
        int i = 0;
        try {
            i = 2;
            return i;
        } finally {
            i = 3;
        }
    }
}
