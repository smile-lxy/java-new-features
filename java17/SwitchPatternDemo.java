// JEP 406: Pattern Matching for switch (preview in Java 17)
// Run: java --enable-preview --source 17 SwitchPatternDemo.java

import java.util.Arrays;

public class SwitchPatternDemo {

    sealed interface Shape permits Circle, Square, Rect {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}
    record Rect(double width, double height) implements Shape {}

    // [ja] 従来：instanceof の if-else を連ねる
    // [zh] 以前的写法：一连串 instanceof 的 if-else
    // [en] Before: a chain of instanceof if-else
    static String formatOld(Object o) {
        if (o instanceof Integer i) {
            return "int " + i;
        } else if (o instanceof Long l) {
            return "long " + l;
        } else if (o instanceof String s) {
            return "String \"" + s + "\"";
        }
        return "other";
    }

    // [ja] Java 17（プレビュー）：case に型パターンを書ける。case null も書ける
    // [zh] Java 17（预览）：case 里可以写类型模式，也可以写 case null
    // [en] Java 17 (preview): case labels take type patterns, and case null is allowed
    static String formatNew(Object o) {
        return switch (o) {
            case null      -> "null!";
            case Integer i -> "int " + i;
            case Long l    -> "long " + l;
            case String s  -> "String \"" + s + "\"";
            case int[] a   -> "int[] " + Arrays.toString(a);
            default        -> "other";
        };
    }

    // [ja] case null がない switch は、これまでどおり null で NullPointerException を投げる
    // [zh] 没有 case null 的 switch 遇到 null 时，和以前一样抛出 NullPointerException
    // [en] A switch without case null still throws NullPointerException on null, as before
    static String withoutNullCase(Object o) {
        return switch (o) {
            case String s -> "String";
            default       -> "other";
        };
    }

    // [ja] ガード付きパターン：Java 17 では && で条件を付ける（JDK 19 で when に変わった）
    // [zh] 带守卫的模式：Java 17 用 && 加条件（JDK 19 改成了 when）
    // [en] Guarded patterns: Java 17 adds the condition with && (JDK 19 changed it to when)
    static String size(Shape shape) {
        return switch (shape) {
            case Circle c && c.radius() > 10 -> "large circle";
            case Circle c                    -> "small circle";
            case Square s                    -> "square " + s.side();
            case Rect r                      -> "rect " + r.width() + "x" + r.height();
            // [ja] Shape は sealed なので default は不要（網羅性をコンパイラが確認する）
            // [zh] Shape 是 sealed 的，所以不需要 default（编译器会检查是否穷尽）
            // [en] Shape is sealed, so no default is needed (the compiler checks exhaustiveness)
        };
    }

    public static void main(String[] args) {
        Object[] values = {42, 42L, "hello", new int[] {1, 2}, 3.14, null};
        System.out.printf("%-16s | %s%n", "formatNew", "formatOld");
        for (Object v : values) {
            System.out.printf("%-16s | %s%n", formatNew(v), formatOld(v));
        }
        try {
            withoutNullCase(null);
        } catch (NullPointerException e) {
            System.out.println("withoutNullCase(null) -> NullPointerException");
        }
        System.out.println();
        for (Shape s : new Shape[] {new Circle(20), new Circle(1), new Square(2), new Rect(2, 3)}) {
            System.out.println(s + " -> " + size(s));
        }
    }
}
