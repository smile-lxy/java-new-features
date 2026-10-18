// Java 17 APIs outside the JEPs: HexFormat and InstantSource
// Run: java HexFormatDemo.java

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.util.HexFormat;

public class HexFormatDemo {

    // [ja] InstantSource：「今の時刻」だけを提供するインターフェース（タイムゾーンを持たない Clock）
    // [zh] InstantSource：只提供“当前时刻”的接口（不带时区的 Clock）
    // [en] InstantSource: an interface that only provides "now" (a Clock without a time zone)
    record Token(String value, Instant expiresAt) {
        boolean isExpired(InstantSource time) {
            return time.instant().isAfter(expiresAt);
        }
    }

    public static void main(String[] args) {
        // [ja] HexFormat：バイト列と 16 進文字列の相互変換（これまでは自作するか外部ライブラリが必要だった）
        // [zh] HexFormat：字节数组和十六进制字符串的相互转换（以前需要自己写或依赖外部库）
        // [en] HexFormat: convert between bytes and hex strings (previously hand-written or from a library)
        byte[] bytes = "Java17".getBytes(StandardCharsets.UTF_8);
        HexFormat hex = HexFormat.of();
        String encoded = hex.formatHex(bytes);
        System.out.println("formatHex         = " + encoded);
        System.out.println("parseHex          = " + new String(hex.parseHex(encoded), StandardCharsets.UTF_8));
        System.out.println("MAC style         = " + HexFormat.ofDelimiter(":").withUpperCase().formatHex(bytes));
        System.out.println("toHexDigits(255)  = " + hex.toHexDigits((byte) 255));

        // [ja] テストでは時刻を固定した InstantSource を渡せる
        // [zh] 在测试中可以传入固定时刻的 InstantSource
        // [en] Tests can pass an InstantSource with a fixed time
        Instant t0 = Instant.parse("2026-08-01T00:00:00Z");
        Token token = new Token("abc", t0.plus(Duration.ofMinutes(10)));
        System.out.println();
        System.out.println("expired at +5 min  = " + token.isExpired(InstantSource.fixed(t0.plusSeconds(300))));
        System.out.println("expired at +15 min = " + token.isExpired(InstantSource.fixed(t0.plusSeconds(900))));
        System.out.println("Clock is an InstantSource = " + (Clock.systemUTC() instanceof InstantSource));
    }
}
