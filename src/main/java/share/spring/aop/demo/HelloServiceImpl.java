package share.spring.aop.demo;

public class HelloServiceImpl implements HelloService {

    @Override
    public void hello() {
        System.out.println("hello");
    }
}
