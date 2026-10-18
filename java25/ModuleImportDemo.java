// JEP 511: Module Import Declarations
// Run: java ModuleImportDemo.java

// [ja] モジュールが公開しているパッケージを、まとめて import する
// [zh] 一次性 import 某个模块导出的所有包
// [en] Import every package that a module exports, in one line
import module java.base;      // java.util, java.util.stream, java.time, java.nio.file ...
import module java.net.http;  // java.net.http

// [ja] java.desktop にも java.awt.List があるので、List が曖昧になる。単一型 import が優先されるので、これで解決する
// [zh] java.desktop 里也有 java.awt.List，List 会产生歧义。单类型 import 优先，用它来解决
// [en] java.desktop also has java.awt.List, which makes List ambiguous. A single-type import wins, so this resolves it
import module java.desktop;
import java.util.List;

public class ModuleImportDemo {

    public static void main(String[] args) {
        // [ja] java.util・java.time・java.net.http のクラスを、個別の import なしで使っている
        // [zh] java.util、java.time、java.net.http 的类都没有单独 import 就用上了
        // [en] Classes from java.util, java.time and java.net.http, with no single-type imports
        List<String> names = Stream.of("b", "a", "c").sorted().toList();
        LocalDate date = LocalDate.of(2025, 9, 16);
        HttpClient.Version version = HttpClient.Version.HTTP_2;
        System.out.println("names   = " + names);
        System.out.println("JDK 25  = " + date + " (" + date.getDayOfWeek() + ")");
        System.out.println("version = " + version);

        // [ja] java.desktop のクラスも使える（List 以外）
        // [zh] java.desktop 的类也可以用（List 除外）
        // [en] Classes from java.desktop work too (except List)
        Point p = new Point(3, 4);
        System.out.println("point   = " + p.x + "," + p.y + " (distance " + p.distance(0, 0) + ")");
        System.out.println("List is " + List.class.getName());
    }
}
