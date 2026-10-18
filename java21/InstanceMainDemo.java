// JEP 445: Unnamed Classes and Instance Main Methods (preview in Java 21)
// Run: java --enable-preview --source 21 InstanceMainDemo.java

// [ja] class 宣言なしで、フィールドとメソッドを直接書ける
// [zh] 不用写 class 声明，直接写字段和方法
// [en] Fields and methods without a class declaration
String greeting = "Hello";

String greet(String name) {
    return greeting + ", " + name + "!";
}

void main() {
    System.out.println(greet("World"));
}
