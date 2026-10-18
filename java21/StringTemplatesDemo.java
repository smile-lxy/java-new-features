// JEP 430: String Templates (preview in Java 21, withdrawn in JDK 23)
// Run: java --enable-preview --source 21 StringTemplatesDemo.java

import java.text.MessageFormat;

public class StringTemplatesDemo {

    public static void main(String[] args) {
        String name = "xyz";
        int age = 18;

        String a = "Hello, " + name + "! Next year you'll be " + (age + 1) + ".";
        String b = new StringBuilder().append("Hello, ").append(name)
                .append("! Next year you'll be ").append(age + 1).append(".").toString();
        String c = String.format("Hello, %s! Next year you'll be %d.", name, age + 1);
        String d = MessageFormat.format("Hello, {0}! Next year you''ll be {1}.", name, age + 1);
        String e = STR."Hello, \{name}! Next year you'll be \{age + 1}.";

        System.out.println("+ operator    : " + a);
        System.out.println("StringBuilder : " + b);
        System.out.println("String.format : " + c);
        System.out.println("MessageFormat : " + d);
        System.out.println("STR           : " + e);

        // [ja] テキストブロックとも組み合わせられる（式は任意に書ける）
        // [zh] 也可以和文本块一起使用（可以嵌入任意表达式）
        // [en] Works with text blocks too (any expression can be embedded)
        int x = 3, y = 4;
        String report = STR."""
                x = \{x}
                y = \{y}
                x * y = \{x * y}
                """;
        System.out.print(report);

        // [ja] 注意：STR は値をそのまま連結するだけ（エスケープしない）
        // [zh] 注意：STR 只是原样拼接，不做任何转义
        // [en] Note: STR just concatenates the values (no escaping)
        String user = "' OR '1'='1";
        System.out.println(STR."SELECT * FROM users WHERE name = '\{user}'");
    }
}
