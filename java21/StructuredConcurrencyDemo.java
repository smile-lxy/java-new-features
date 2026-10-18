// JEP 453: Structured Concurrency (preview in Java 21)
// Run: java --enable-preview --source 21 StructuredConcurrencyDemo.java

import java.util.concurrent.*;
import java.util.function.Supplier;

public class StructuredConcurrencyDemo {

    static long t0;

    static void log(String message) {
        System.out.printf("%5d ms  %s%n", (System.nanoTime() - t0) / 1_000_000, message);
    }

    record User(String id, String name) {}
    record Order(String userId, int count) {}

    static User findUser(String id) throws InterruptedException {
        Thread.sleep(200);
        if (id.equals("bad")) throw new IllegalStateException("user not found");
        return new User(id, "Alice");
    }

    static Order fetchOrder(String id) throws InterruptedException {
        try {
            Thread.sleep(3_000);
            log("fetchOrder: finished");
            return new Order(id, 3);
        } catch (InterruptedException e) {
            log("fetchOrder: interrupted");
            throw e;
        }
    }

    // [ja] ExecutorService + Future（キャンセルは呼び出し側の責任）
    // [zh] ExecutorService + Future（取消要由调用方自己负责）
    // [en] ExecutorService + Future (cancelling is the caller's job)
    static String withFuture(ExecutorService pool, String id) throws Exception {
        Future<Order> order = pool.submit(() -> fetchOrder(id));
        Future<User> user = pool.submit(() -> findUser(id));
        // [ja] user が失敗しても order は止まらない
        // [zh] 即使 user 失败，order 也不会停下来
        // [en] Even if user fails, order keeps running
        return user.get().name() + " / " + order.get().count() + " orders";
    }

    // [ja] StructuredTaskScope.ShutdownOnFailure
    // [zh] StructuredTaskScope.ShutdownOnFailure
    // [en] StructuredTaskScope.ShutdownOnFailure
    static String withScope(String id) throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Supplier<Order> order = scope.fork(() -> fetchOrder(id));
            Supplier<User> user = scope.fork(() -> findUser(id));
            // [ja] どれかが失敗したら残りを中断して例外を投げる
            // [zh] 任何一个失败，就中断其余任务并抛出异常
            // [en] If any subtask fails, interrupt the rest and throw
            scope.join().throwIfFailed();
            return user.get().name() + " / " + order.get().count() + " orders";
        }
    }

    // [ja] StructuredTaskScope.ShutdownOnSuccess：最初に成功した結果だけ使う
    // [zh] StructuredTaskScope.ShutdownOnSuccess：只用最先成功的结果
    // [en] StructuredTaskScope.ShutdownOnSuccess: use only the first successful result
    static String fastest() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            scope.fork(() -> { Thread.sleep(300); return "server A"; });
            scope.fork(() -> { Thread.sleep(100); return "server B"; });
            scope.fork(() -> { Thread.sleep(200); throw new IllegalStateException("server C is down"); });
            scope.join();
            return scope.result();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== 1. Success ===");
        t0 = System.nanoTime();
        log("StructuredTaskScope: " + withScope("u1"));

        System.out.println("=== 2. findUser fails: ExecutorService + Future ===");
        t0 = System.nanoTime();
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            try {
                withFuture(pool, "bad");
            } catch (ExecutionException e) {
                log("caller: failure detected -> " + e.getCause().getMessage());
            }
        }
        // [ja] close() は残ったタスクの終了を待つ
        // [zh] close() 会等待剩下的任务结束
        // [en] close() waits for the remaining tasks
        log("pool closed");

        System.out.println("=== 3. findUser fails: StructuredTaskScope ===");
        t0 = System.nanoTime();
        try {
            withScope("bad");
        } catch (ExecutionException e) {
            log("caller: failure detected -> " + e.getCause().getMessage());
        }
        log("scope closed");

        System.out.println("=== 4. Use the first success ===");
        t0 = System.nanoTime();
        log("result: " + fastest());
    }
}
