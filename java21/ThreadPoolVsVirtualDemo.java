// JEP 444: Virtual Threads - limits of a thread pool vs virtual threads on a single carrier
// Run: java ThreadPoolVsVirtualDemo.java

import java.time.Duration;
import java.util.concurrent.*;

public class ThreadPoolVsVirtualDemo {

    static final long START = System.nanoTime();

    static void log(String message) {
        String thread = Thread.currentThread().isVirtual()
                ? "virtual@" + Thread.currentThread().toString().replaceAll(".*@", "")
                : Thread.currentThread().getName();
        System.out.printf("%5d ms  %-32s  %s%n", (System.nanoTime() - START) / 1_000_000, thread, message);
    }

    static Runnable task(int n) {
        return new Runnable() {
            public void run() {
                log("task " + n + " start");
                try {
                    Thread.sleep(Duration.ofSeconds(1));
                } catch (InterruptedException ignored) {
                }
                log("task " + n + " end");
            }

            public String toString() {
                return "task " + n;
            }
        };
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== 1. Fixed pool (1 thread), 5 tasks of 1 s ===");
        try (ExecutorService pool = Executors.newFixedThreadPool(1)) {
            for (int i = 0; i < 5; i++) pool.execute(task(i));
        }

        System.out.println("=== 2. Bounded queue (1 thread, queue of 2), 5 tasks ===");
        ThreadPoolExecutor bounded = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(2),
                (r, executor) -> log("[REJECTED] " + r + " (queue is full)"));
        for (int i = 0; i < 5; i++) bounded.execute(task(i));
        bounded.shutdown();
        bounded.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("=== 3. Virtual threads (1 carrier), 5 tasks of 1 s ===");
        // [ja] 最初の仮想スレッドを作る前に設定する
        // [zh] 必须在创建第一个虚拟线程之前设置
        // [en] Must be set before the first virtual thread is created
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) executor.execute(task(i));
        }
    }
}
