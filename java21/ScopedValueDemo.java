// JEP 446: Scoped Values (preview in Java 21)
// Run: java --enable-preview --source 21 ScopedValueDemo.java

import java.util.concurrent.StructuredTaskScope;

public class ScopedValueDemo {

    static final ThreadLocal<String> TL = new ThreadLocal<>();
    static final ScopedValue<String> SV = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("=== 1. ThreadLocal: a value you set stays from then on ===");
        TL.set("a");
        threadLocalFlow();
        TL.remove();

        System.out.println("=== 2. ScopedValue: the value exists only inside the scope ===");
        ScopedValue.where(SV, "a").run(ScopedValueDemo::scopedValueFlow);
        System.out.println("  outside the scope: isBound = " + SV.isBound());

        System.out.println("=== 3. The binding is immutable, but the object's contents are not ===");
        mutableObject();

        System.out.println("=== 4. Inherited automatically by StructuredTaskScope subtasks ===");
        inheritance();
    }

    static void threadLocalFlow() {
        System.out.println("  " + TL.get()); // a
        // [ja] ここで書き換えると……
        // [zh] 如果在这里改掉……
        // [en] If we overwrite it here...
        TL.set("b");
        System.out.println("  " + TL.get()); // b
        // [ja] 呼び出し先も b のまま
        // [zh] 被调用的方法里也一直是 b
        // [en] ...callees keep seeing b too
        print();
    }

    static void print() {
        System.out.println("  " + TL.get());
    }

    static void scopedValueFlow() {
        System.out.println("  " + SV.get()); // a
        // [ja] 内側のスコープでだけ b
        // [zh] 只有在内层作用域里是 b
        // [en] b only inside the inner scope
        ScopedValue.where(SV, "b").run(() -> System.out.println("  " + SV.get()));
        // [ja] 抜けると a に戻る
        // [zh] 离开后又变回 a
        // [en] back to a after leaving it
        System.out.println("  " + SV.get());
    }

    // [ja] わざと可変にしたクラス
    // [zh] 故意设计成可变的类
    // [en] A deliberately mutable class
    static class Context {
        String name;
        Context(String name) { this.name = name; }
        public String toString() { return "Context{name=" + name + "}"; }
    }

    static final ScopedValue<Context> CTX = ScopedValue.newInstance();

    static void mutableObject() {
        ScopedValue.where(CTX, new Context("a")).run(() -> {
            System.out.println("  before: " + CTX.get());
            // [ja] 中身を書き換える
            // [zh] 修改对象的内容
            // [en] Change the object's contents
            CTX.get().name = "b";
            System.out.println("  after : " + CTX.get() + "  <- visible to everyone in the same scope");
        });
    }

    static void inheritance() throws Exception {
        TL.set("REQ-123");
        ScopedValue.where(SV, "REQ-123").run(() -> {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                var a = scope.fork(() -> describe("fork-A"));
                var b = scope.fork(() -> describe("fork-B"));
                scope.join().throwIfFailed();
                System.out.println("  " + a.get());
                System.out.println("  " + b.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        TL.remove();
    }

    static String describe(String name) {
        return name + ": ScopedValue=" + SV.get() + ", ThreadLocal=" + TL.get();
    }
}
