package share.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentDemo {

    public volatile boolean v;

    public boolean tempShare = false;

    public boolean tempShare1;


    public void write() {

        //插入 StoreStore (刷Store Buffer)
        v = tempShare;
        //插入 StoreLoad（全能屏障，刷Store Buffer，刷Invalidate Queue）
    }

    public void read() {
        //插入 LoadLoad（刷Invalidate Queue ）
        tempShare1 = v;
        //插入 LoadStore（实质同loadload，只是抽象概念，刷Invalidate Queue）
    }

    // **************************Synchronized的使用方式**************************
    public synchronized void testSynchronized1() {
        int a = 1;
    }

    public void testSynchronized2() {
        synchronized (this) {
            int a = 1;
        }
    }

    // **************************ReentrantLock的使用方式**************************
    public void testReentrantLock() throws InterruptedException {
        // 1.初始化选择公平锁、非公平锁
        ReentrantLock lock = new ReentrantLock(true);
        // 2.可用于代码块
        lock.lock();
        try {
            try {
                // 3.支持多种加锁方式，比较灵活; 具有可重入特性
                if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                }
            } finally {
                // 4.手动释放锁
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * 指令重排：代码执行顺序与预期不一致 (发生在前后行代码无联系时)
     * <p>
     * 目的：提高性能
     */

    public static class HappenBefore {//undefined

        private static int a = 0;

        private static boolean flag = false;

        public static void main(String[] args) throws InterruptedException {//undefined

            for (int i = 0; i < 10; i++) {//undefined

                a = 0;

                flag = false;

                //线程一：更改数据

                Thread t1 = new Thread(() -> {//undefined

                    a = 1;

                    flag = true;

                });

                //线程二：读取数据

                Thread t2 = new Thread(() -> {//undefined

                    if (flag) {//undefined

                        a *= 1;

                    }

                    //指令重排

                    if (a == 0) {//undefined

                        System.out.println("happen-before->" + a);

                    }

                });

                t1.start();

                t2.start();

                t1.join();

                t2.join();

            }

        }

    }
}
