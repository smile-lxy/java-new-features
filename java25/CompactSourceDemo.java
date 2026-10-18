// JEP 512: Compact Source Files and Instance Main Methods
// Run: java CompactSourceDemo.java

// [ja] class 宣言も public static も String[] args も要らない。java.base のクラスは import なしで使える
// [zh] 不需要 class 声明，也不需要 public static 和 String[] args。java.base 的类不用 import 就能用
// [en] No class declaration, no public static, no String[] args. Classes from java.base need no import

final List<String> fruits = List.of("apple", "banana", "cherry");

String shout(String s) {
    return s.toUpperCase() + "!";
}

void main() {
    // [ja] IO.println は System.out.println の簡易版（java.lang.IO、JDK 25 で追加）
    // [zh] IO.println 是 System.out.println 的简化版（java.lang.IO，JDK 25 新增）
    // [en] IO.println is a short form of System.out.println (java.lang.IO, added in JDK 25)
    IO.println("Hello, Java 25!");
    for (String f : fruits) {
        IO.println(shout(f));
    }

    // [ja] List、Map、Collectors などは java.base モジュールの自動 import で使える
    // [zh] List、Map、Collectors 等都通过自动 import java.base 模块而可用
    // [en] List, Map, Collectors and so on come from the automatic import of the java.base module
    Map<Integer, List<String>> byLength = fruits.stream().collect(Collectors.groupingBy(String::length));
    IO.println("by length: " + byLength);

    // [ja] 実はコンパイラが暗黙のクラスを作っている
    // [zh] 其实编译器生成了一个隐式类
    // [en] Behind the scenes, the compiler creates an implicit class
    IO.println("class name: " + getClass().getName());
}
