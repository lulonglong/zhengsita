package share.java.base;

public class InitDemo {

    static Table table = new Table();
    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("main()");
        table.otherMethod(1);
    }

}


class Bowl {
    Bowl(int marker) {
        System.out.println("Bowl(" + marker + ")");
    }
}

class Tableware {
    static {
        System.out.println("静态代码块1");
    }

    static Bowl bowl5 = new Bowl(5);

    static {
        System.out.println("Tableware静态代码块");
    }


    Tableware() {
        System.out.println("Tableware构造方法");
    }

    Bowl bowl4 = new Bowl(4);
}

class Table extends Tableware {
    {
        System.out.println("Table非静态代码块_1");
    }

    Bowl bowl3 = new Bowl(3);    // 9

    {
        System.out.println("Table非静态代码块_2");
    }

    static Bowl bowl2 = new Bowl(2);

    static {
        System.out.println("Table静态代码块");
    }

    Table() {
        System.out.println("Table构造方法");
    }

    static Bowl bowl1 = new Bowl(1);

    void otherMethod(int marker) {
        System.out.println("otherMethod(" + marker + ")");
    }

}
