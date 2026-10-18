// JEP 444: Virtual Threads - memory per platform thread vs per virtual thread
// Run: java MemoryCostDemo.java

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MemoryCostDemo {

    static final int PLATFORM_COUNT = 2_000;
    static final int VIRTUAL_COUNT = 200_000;

    public static void main(String[] args) throws Exception {
        System.out.println("Java " + System.getProperty("java.version")
                + ", " + Runtime.getRuntime().availableProcessors() + " CPU cores");
        measure("Platform", PLATFORM_COUNT, Thread.ofPlatform());
        measure("Virtual ", VIRTUAL_COUNT, Thread.ofVirtual());
    }

    static void measure(String label, int count, Thread.Builder builder) throws Exception {
        gc();
        long heapBefore = usedHeap();
        long osBefore = committedVirtualMemory();

        CountDownLatch ready = new CountDownLatch(count);
        CountDownLatch release = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            threads.add(builder.start(() -> {
                ready.countDown();
                try {
                    // [ja] 全スレッドを待機状態のまま生かしておく
                    // [zh] 让所有线程保持等待状态
                    // [en] Keep every thread alive and waiting
                    release.await();
                } catch (InterruptedException ignored) {
                }
            }));
        }
        ready.await();
        gc();

        long heapDelta = usedHeap() - heapBefore;
        long osDelta = committedVirtualMemory() - osBefore;
        System.out.printf("[%s] %,d threads: heap +%s (%s each), OS committed virtual memory +%s (%s each)%n",
                label, count, format(heapDelta), format(heapDelta / count),
                format(osDelta), format(osDelta / count));

        release.countDown();
        for (Thread t : threads) t.join();
    }

    static long usedHeap() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    }

    static long committedVirtualMemory() {
        var os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return os.getCommittedVirtualMemorySize();
    }

    static void gc() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            System.gc();
            Thread.sleep(300);
        }
    }

    static String format(long bytes) {
        if (Math.abs(bytes) < 1024) return bytes + " B";
        if (Math.abs(bytes) < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (Math.abs(bytes) < 1024L * 1024 * 1024) return String.format("%.2f MB", bytes / 1024.0 / 1024);
        return String.format("%.2f GB", bytes / 1024.0 / 1024 / 1024);
    }
}
