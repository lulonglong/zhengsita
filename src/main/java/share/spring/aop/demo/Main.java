package share.spring.aop.demo;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Main {

    public static void main(String[] args) {

        testHello();
        System.out.println();

        testStaticProxy();
        System.out.println();

        testJDKDynamicProxy();
        System.out.println();

        testCGLIBProxy();
        System.out.println();
    }

    private static void testHello() {
        HelloService helloService = new HelloServiceImpl();
        helloService.hello();
    }

    private static void testStaticProxy() {
        HelloService helloService = new HelloServiceStaticProxy(new HelloServiceImpl());
        helloService.hello();
    }

    private static void testJDKDynamicProxy() {
        // 当前线程的类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        HelloService helloService = (HelloService) Proxy.newProxyInstance(classLoader, new Class[]{HelloService.class}, new XXXInvocationHandler(new HelloServiceImpl()));
        helloService.hello();
    }

    private static void testCGLIBProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloServiceImpl.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object proxy, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("cglib-插前边");
                Object object = methodProxy.invokeSuper(proxy, objects);
                System.out.println("cglib-插后边");
                return object;
            }
        });

        HelloService helloService= (HelloService)enhancer.create();
        helloService.hello();
    }

}
