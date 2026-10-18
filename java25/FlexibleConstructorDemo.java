// JEP 513: Flexible Constructor Bodies
// Run: java FlexibleConstructorDemo.java

public class FlexibleConstructorDemo {

    static class Shape {
        Shape() {
            // [ja] 親のコンストラクタからオーバーライドされたメソッドを呼ぶ（よくある落とし穴）
            // [zh] 在父类构造器里调用被重写的方法（常见的陷阱）
            // [en] Call an overridden method from the parent constructor (a classic pitfall)
            System.out.println("  Shape(): " + describe());
        }
        String describe() { return "shape"; }
    }

    // [ja] 従来：super() は先頭に書く必要があり、フィールドは super() の後でしか代入できなかった
    // [zh] 以前：super() 必须写在第一行，字段只能在 super() 之后赋值
    // [en] Before: super() had to come first, and fields could only be assigned after it
    static class OldCircle extends Shape {
        private final double radius;
        OldCircle(double radius) {
            super();
            this.radius = radius;
        }
        @Override String describe() { return "circle r=" + radius; }
    }

    // [ja] JDK 25：super() の前に引数を検証し、フィールドを初期化できる
    // [zh] JDK 25：可以在 super() 之前校验参数、初始化字段
    // [en] JDK 25: validate arguments and initialize fields before super()
    static class NewCircle extends Shape {
        private final double radius;
        NewCircle(double radius) {
            if (radius <= 0) {
                throw new IllegalArgumentException("radius must be positive: " + radius);
            }
            this.radius = radius;
            super();
        }
        @Override String describe() { return "circle r=" + radius; }
    }

    // [ja] 親に渡す値を super() の前で計算する（従来は static メソッドに逃がす必要があった）
    // [zh] 在 super() 之前计算要传给父类的值（以前只能放到 static 方法里）
    // [en] Compute the value for the parent before super() (previously this needed a static helper)
    static class Base {
        final String id;
        Base(String id) { this.id = id; }
    }
    static class User extends Base {
        User(String email) {
            String normalized = email.trim().toLowerCase();
            if (!normalized.contains("@")) {
                throw new IllegalArgumentException("not an email: " + email);
            }
            super(normalized);
        }
    }

    public static void main(String[] args) {
        System.out.println("new OldCircle(2):");
        new OldCircle(2);
        System.out.println("new NewCircle(2):");
        new NewCircle(2);

        try {
            new NewCircle(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("new NewCircle(-1): " + e.getMessage());
        }
        System.out.println("new User(\"  Alice@Example.COM \").id = " + new User("  Alice@Example.COM ").id);
    }
}
