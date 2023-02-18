package share.java.并发与锁;

import org.openjdk.jol.info.ClassLayout;

public class SynchronizedDemo {
    private static Object obj;
    static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    public static void main(String[] args) throws InterruptedException {

       Object testJvmStartUpObj = new Object();
       System.out.println("jvm 偏向锁功能启动前："+ClassLayout.parseInstance(testJvmStartUpObj).toPrintable());

        Thread.sleep(5000);

        obj = new Object();
        System.out.println("无竞争环境"+ClassLayout.parseInstance(obj).toPrintable());

        test_Thread();

        Thread.sleep(1000);
        System.out.println("无竞争环境"+ClassLayout.parseInstance(obj).toPrintable());

        new Thread(()->{
            synchronized (obj){
                System.out.println(Thread.currentThread().getName()+"---"+ClassLayout.parseInstance(obj).toPrintable());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        },"th2").start();

        Thread.sleep(5000);
        System.out.println("主线程"+ClassLayout.parseInstance(obj).toPrintable());


        new Thread(()->{
            synchronized (obj){
                System.out.println(Thread.currentThread().getName()+"---"+ClassLayout.parseInstance(obj).toPrintable());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        },"th3").start();

        Thread.sleep(2000);

        System.out.println("主线程"+ClassLayout.parseInstance(obj).toPrintable());
    }

    public static void test_Thread(){
        new Thread(()->{
            synchronized (obj){
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    System.out.println("wait 失败");
                }
                System.out.println(Thread.currentThread().getName()+"---"+ ClassLayout.parseInstance(obj).toPrintable());
                int a= obj.hashCode();
                System.out.println("hash 后"+ClassLayout.parseInstance(obj).toPrintable());
            }
        },"th1").start();
    }
}
