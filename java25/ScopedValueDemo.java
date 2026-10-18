// JEP 506: Scoped Values
// Run: java ScopedValueDemo.java

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScopedValueDemo {

    record User(String name, String role) {}

    // [ja] 「リクエスト中のユーザー」を、引数で受け渡さずに共有する
    // [zh] 不通过参数传递，而是共享“当前请求的用户”
    // [en] Share "the user of this request" without passing it as an argument
    static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();
    static final ThreadLocal<User> TL_USER = new ThreadLocal<>();

    static void controller() {
        service();
    }

    static void service() {
        // [ja] 呼び出し階層の深いところで値を読む
        // [zh] 在调用层级很深的地方读取值
        // [en] Read the value deep in the call chain
        User u = CURRENT_USER.get();
        System.out.println("  service: user=" + u.name() + ", role=" + u.role());
    }

    public static void main(String[] args) throws Exception {
        // [ja] ① where(...).run(...) の中だけで値が見える
        // [zh] ① 值只在 where(...).run(...) 内部可见
        // [en] (1) The value is visible only inside where(...).run(...)
        System.out.println("(1) bind and read");
        ScopedValue.where(CURRENT_USER, new User("alice", "admin")).run(ScopedValueDemo::controller);
        System.out.println("  after run: isBound=" + CURRENT_USER.isBound());

        // [ja] ② 入れ子で別の値を一時的に束縛できる。抜けると元に戻る
        // [zh] ② 可以嵌套地临时绑定另一个值，退出后自动恢复
        // [en] (2) Rebind temporarily in a nested scope; the old value comes back on exit
        System.out.println("(2) nested rebinding");
        ScopedValue.where(CURRENT_USER, new User("alice", "admin")).run(() -> {
            ScopedValue.where(CURRENT_USER, new User("guest", "viewer")).run(ScopedValueDemo::service);
            service();
        });

        // [ja] ③ call() で値を返せる
        // [zh] ③ 用 call() 可以返回值
        // [en] (3) call() returns a value
        String greeting = ScopedValue.where(CURRENT_USER, new User("bob", "user"))
                .call(() -> "hello, " + CURRENT_USER.get().name());
        System.out.println("(3) call() -> " + greeting);

        // [ja] ④ ThreadLocal は remove() を忘れると、スレッドプールで次のタスクに値が漏れる
        // [zh] ④ ThreadLocal 如果忘了 remove()，在线程池中值会泄漏给下一个任务
        // [en] (4) If you forget remove(), a ThreadLocal leaks into the next task on a pooled thread
        System.out.println("(4) ThreadLocal vs ScopedValue on a pooled thread");
        try (ExecutorService pool = Executors.newSingleThreadExecutor()) {
            pool.submit(() -> TL_USER.set(new User("carol", "admin"))).get();
            pool.submit(() -> System.out.println("  next task sees ThreadLocal: " + TL_USER.get())).get();
            pool.submit(() -> ScopedValue.where(CURRENT_USER, new User("carol", "admin")).run(() -> {})).get();
            pool.submit(() -> System.out.println("  next task sees ScopedValue: "
                    + (CURRENT_USER.isBound() ? CURRENT_USER.get() : "(unbound)"))).get();
        }

        // [ja] ⑤ 束縛の外で get() すると例外
        // [zh] ⑤ 在绑定范围之外调用 get() 会抛出异常
        // [en] (5) get() outside a binding throws
        try {
            CURRENT_USER.get();
        } catch (java.util.NoSuchElementException e) {
            System.out.println("(5) get() outside: " + e.getClass().getSimpleName());
        }
    }
}
