package share.java.base;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Yinyong {

    public static void main(String[] args) {
        phantomReference();
    }

    public static void phantomReference(){
        ReferenceQueue<MyObject> referenceQueue = new ReferenceQueue();

        PhantomReference<MyObject> phantomReference = new PhantomReference<>(new MyObject("abc"),referenceQueue);
        System.out.println(phantomReference.get());

        List<byte[]> list = new ArrayList<>();

        new Thread(() -> {
            while (true)
            {
                list.add(new byte[1 * 1024 * 1024]);
                try { TimeUnit.MILLISECONDS.sleep(600); }
                catch (InterruptedException e) { e.printStackTrace(); }

               // System.out.println(phantomReference.get());
            }
        },"t1").start();

        new Thread(() -> {
            while (true)
            {
                System.gc();
                Reference<? extends MyObject> reference = referenceQueue.poll();
                if (reference != null) {
                    MyObject my= reference.get();
                    System.out.println(my.name);
                }
            }
        },"t2").start();

        //暂停几秒钟线程
        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
class MyObject{
    public String name;
    public MyObject(String name){
        this.name=name;
    }
}