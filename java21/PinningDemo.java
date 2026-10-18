// JEP 444: Virtual Threads - pinning caused by synchronized (JDK 21-23)
// Run: java -Djdk.tracePinnedThreads=short PinningDemo.java

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class PinningDemo {

    static void sleep1s() {
        try {
            Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException ignored) {
        }
    }

    // [ja] ロックを持ったまま 1 s ブロックするタスクを 2 つ実行する（ロックはタスクごとに別物なので競合はない）
    // [zh] 运行 2 个持有锁并阻塞 1 s 的任务（每个任务用各自的锁，所以没有锁竞争）
    // [en] Run 2 tasks that block for 1 s while holding a lock (each task has its own lock, so there is no contention)
    static void run(String label, Runnable body) {
        long start = System.nanoTime();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.execute(body);
            executor.execute(body);
        }
        System.out.printf("%-14s %5d ms%n", label, (System.nanoTime() - start) / 1_000_000);
    }

    public static void main(String[] args) {
        // [ja] キャリアスレッドを 1 本にして違いを見やすくする
        // [zh] 只用 1 个载体线程，让差异更明显
        // [en] Use a single carrier thread to make the difference obvious
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");

        run("synchronized", () -> {
            Object lock = new Object();
            synchronized (lock) {
                // [ja] JDK 21：キャリアごと固定される
                // [zh] JDK 21：连同载体线程一起被固定
                // [en] JDK 21: the carrier is pinned as well
                sleep1s();
            }
        });

        run("ReentrantLock", () -> {
            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            try {
                // [ja] アンマウントできる
                // [zh] 可以卸载
                // [en] Can unmount
                sleep1s();
            } finally {
                lock.unlock();
            }
        });
    }
}
