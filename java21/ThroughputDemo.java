// JEP 444: Virtual Threads - 10,000 tasks that block for 1 s: fixed thread pool vs virtual threads
// Run: java ThroughputDemo.java

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ThroughputDemo {

    static void run(String label, ExecutorService executor) {
        long start = System.nanoTime();
        try (executor) {
            IntStream.range(0, 10_000).forEach(i -> executor.submit(() -> {
                Thread.sleep(Duration.ofSeconds(1));
                return i;
            }));
        }
        // [ja] close() で全タスクの完了を待ってから時間を測る
        // [zh] close() 等所有任务完成后再计时
        // [en] close() waits for every task before we take the time
        System.out.printf("%-28s %6d ms%n", label, (System.nanoTime() - start) / 1_000_000);
    }

    public static void main(String[] args) {
        run("newFixedThreadPool(200)", Executors.newFixedThreadPool(200));
        run("newVirtualThreadPerTask", Executors.newVirtualThreadPerTaskExecutor());
    }
}
