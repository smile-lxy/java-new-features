// JEP 521: Generational Shenandoah
// Run: java -XX:+UseShenandoahGC -XX:ShenandoahGCMode=generational -Xmx2g GenerationalShenandoahDemo.java
// Run: java -XX:+UseShenandoahGC -Xmx2g GenerationalShenandoahDemo.java

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class GenerationalShenandoahDemo {

    public static void main(String[] args) {
        // [ja] 直近の 5 万個だけを保持する（すぐゴミになる短命なオブジェクト）
        // [zh] 只保留最近的 5 万个对象（很快就会变成垃圾的短命对象）
        // [en] Keep only the latest 50,000 objects (short-lived ones that soon become garbage)
        byte[][] recent = new byte[50_000][];
        // [ja] 最後まで生き残るデータを先に約 600 MB 作っておく（キャッシュなどのつもり）
        // [zh] 先创建约 600 MB 一直存活到最后的数据（假装是缓存之类）
        // [en] First create about 600 MB of data that lives until the end (think of a cache)
        List<byte[]> longLived = new ArrayList<>();
        for (int i = 0; i < 600_000; i++) {
            longLived.add(new byte[1024]);
        }
        long start = System.nanoTime();

        for (int i = 0; i < 30_000_000; i++) {
            recent[i % recent.length] = new byte[1024];
        }

        System.out.printf("allocated ~%d GB, long-lived %,d, elapsed %,d ms%n",
                30_000_000L * 1024 / 1024 / 1024 / 1024, longLived.size(), (System.nanoTime() - start) / 1_000_000);
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.printf("  %-20s count %4d, total %,6d ms%n", gc.getName(), gc.getCollectionCount(), gc.getCollectionTime());
        }
    }
}
