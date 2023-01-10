package share.spring.aop.demo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class XXXInvocationHandler implements InvocationHandler {

    private Object targetObject;

    public XXXInvocationHandler(Object targetObject) {
        this.targetObject = targetObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("JDK-插前边");
        Object result= method.invoke(targetObject, args);
        System.out.println("JDK-插后边");
        return result;
    }
}
