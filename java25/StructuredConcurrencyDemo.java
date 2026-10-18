// JEP 505: Structured Concurrency (fifth preview in JDK 25)
// Run: java --enable-preview StructuredConcurrencyDemo.java

import java.time.Duration;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class StructuredConcurrencyDemo {

    static long t0;

    static void log(String message) {
        System.out.printf("%5d ms  %s%n", (System.nanoTime() - t0) / 1_000_000, message);
    }

    static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    // [ja] ms ミリ秒かかる処理。中断されたらログを出す
    // [zh] 耗时 ms 毫秒的处理。被中断时输出日志
    // [en] A task that takes ms milliseconds; it logs when interrupted
    static String work(String name, long ms, boolean fail) throws InterruptedException {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            log(name + ": interrupted");
            throw e;
        }
        if (fail) {
            log(name + ": failed");
            throw new IllegalStateException(name + " failed");
        }
        log(name + ": done");
        return name;
    }

    public static void main(String[] args) throws Exception {
        // [ja] ① 既定の Joiner：全部成功するまで待つ。1 つでも失敗したら残りを中断する
        // [zh] ① 默认的 Joiner：等待全部成功。只要有一个失败，就中断其余的
        // [en] (1) The default joiner waits for all to succeed and interrupts the rest on the first failure
        System.out.println("=== 1. open(): one subtask fails ===");
        t0 = System.nanoTime();
        try (var scope = StructuredTaskScope.open()) {
            Subtask<String> user = scope.fork(() -> work("findUser", 100, true));
            Subtask<String> order = scope.fork(() -> work("fetchOrder", 1000, false));
            scope.join();
            log("never reached: " + user.get() + order.get());
        } catch (StructuredTaskScope.FailedException e) {
            log("caller: " + e.getCause().getMessage());
        }

        // [ja] ② 最初に成功した結果だけを使う（ミラーサーバーの競争など）
        // [zh] ② 只使用最先成功的结果（例如多个镜像服务器竞速）
        // [en] (2) Use only the first successful result (racing mirror servers, for example)
        System.out.println("=== 2. anySuccessfulResultOrThrow() ===");
        t0 = System.nanoTime();
        try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulResultOrThrow())) {
            scope.fork(() -> work("mirror A", 300, false));
            scope.fork(() -> work("mirror B", 100, false));
            scope.fork(() -> work("mirror C", 50, true));
            log("result: " + scope.join());
        }

        // [ja] ③ 全部の結果をまとめて受け取る
        // [zh] ③ 一次性拿到所有结果
        // [en] (3) Receive every result at once
        System.out.println("=== 3. allSuccessfulOrThrow() ===");
        t0 = System.nanoTime();
        try (var scope = StructuredTaskScope.open(Joiner.<String>allSuccessfulOrThrow())) {
            for (String s : List.of("price", "stock", "review")) {
                scope.fork(() -> work(s, 100, false));
            }
            List<String> results = scope.join().map(Subtask::get).toList();
            log("results: " + results);
        }

        // [ja] ④ スコープ全体にタイムアウトを設定する
        // [zh] ④ 给整个作用域设置超时
        // [en] (4) Set a timeout for the whole scope
        System.out.println("=== 4. withTimeout(250 ms) ===");
        t0 = System.nanoTime();
        try (var scope = StructuredTaskScope.open(Joiner.<String>allSuccessfulOrThrow(),
                cf -> cf.withTimeout(Duration.ofMillis(250)))) {
            scope.fork(() -> work("fast", 100, false));
            scope.fork(() -> work("slow", 1000, false));
            scope.join();
        } catch (StructuredTaskScope.TimeoutException e) {
            log("caller: TimeoutException");
        }

        // [ja] ⑤ fork したサブタスクにも ScopedValue が引き継がれる
        // [zh] ⑤ fork 出来的子任务也会继承 ScopedValue
        // [en] (5) Forked subtasks inherit ScopedValue bindings
        System.out.println("=== 5. ScopedValue inheritance ===");
        t0 = System.nanoTime();
        ScopedValue.where(REQUEST_ID, "req-42").call(() -> {
            try (var scope = StructuredTaskScope.open()) {
                Subtask<String> a = scope.fork(() -> "subtask A sees " + REQUEST_ID.get());
                Subtask<String> b = scope.fork(() -> "subtask B sees " + REQUEST_ID.get());
                scope.join();
                log(a.get());
                log(b.get());
            }
            return null;
        });
    }
}
