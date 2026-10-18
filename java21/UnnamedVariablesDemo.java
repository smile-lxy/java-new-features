// JEP 443: Unnamed Patterns and Variables (preview in Java 21)
// Run: java --enable-preview --source 21 UnnamedVariablesDemo.java

import java.util.List;

public class UnnamedVariablesDemo {

    record Point(int x, int y) {}
    record Line(Point from, Point to) {}

    public static void main(String[] args) {
        // [ja] 1. パターンの中で使わない要素
        // [zh] 1. 模式中不使用的部分
        // [en] 1. Unused parts of a pattern
        Object obj = new Line(new Point(1, 2), new Point(3, 4));
        if (obj instanceof Line(Point(int x, _), _)) {
            System.out.println("x of the start point = " + x);
        }

        // [ja] 2. 使わないループ変数
        // [zh] 2. 不使用的循环变量
        // [en] 2. An unused loop variable
        int count = 0;
        for (var _ : List.of("a", "b", "c")) {
            count++;
        }
        System.out.println("number of elements = " + count);

        // [ja] 3. 使わない例外変数
        // [zh] 3. 不使用的异常变量
        // [en] 3. An unused exception variable
        try {
            Integer.parseInt("abc");
        } catch (NumberFormatException _) {
            System.out.println("not a number");
        }

        // [ja] 4. 使わないラムダ引数
        // [zh] 4. 不使用的 lambda 参数
        // [en] 4. An unused lambda parameter
        List.of(1, 2, 3).forEach(_ -> System.out.print("*"));
        System.out.println();
    }
}
