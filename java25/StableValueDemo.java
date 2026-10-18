// JEP 502: Stable Values (preview in JDK 25)
// Run: java --enable-preview StableValueDemo.java

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class StableValueDemo {

    static final AtomicInteger CREATED = new AtomicInteger();

    static class Settings {
        Settings() {
            // [ja] 重い初期化のつもり（100 ms）
            // [zh] 假装初始化很耗时（100 ms）
            // [en] Pretend this is expensive (100 ms)
            CREATED.incrementAndGet();
            sleep(100);
        }
    }

    // [ja] 「1 回だけ設定できる値」。final フィールドのように JIT が定数として扱える
    // [zh] “只能设置一次的值”。JIT 可以像 final 字段一样把它当作常量
    // [en] A value that can be set only once; the JIT can treat it as a constant, like a final field
    static final StableValue<Settings> SETTINGS = StableValue.of();

    static Settings settings() {
        // [ja] 未設定なら初期化する。複数スレッドが同時に呼んでも、初期化は 1 回だけ
        // [zh] 如果还没设置就初始化。即使多个线程同时调用，也只初始化一次
        // [en] Initialize if not set yet; even with many threads calling at once, it runs only once
        return SETTINGS.orElseSet(Settings::new);
    }

    // [ja] Supplier として使う書き方
    // [zh] 作为 Supplier 使用的写法
    // [en] The same idea as a Supplier
    static final Supplier<String> GREETING = StableValue.supplier(() -> {
        System.out.println("  computing greeting...");
        return "hello";
    });

    // [ja] 要素ごとに遅延初期化されるリスト
    // [zh] 每个元素都延迟初始化的列表
    // [en] A list whose elements are initialized lazily, one by one
    static final List<Integer> SQUARES = StableValue.list(10, i -> {
        System.out.println("  computing square of " + i);
        return i * i;
    });

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("(1) 8 threads call settings() at the same time");
        Thread[] threads = new Thread[8];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = Thread.ofVirtual().start(StableValueDemo::settings);
        }
        for (Thread t : threads) t.join();
        System.out.println("  Settings created " + CREATED.get() + " time(s), isSet=" + SETTINGS.isSet());

        System.out.println("(2) supplier");
        System.out.println("  " + GREETING.get() + " / " + GREETING.get());

        System.out.println("(3) list");
        System.out.println("  SQUARES.get(3) = " + SQUARES.get(3));
        System.out.println("  SQUARES.get(3) = " + SQUARES.get(3));
        System.out.println("  SQUARES.get(7) = " + SQUARES.get(7));

        // [ja] 2 回目の設定は失敗する
        // [zh] 第二次设置会失败
        // [en] A second set fails
        System.out.println("(4) trySet on a set value -> " + SETTINGS.trySet(new Settings()));
    }
}
