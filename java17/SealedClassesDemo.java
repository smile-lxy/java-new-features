// JEP 409: Sealed Classes
// Run: java SealedClassesDemo.java

import java.util.Arrays;
import java.util.List;

public class SealedClassesDemo {

    // [ja] Shape を継承できるのは permits に書いた 3 つだけ
    // [zh] 只有 permits 里列出的 3 个类型可以继承 Shape
    // [en] Only the three types listed in permits may extend Shape
    sealed interface Shape permits Circle, Square, Polygon {}

    // [ja] record は暗黙的に final
    // [zh] record 隐式为 final
    // [en] A record is implicitly final
    record Circle(double radius) implements Shape {}

    // [ja] final：これ以上継承させない
    // [zh] final：不再允许继承
    // [en] final: no further subclasses
    static final class Square implements Shape {
        final double side;
        Square(double side) { this.side = side; }
    }

    // [ja] non-sealed：ここから先は誰でも継承できる
    // [zh] non-sealed：从这里开始，任何类都可以继承
    // [en] non-sealed: anyone may extend it from here on
    static non-sealed class Polygon implements Shape {
        final int sides;
        Polygon(int sides) { this.sides = sides; }
    }

    static class Triangle extends Polygon {
        Triangle() { super(3); }
    }

    // [ja] 次の行はコンパイルエラーになる（Shape は permits に含まれない型を拒否する）
    // [zh] 下面这一行会编译失败（Shape 拒绝 permits 以外的类型）
    // [en] The next line does not compile (Shape rejects types not listed in permits)
    // record Hexagon() implements Shape {}

    // [ja] Java 17 の switch はまだパターンを書けない（プレビュー）ので、instanceof で分岐する
    // [zh] Java 17 的 switch 还不能写模式（仍是预览），所以用 instanceof 分支
    // [en] switch cannot take patterns yet in Java 17 (still a preview), so branch with instanceof
    static double area(Shape shape) {
        if (shape instanceof Circle c) {
            return Math.PI * c.radius() * c.radius();
        } else if (shape instanceof Square s) {
            return s.side * s.side;
        } else if (shape instanceof Polygon p) {
            // [ja] 辺の数だけでは面積は決まらない
            // [zh] 只凭边数算不出面积
            // [en] The number of sides alone does not give the area
            return Double.NaN;
        }
        throw new AssertionError("unreachable: Shape is sealed");
    }

    public static void main(String[] args) {
        // [ja] リフレクションで「封印」の情報を読める（JDK 17 で追加された API）
        // [zh] 可以用反射读取“密封”信息（JDK 17 新增的 API）
        // [en] Reflection can read the sealing information (APIs added in JDK 17)
        System.out.println("Shape.isSealed()          = " + Shape.class.isSealed());
        System.out.println("permitted subclasses      = " + Arrays.stream(Shape.class.getPermittedSubclasses())
                .map(Class::getSimpleName).toList());
        System.out.println("Polygon.isSealed()        = " + Polygon.class.isSealed());
        System.out.println("Circle is a record        = " + Circle.class.isRecord());
        System.out.println();

        for (Shape s : List.of(new Circle(1), new Square(2), new Triangle())) {
            System.out.printf("%-9s area = %.2f%n", s.getClass().getSimpleName(), area(s));
        }
    }
}
