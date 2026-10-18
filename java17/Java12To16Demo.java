// Java 12-16: features that became standard between Java 11 and Java 17
// Run: java Java12To16Demo.java

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Java12To16Demo {

    // [ja] record（JEP 395、Java 16）：コンストラクタ・アクセサ・equals・hashCode・toString を自動生成
    // [zh] record（JEP 395，Java 16）：自动生成构造器、访问器、equals、hashCode、toString
    // [en] Records (JEP 395, Java 16): constructor, accessors, equals, hashCode and toString are generated
    record Order(String id, int amount) {
        // [ja] コンパクトコンストラクタで値を検証する
        // [zh] 用紧凑构造器校验参数
        // [en] Validate the values in a compact constructor
        Order {
            if (amount < 0) throw new IllegalArgumentException("amount < 0");
        }
    }

    record Customer(String name, Address address) {}
    record Address(String city) {}

    public static void main(String[] args) {
        // [ja] テキストブロック（JEP 378、Java 15）
        // [zh] 文本块（JEP 378，Java 15）
        // [en] Text blocks (JEP 378, Java 15)
        String json = """
                {
                  "id": "A-1",
                  "amount": 300
                }
                """;
        System.out.print("--- text block ---\n" + json);

        // [ja] switch 式（JEP 361、Java 14）：値を返し、break も不要
        // [zh] switch 表达式（JEP 361，Java 14）：可以返回值，不需要 break
        // [en] Switch expressions (JEP 361, Java 14): they return a value and need no break
        for (int day : new int[] {1, 6, 8}) {
            String type = switch (day) {
                case 1, 2, 3, 4, 5 -> "weekday";
                case 6, 7 -> "weekend";
                default -> {
                    String message = "invalid day " + day;
                    yield message;
                }
            };
            System.out.println("day " + day + " -> " + type);
        }

        // [ja] instanceof のパターンマッチング（JEP 394、Java 16）：キャストが不要
        // [zh] instanceof 模式匹配（JEP 394，Java 16）：不需要强制转换
        // [en] Pattern matching for instanceof (JEP 394, Java 16): no cast needed
        Object o = new Order("A-1", 300);
        if (o instanceof Order order && order.amount() > 100) {
            System.out.println("large order: " + order);
        }
        System.out.println("equals by value: " + o.equals(new Order("A-1", 300)));

        // [ja] Stream.toList()（Java 16）と Collectors.teeing（Java 12）
        // [zh] Stream.toList()（Java 16）和 Collectors.teeing（Java 12）
        // [en] Stream.toList() (Java 16) and Collectors.teeing (Java 12)
        List<Integer> numbers = Stream.of(3, 1, 4, 1, 5, 9).toList();
        double average = numbers.stream().collect(Collectors.teeing(
                Collectors.summingInt(i -> i), Collectors.counting(), (sum, n) -> (double) sum / n));
        System.out.println("numbers = " + numbers + ", average = " + average);

        // [ja] わかりやすい NullPointerException（JEP 358、Java 14 で追加、Java 15 からデフォルトで有効）
        // [zh] 更易懂的 NullPointerException（JEP 358，Java 14 加入，Java 15 起默认启用）
        // [en] Helpful NullPointerExceptions (JEP 358, added in Java 14, on by default since Java 15)
        Customer customer = new Customer("Alice", null);
        try {
            System.out.println(customer.address().city());
        } catch (NullPointerException e) {
            System.out.println("NPE: " + e.getMessage());
        }
    }
}
