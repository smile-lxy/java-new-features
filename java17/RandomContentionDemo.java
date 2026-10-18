// JEP 356: Enhanced Pseudo-Random Number Generators - sharing one Random across threads vs split()
// Run: java RandomContentionDemo.java

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.SplittableGenerator;
import java.util.random.RandomGeneratorFactory;

public class RandomContentionDemo {

    static final int THREADS = 8;
    static final int PER_THREAD = 2_000_000;
    static volatile long sink;

    // [ja] THREADS 本のスレッドで、それぞれ PER_THREAD 回 nextLong() を呼び、かかった時間（ms）を返す
    // [zh] 用 THREADS 个线程，各调用 PER_THREAD 次 nextLong()，返回耗时（ms）
    // [en] Call nextLong() PER_THREAD times on each of THREADS threads; return the elapsed time (ms)
    static long run(IntFunction<RandomGenerator> generatorForThread) throws InterruptedException {
        Thread[] threads = new Thread[THREADS];
        long start = System.nanoTime();
        for (int t = 0; t < THREADS; t++) {
            int id = t;
            threads[t] = new Thread(() -> {
                // [ja] ジェネレーターはスレッドの中で取得する（ThreadLocalRandom.current() のため）
                // [zh] 在线程内部获取生成器（为了 ThreadLocalRandom.current()）
                // [en] Get the generator inside the thread (needed for ThreadLocalRandom.current())
                RandomGenerator g = generatorForThread.apply(id);
                long sum = 0;
                for (int i = 0; i < PER_THREAD; i++) sum += g.nextLong();
                sink += sum;
            });
            threads[t].start();
        }
        for (Thread th : threads) th.join();
        return (System.nanoTime() - start) / 1_000_000;
    }

    public static void main(String[] args) throws InterruptedException {
        Random shared = new Random(1);
        SplittableGenerator root = (SplittableGenerator) RandomGeneratorFactory.of("L64X128MixRandom").create(1);
        SplittableGenerator[] children = new SplittableGenerator[THREADS];
        for (int t = 0; t < THREADS; t++) children[t] = root.split();

        System.out.printf("%d threads x %,d nextLong() calls%n%n", THREADS, PER_THREAD);
        // [ja] 1 回目は JIT のウォームアップなので、2 回目以降の 3 回を表示する
        // [zh] 第 1 轮用于 JIT 预热，只显示之后的 3 轮
        // [en] The first round warms up the JIT; show the next three rounds
        for (int round = 0; round <= 3; round++) {
            // [ja] ① 1 つの Random を全スレッドで共有（内部の AtomicLong を CAS で奪い合う）
            // [zh] ① 所有线程共享 1 个 Random（争抢内部 AtomicLong 的 CAS）
            // [en] (1) All threads share one Random (they fight over its AtomicLong with CAS)
            long a = run(t -> shared);
            // [ja] ② ThreadLocalRandom（スレッドごとの状態）
            // [zh] ② ThreadLocalRandom（每个线程有自己的状态）
            // [en] (2) ThreadLocalRandom (per-thread state)
            long b = run(t -> ThreadLocalRandom.current());
            // [ja] ③ split() した L64X128MixRandom をスレッドごとに 1 つ
            // [zh] ③ 每个线程用 1 个 split() 出来的 L64X128MixRandom
            // [en] (3) One L64X128MixRandom from split() per thread
            long c = run(t -> children[t]);
            if (round > 0) {
                System.out.printf("round %d: shared Random %5d ms | ThreadLocalRandom %4d ms | split() %4d ms%n", round, a, b, c);
            }
        }
    }
}
