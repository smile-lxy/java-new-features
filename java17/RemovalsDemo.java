// JEP 398 / 407 / 410 / 411: deprecations and removals in Java 17
// Run: java RemovalsDemo.java

import java.nio.file.Files;
import java.nio.file.Path;

public class RemovalsDemo {

    static void printDeprecation(String className) {
        try {
            Deprecated d = Class.forName(className).getAnnotation(Deprecated.class);
            if (d == null) {
                System.out.printf("%-34s not deprecated%n", className);
                return;
            }
            System.out.printf("%-34s @Deprecated(since=\"%s\", forRemoval=%s)%n", className, d.since(), d.forRemoval());
        } catch (ClassNotFoundException e) {
            System.out.printf("%-34s ClassNotFoundException%n", className);
        }
    }

    // [ja] 削除予定の API を使うと javac が [removal] 警告を出すので、ここでは抑止している
    // [zh] 使用将要删除的 API 时 javac 会给出 [removal] 警告，这里将其屏蔽
    // [en] javac warns with [removal] when code uses an API marked for removal; suppress it here
    @SuppressWarnings("removal")
    public static void main(String[] args) {
        // [ja] JEP 398：Applet API は「削除予定」になった
        // [zh] JEP 398：Applet API 被标记为“将要删除”
        // [en] JEP 398: the Applet API is now deprecated for removal
        printDeprecation("java.applet.Applet");

        // [ja] JEP 411：Security Manager も「削除予定」になった
        // [zh] JEP 411：Security Manager 也被标记为“将要删除”
        // [en] JEP 411: the Security Manager is deprecated for removal too
        printDeprecation("java.lang.SecurityManager");

        // [ja] JEP 407：RMI Activation は削除された（java.rmi のほかの機能は残る）
        // [zh] JEP 407：RMI Activation 已被删除（java.rmi 的其他功能仍保留）
        // [en] JEP 407: RMI Activation is removed (the rest of java.rmi stays)
        printDeprecation("java.rmi.activation.Activatable");
        printDeprecation("java.rmi.Remote");

        // [ja] JEP 410：実験的な AOT コンパイラ（jaotc）と Graal JIT が削除された
        // [zh] JEP 410：实验性的 AOT 编译器（jaotc）和 Graal JIT 被删除
        // [en] JEP 410: the experimental AOT compiler (jaotc) and the Graal JIT are removed
        Path bin = Path.of(System.getProperty("java.home"), "bin");
        System.out.printf("%-34s %s%n", "bin/jaotc exists", Files.exists(bin.resolve("jaotc")));
        System.out.printf("%-34s %s%n", "module jdk.internal.vm.compiler", ModuleLayer.boot().findModule("jdk.internal.vm.compiler").isPresent());

        // [ja] Security Manager を有効にしようとすると、警告が出る（標準エラー出力）
        // [zh] 尝试启用 Security Manager 时会输出警告（标准错误输出）
        // [en] Trying to enable the Security Manager prints a warning (on standard error)
        System.out.println();
        System.out.flush();
        System.setSecurityManager(new SecurityManager());
        System.out.println("Security Manager installed: " + (System.getSecurityManager() != null));
    }
}
