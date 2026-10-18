// JEP 444: Virtual Threads - four ways to create a virtual thread
// Run: java VirtualThreadCreateDemo.java

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class VirtualThreadCreateDemo {

    public static void main(String[] args) throws Exception {
        Runnable task = () -> System.out.println("  " + Thread.currentThread());

        System.out.println("1. Thread.startVirtualThread");
        Thread.startVirtualThread(task).join();

        System.out.println("2. Thread.ofVirtual().name(...).start");
        Thread.ofVirtual().name("my-virtual").start(task).join();

        System.out.println("3. ThreadFactory");
        ThreadFactory factory = Thread.ofVirtual().name("worker-", 0).factory();
        Thread t = factory.newThread(task);
        t.start();
        t.join();

        System.out.println("4. Executors.newVirtualThreadPerTaskExecutor");
        // [ja] try-with-resources を抜けるとき、close() が全タスクの完了を待つ
        // [zh] 离开 try-with-resources 时，close() 会等待所有任务完成
        // [en] Leaving try-with-resources, close() waits for all tasks to finish
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(task);
            executor.submit(task);
        }

        System.out.println("For comparison: a platform thread");
        Thread.ofPlatform().start(task).join();
        System.out.println("isVirtual = " + Thread.ofVirtual().unstarted(task).isVirtual());
    }
}
