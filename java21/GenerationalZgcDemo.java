// JEP 439: Generational ZGC
// Run (generational)    : java -XX:+UseZGC -XX:+ZGenerational -Xmx1g GenerationalZgcDemo.java
// Run (non-generational): java -XX:+UseZGC -Xmx1g GenerationalZgcDemo.java

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class GenerationalZgcDemo {

    public static void main(String[] args) {
        // [ja] 直近 5 万個だけ保持する（すぐ不要になる短命オブジェクト）
        // [zh] 只保留最近的 5 万个对象（很快就不再需要的短命对象）
        // [en] Keep only the latest 50,000 objects (short-lived ones that soon become garbage)
        byte[][] recent = new byte[50_000][];
        // [ja] ごく一部は最後まで残る
        // [zh] 极少数对象会一直存活到最后
        // [en] A small fraction lives until the end
        List<byte[]> longLived = new ArrayList<>();
        long start = System.nanoTime();

        for (int i = 0; i < 30_000_000; i++) {
            recent[i % recent.length] = new byte[1024];
            if (i % 500 == 0) longLived.add(new byte[1024]);
        }

        System.out.printf("allocated ~%d GB, long-lived %,d, elapsed %,d ms%n",
                30_000_000L * 1024 / 1024 / 1024 / 1024, longLived.size(), (System.nanoTime() - start) / 1_000_000);
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.printf("  %-20s count %4d, total %,6d ms%n", gc.getName(), gc.getCollectionCount(), gc.getCollectionTime());
        }
    }
}
