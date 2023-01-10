package share.spring.aop.demo;

public class HelloServiceStaticProxy implements HelloService{
    private HelloService helloService;

    public HelloServiceStaticProxy(HelloService helloService) {
        this.helloService = helloService;
    }

    @Override
    public void hello() {
        System.out.println("静态代理-插前边");
        helloService.hello();
        System.out.println("静态代理-插后边");
    }
}
