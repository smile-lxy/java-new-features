// JEP 514: Ahead-of-Time Command-Line Ergonomics / JEP 515: Ahead-of-Time Method Profiling
// Run: javac -d out AotCacheDemo.java && jar --create --file app.jar -C out .
// Run: java -cp app.jar AotCacheDemo
// Run: java -XX:AOTCacheOutput=app.aot -cp app.jar AotCacheDemo
// Run: java -XX:AOTCache=app.aot -cp app.jar AotCacheDemo

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AotCacheDemo {

    record Order(String id, LocalDate date, String category, int amount) {}

    static final String CSV = """
            A-1,2025-09-16,book,1200
            A-2,2025-09-16,food,800
            A-3,2025-09-17,book,2400
            A-4,2025-09-18,toy,1500
            """;

    public static void main(String[] args) throws Exception {
        // [ja] 小さな「アプリ」：CSV の解析、集計、日付の整形、HTTP リクエストの組み立て、仮想スレッド
        // [zh] 一个小“应用”：解析 CSV、汇总、格式化日期、构造 HTTP 请求、虚拟线程
        // [en] A tiny "app": parse CSV, aggregate, format dates, build an HTTP request, use virtual threads
        Pattern comma = Pattern.compile(",");
        List<Order> orders = CSV.lines()
                .map(comma::split)
                .map(f -> new Order(f[0], LocalDate.parse(f[1]), f[2], Integer.parseInt(f[3])))
                .toList();
        Map<String, Integer> byCategory = orders.stream()
                .collect(Collectors.groupingBy(Order::category, java.util.TreeMap::new, Collectors.summingInt(Order::amount)));

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://example.com/report"))
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(byCategory.toString()))
                .build();
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

        int total;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            total = executor.submit(() -> orders.stream().mapToInt(Order::amount).sum()).get();
        }

        String first = orders.getFirst().date().format(DateTimeFormatter.ofPattern("yyyy/MM/dd (EEE)", Locale.US));
        System.out.println(byCategory + " total=" + total + " first=" + first + " " + request.method()
                + " " + client.version());

        // [ja] JVM プロセスの起動からここまでの時間
        // [zh] 从 JVM 进程启动到这里所用的时间
        // [en] Time from the start of the JVM process to this point
        Instant start = ProcessHandle.current().info().startInstant().orElseThrow();
        System.out.println("finished " + Duration.between(start, Instant.now()).toMillis() + " ms after the JVM started");
    }
}
