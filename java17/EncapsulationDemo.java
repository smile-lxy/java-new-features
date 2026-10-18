// JEP 403: Strongly Encapsulate JDK Internals
// Run: java EncapsulationDemo.java

import java.lang.reflect.Field;

public class EncapsulationDemo {

    public static void main(String[] args) throws Exception {
        // [ja] ① java.lang.String の private フィールド value にリフレクションでアクセスする
        // [zh] ① 通过反射访问 java.lang.String 的私有字段 value
        // [en] (1) Access the private field value of java.lang.String with reflection
        Field value = String.class.getDeclaredField("value");
        try {
            value.setAccessible(true);
            byte[] bytes = (byte[]) value.get("hello");
            System.out.println("(1) String.value opened: " + bytes.length + " bytes");
        } catch (RuntimeException e) {
            System.out.println("(1) " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // [ja] ② JDK 内部パッケージ（sun.nio.ch）のクラスもリフレクションでは開けない
        // [zh] ② JDK 内部包（sun.nio.ch）里的类，同样无法通过反射打开
        // [en] (2) Classes in JDK internal packages (sun.nio.ch) cannot be opened with reflection either
        try {
            Class<?> util = Class.forName("sun.nio.ch.Util");
            util.getDeclaredMethod("getTemporaryDirectBuffer", int.class).setAccessible(true);
            System.out.println("(2) sun.nio.ch.Util opened");
        } catch (RuntimeException e) {
            System.out.println("(2) " + e.getClass().getSimpleName());
        }

        // [ja] ③ sun.misc.Unsafe は jdk.unsupported モジュールにあり、Java 17 でも使える（例外扱い）
        // [zh] ③ sun.misc.Unsafe 在 jdk.unsupported 模块中，Java 17 仍可使用（属于例外）
        // [en] (3) sun.misc.Unsafe lives in the jdk.unsupported module and still works in Java 17 (an exception)
        Field theUnsafe = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        System.out.println("(3) sun.misc.Unsafe: " + theUnsafe.get(null).getClass().getName());

        // [ja] ④ java.base が java.lang パッケージを無名モジュールに open しているか
        // [zh] ④ java.base 是否向未命名模块 open 了 java.lang 包
        // [en] (4) Does java.base open the java.lang package to the unnamed module?
        Module base = String.class.getModule();
        System.out.println("(4) java.lang opened to us: " + base.isOpen("java.lang", EncapsulationDemo.class.getModule()));
    }
}
