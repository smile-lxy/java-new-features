// JEP 520: JFR Method Timing & Tracing
// Run: java '-XX:StartFlightRecording:method-timing=MethodTimingDemo::parse;java.lang.Integer::parseInt,filename=timing.jfr' MethodTimingDemo.java
// Run: jfr view method-timing timing.jfr

import java.util.ArrayList;
import java.util.List;

public class MethodTimingDemo {

    // [ja] 計測したいメソッド。コードには計測用の処理を一切書いていない
    // [zh] 想要测量的方法。代码里没有写任何测量用的处理
    // [en] The method we want to time; the code itself contains no timing logic at all
    static List<Integer> parse(String csv) {
        List<Integer> values = new ArrayList<>();
        for (String s : csv.split(",")) {
            values.add(Integer.parseInt(s.trim()));
        }
        return values;
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1_000; i++) {
            sb.append(i).append(", ");
        }
        sb.append(1_000);
        String csv = sb.toString();

        long sum = 0;
        for (int i = 0; i < 2_000; i++) {
            sum += parse(csv).size();
        }
        System.out.println("parsed " + sum + " values");
    }
}
