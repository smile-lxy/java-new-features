// JEP 444: Virtual Threads - what happens if you keep creating them without a limit
// Run: java -Xmx128m VirtualThreadOomDemo.java

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class VirtualThreadOomDemo {

    static volatile boolean outOfMemory = false;
    // [ja] 結果を出力するための予備のメモリ
    // [zh] 预留一块内存，用于最后输出结果
    // [en] Spare memory so that the result can still be printed
    static byte[] reserve = new byte[8 * 1024 * 1024];

    public static void main(String[] args) {
        // [ja] OOM は仮想スレッドの中で起きることもあるので、ハンドラで検知する
        // [zh] OOM 也可能发生在虚拟线程内部，所以用异常处理器来捕获
        // [en] The OOM may happen inside a virtual thread, so detect it with a handler
        Thread.Builder builder = Thread.ofVirtual().uncaughtExceptionHandler((t, e) -> {
            if (e instanceof OutOfMemoryError) outOfMemory = true;
        });
        Runnable sleep10s = () -> {
            try {
                Thread.sleep(Duration.ofSeconds(10));
            } catch (InterruptedException ignored) {
            }
        };

        List<Thread> hold = new ArrayList<>();
        long start = System.nanoTime();
        try {
            while (!outOfMemory) {
                hold.add(builder.start(sleep10s));
            }
        } catch (OutOfMemoryError e) {
            outOfMemory = true;
        }
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        // [ja] 予備のメモリを手放してから出力する
        // [zh] 先释放预留的内存，再输出
        // [en] Release the spare memory before printing
        reserve = null;
        System.out.printf("OutOfMemoryError after creating %,d threads (%,d ms)%n", hold.size(), elapsed);
    }
}
