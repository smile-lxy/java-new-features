// JEP 519: Compact Object Headers
// Run: java -Xmx2g CompactHeadersDemo.java
// Run: java -Xmx2g -XX:+UseCompactObjectHeaders CompactHeadersDemo.java

import java.lang.management.ManagementFactory;

public class CompactHeadersDemo {

    // [ja] int を 2 つ持つだけの小さなオブジェクト
    // [zh] 只有 2 个 int 的小对象
    // [en] A small object with just two ints
    record Point(int x, int y) {}

    static final int COUNT = 10_000_000;

    static long usedHeap() {
        for (int i = 0; i < 3; i++) System.gc();
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public static void main(String[] args) {
        boolean compact = ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-XX:+UseCompactObjectHeaders");
        System.out.println("UseCompactObjectHeaders = " + compact);

        long before = usedHeap();
        Point[] points = new Point[COUNT];
        for (int i = 0; i < COUNT; i++) {
            points[i] = new Point(i, -i);
        }
        long after = usedHeap();

        // [ja] 配列自体（参照 1 個 4 バイト × 1,000 万）を引いて、Point 1 個あたりのサイズを求める
        // [zh] 减去数组本身（每个引用 4 字节 × 1000 万），求出每个 Point 的大小
        // [en] Subtract the array itself (4 bytes per reference x 10 million) to get the size per Point
        long arrayBytes = 4L * COUNT;
        double perObject = (double) (after - before - arrayBytes) / COUNT;
        System.out.printf("%,d Points: %,d MB on the heap, %.1f bytes per Point%n",
                COUNT, (after - before) / 1024 / 1024, perObject);
        System.out.println("check: " + points[COUNT - 1].y());
    }
}
