// JEP 306: Restore Always-Strict Floating-Point Semantics
// Run: java StrictFpDemo.java

public class StrictFpDemo {

    // [ja] Java 16 までは、strictfp の有無で中間結果の扱いが変わる可能性があった
    // [zh] 在 Java 16 之前，有没有 strictfp 可能会影响中间结果的处理方式
    // [en] Up to Java 16, strictfp could change how intermediate results were handled
    static strictfp double withStrictfp(double x) {
        return x * 10 / 10;
    }

    // [ja] Java 17 からは、strictfp がなくても常に厳密（IEEE 754）に計算される
    // [zh] 从 Java 17 开始，即使没有 strictfp，也总是严格按 IEEE 754 计算
    // [en] Since Java 17, every expression is evaluated strictly (IEEE 754), with or without strictfp
    static double withoutStrictfp(double x) {
        return x * 10 / 10;
    }

    public static void main(String[] args) {
        // [ja] x * 10 は double の最大値（約 1.8e308）を超えるので、途中で Infinity になる
        // [zh] x * 10 超过了 double 的最大值（约 1.8e308），所以中途就变成 Infinity
        // [en] x * 10 exceeds the largest double (about 1.8e308), so it becomes Infinity midway
        double x = 1e308;
        System.out.println("with strictfp    : " + withStrictfp(x));
        System.out.println("without strictfp : " + withoutStrictfp(x));
        System.out.println("0.1 + 0.2        : " + (0.1 + 0.2));
    }
}
