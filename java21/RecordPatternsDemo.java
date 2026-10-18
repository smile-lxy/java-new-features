// JEP 440: Record Patterns
// Run: java RecordPatternsDemo.java

public class RecordPatternsDemo {

    enum Color { RED, GREEN, BLUE }

    record Point(int x, int y) {}
    record ColoredPoint(Point p, Color c) {}
    record Rectangle(ColoredPoint upperLeft, ColoredPoint lowerRight) {}

    // [ja] 従来：型チェック → キャスト → アクセサ
    // [zh] 以前的写法：类型检查 → 强制转换 → 访问器
    // [en] Before: type check -> cast -> accessor
    static int sumOld(Object obj) {
        if (obj instanceof Point) {
            Point p = (Point) obj;
            return p.x() + p.y();
        }
        return 0;
    }

    // [ja] Java 21：型チェックと分解を同時に行う
    // [zh] Java 21：类型检查和解构一步完成
    // [en] Java 21: type check and deconstruction in one step
    static int sumNew(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            return x + y;
        }
        return 0;
    }

    // [ja] ネストしたレコードを一度に分解する
    // [zh] 一次性解构嵌套的 record
    // [en] Deconstruct nested records at once
    static Color upperLeftColor(Object obj) {
        if (obj instanceof Rectangle(ColoredPoint(Point p, Color c), ColoredPoint lr)) {
            return c;
        }
        throw new IllegalArgumentException("not a rectangle");
    }

    // [ja] switch + レコードパターン + when ガード
    // [zh] switch + record 模式 + when 守卫
    // [en] switch + record patterns + when guards
    static String describe(Object obj) {
        return switch (obj) {
            case Point(int x, int y) when x == y -> "point on the diagonal (" + x + ", " + y + ")";
            case Point(int x, int y) -> "point (" + x + ", " + y + ")";
            case Rectangle(ColoredPoint(Point p, Color c), ColoredPoint lr) when c == Color.BLUE ->
                    "rectangle with a blue upper-left corner (x=" + p.x() + ")";
            case Rectangle r -> "rectangle";
            default -> "unknown";
        };
    }

    public static void main(String[] args) {
        Point point = new Point(1, 2);
        Rectangle blue = new Rectangle(
                new ColoredPoint(new Point(0, 0), Color.BLUE),
                new ColoredPoint(new Point(10, 10), Color.RED));
        Rectangle green = new Rectangle(
                new ColoredPoint(new Point(5, 5), Color.GREEN),
                new ColoredPoint(new Point(10, 10), Color.RED));

        System.out.println("sumOld(point)        = " + sumOld(point));
        System.out.println("sumNew(point)        = " + sumNew(point));
        System.out.println("upperLeftColor(blue) = " + upperLeftColor(blue));
        System.out.println("describe(Point(3,3)) = " + describe(new Point(3, 3)));
        System.out.println("describe(point)      = " + describe(point));
        System.out.println("describe(blue)       = " + describe(blue));
        System.out.println("describe(green)      = " + describe(green));
        System.out.println("describe(\"text\")     = " + describe("text"));
    }
}
