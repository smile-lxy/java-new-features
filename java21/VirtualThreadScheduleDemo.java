// JEP 444: Virtual Threads - run 4 virtual threads on 2 carrier threads and watch them mount / unmount
// Run: java VirtualThreadScheduleDemo.java

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class VirtualThreadScheduleDemo {

    static final long START = System.nanoTime();

    // [ja] [計算 ms, I/O ms, 計算 ms, ...] の順に実行する
    // [zh] 按 [计算 ms, I/O ms, 计算 ms, ...] 的顺序执行
    // [en] Executed in the order [compute ms, I/O ms, compute ms, ...]
    static final long[][] PLANS = {
            {2500, 500, 1000, 1500, 1300},
            {1800, 1000, 3800, 1500, 600},
            {2000, 500, 2500},
            {800, 500, 2800},
    };

    public static void main(String[] args) throws InterruptedException {
        // [ja] キャリアスレッドを 2 本に制限（最初の仮想スレッドを作る前に設定する）
        // [zh] 把载体线程限制为 2 个（必须在创建第一个虚拟线程之前设置）
        // [en] Limit the carrier threads to 2 (set before the first virtual thread is created)
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "2");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "2");

        CountDownLatch done = new CountDownLatch(PLANS.length);
        for (int i = 0; i < PLANS.length; i++) {
            int id = i + 1;
            long[] plan = PLANS[i];
            Thread.startVirtualThread(() -> {
                log(id, "start");
                for (int step = 0; step < plan.length; step++) {
                    if (step % 2 == 0) {
                        log(id, "compute " + plan[step] + " ms");
                        compute(plan[step]);
                    } else {
                        log(id, "I/O " + plan[step] + " ms (unmount)");
                        sleep(plan[step]);
                        log(id, "I/O done (remount)");
                    }
                }
                log(id, "done");
                done.countDown();
            });
        }
        done.await();
    }

    // [ja] CPU を使い続ける（仮想スレッドはマウントされたまま）
    // [zh] 持续占用 CPU（虚拟线程一直挂载在载体线程上）
    // [en] Keep the CPU busy (the virtual thread stays mounted)
    static void compute(long ms) {
        long end = System.nanoTime() + ms * 1_000_000;
        double sink = 0;
        while (System.nanoTime() < end) {
            sink += Math.sqrt(sink + 1);
        }
    }

    // [ja] ブロッキング I/O の代わり（仮想スレッドはアンマウントされる）
    // [zh] 代替阻塞 I/O（虚拟线程会被卸载）
    // [en] Stands in for blocking I/O (the virtual thread unmounts)
    static void sleep(long ms) {
        try {
            Thread.sleep(Duration.ofMillis(ms));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static synchronized void log(int id, String message) {
        long elapsed = (System.nanoTime() - START) / 1_000_000;
        // [ja] toString() は「VirtualThread[#id]/状態@キャリアスレッド名」の形式
        // [zh] toString() 的格式是「VirtualThread[#id]/状态@载体线程名」
        // [en] toString() looks like "VirtualThread[#id]/state@carrier-thread-name"
        String carrier = Thread.currentThread().toString().replaceAll(".*@", "");
        System.out.printf("%5d ms  %-23s  VT-%d  %s%n", elapsed, carrier, id, message);
    }
}
