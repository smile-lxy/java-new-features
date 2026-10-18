// JEP 441: Pattern Matching for switch
// Run: java SwitchPatternDemo.java

public class SwitchPatternDemo {

    // [ja] 従来：instanceof の連鎖
    // [zh] 以前的写法：一连串的 instanceof
    // [en] Before: a chain of instanceof checks
    static String formatOld(Object o) {
        if (o instanceof Integer) {
            Integer i = (Integer) o;
            return "int " + i;
        } else if (o instanceof Long) {
            Long l = (Long) o;
            return "long " + l;
        } else if (o instanceof String) {
            String s = (String) o;
            return "String " + s;
        }
        return "unknown";
    }

    // [ja] Java 21：型パターンを case に書く
    // [zh] Java 21：在 case 中直接写类型模式
    // [en] Java 21: type patterns in case labels
    static String formatNew(Object o) {
        return switch (o) {
            case Integer i -> "int " + i;
            case Long l -> "long " + l;
            case String s -> "String " + s;
            default -> "unknown";
        };
    }

    // [ja] 従来：null を事前にチェックしないと NullPointerException
    // [zh] 以前的写法：不先检查 null 就会抛出 NullPointerException
    // [en] Before: without a null check first, switch throws NullPointerException
    static String answerOld(String s) {
        if (s == null) {
            return "Oops";
        }
        return switch (s) {
            case "YES", "NO" -> "Known";
            default -> "Other";
        };
    }

    // [ja] Java 21：case null で受けられる
    // [zh] Java 21：可以用 case null 处理
    // [en] Java 21: handle it with case null
    static String answerNew(String s) {
        return switch (s) {
            case null -> "Oops";
            case "YES", "NO" -> "Known";
            default -> "Other";
        };
    }

    // [ja] when ガード
    // [zh] when 守卫
    // [en] when guards
    static String classify(Object o) {
        return switch (o) {
            case Integer i when i < 0 -> "negative integer";
            case Integer i when i == 0 -> "zero";
            case Integer i -> "positive integer";
            case String s when s.isBlank() -> "blank string";
            case String s -> "string (" + s.length() + " chars)";
            default -> "other";
        };
    }

    // [ja] sealed と組み合わせると default が不要（網羅性をコンパイラが保証）
    // [zh] 与 sealed 配合时不需要 default（由编译器保证穷尽）
    // [en] With sealed types no default is needed (the compiler checks exhaustiveness)
    sealed interface Shape permits Circle, Square {}
    record Circle(double r) implements Shape {}
    record Square(double side) implements Shape {}

    static double area(Shape s) {
        return switch (s) {
            case Circle c -> Math.PI * c.r() * c.r();
            case Square q -> q.side() * q.side();
        };
    }

    public static void main(String[] args) {
        for (Object o : new Object[] {1, 2L, "abc", 3.14}) {
            System.out.printf("%-6s old=%-12s new=%s%n", o, formatOld(o), formatNew(o));
        }
        System.out.println("---");
        for (String s : new String[] {"YES", "yes", null}) {
            System.out.printf("%-5s old=%-6s new=%s%n", s, answerOld(s), answerNew(s));
        }
        System.out.println("---");
        for (Object o : new Object[] {-5, 0, 42, "  ", "Java", 1.0}) {
            System.out.printf("%-6s -> %s%n", "\"" + o + "\"", classify(o));
        }
        System.out.println("---");
        System.out.printf("area(Circle(1)) = %.4f%n", area(new Circle(1)));
        System.out.printf("area(Square(2)) = %.4f%n", area(new Square(2)));
    }
}
