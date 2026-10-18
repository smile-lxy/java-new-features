// JEP 507: Primitive Types in Patterns, instanceof, and switch (third preview in JDK 25)
// Run: java --enable-preview PrimitivePatternsDemo.java

public class PrimitivePatternsDemo {

    // [ja] instanceof int：値を情報の欠落なしに int へ変換できるかを調べる
    // [zh] instanceof int：检查值能否无损地转换为 int
    // [en] instanceof int: checks whether the value converts to int without losing information
    static String fitsInInt(long value) {
        if (value instanceof int i) {
            return value + " fits in int: " + i;
        }
        return value + " does not fit in int";
    }

    // [ja] double も同じ。小数部がなく範囲内なら int に一致する
    // [zh] double 也一样。没有小数部分且在范围内，就能匹配 int
    // [en] Same for double: it matches int if it has no fraction and is in range
    static String classify(double d) {
        return switch (d) {
            case int i -> "integral " + i;
            case float f -> "fits in float " + f;
            case double x -> "needs double " + x;
        };
    }

    // [ja] switch にプリミティブ型パターンと when ガードを組み合わせる
    // [zh] 在 switch 中组合基本类型模式和 when 守卫
    // [en] Combine primitive type patterns with when guards in a switch
    static String httpStatus(int code) {
        return switch (code) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case int c when c >= 500 -> "server error " + c;
            case int c -> "other " + c;
        };
    }

    // [ja] boolean・long なども switch のセレクターにできるようになった
    // [zh] boolean、long 等也可以作为 switch 的选择器了
    // [en] boolean, long and others can now be switch selectors
    static String onOff(boolean b) {
        return switch (b) {
            case true -> "ON";
            case false -> "OFF";
        };
    }

    public static void main(String[] args) {
        System.out.println(fitsInInt(42L));
        System.out.println(fitsInInt(10_000_000_000L));
        System.out.println(classify(3.0));
        System.out.println(classify(0.5));
        System.out.println(classify(0.1));
        System.out.println(httpStatus(200) + " / " + httpStatus(503) + " / " + httpStatus(302));
        System.out.println(onOff(true) + " / " + onOff(false));

        // [ja] 従来のキャストは黙って値を壊す
        // [zh] 以前的强制转换会悄悄地破坏值
        // [en] A plain cast silently corrupts the value
        System.out.println("(int) 10_000_000_000L = " + (int) 10_000_000_000L);
    }
}
