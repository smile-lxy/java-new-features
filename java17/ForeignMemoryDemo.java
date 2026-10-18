// JEP 412: Foreign Function & Memory API (incubator in Java 17)
// Run: java --add-modules jdk.incubator.foreign --enable-native-access=ALL-UNNAMED ForeignMemoryDemo.java

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public class ForeignMemoryDemo {

    public static void main(String[] args) throws Throwable {
        // [ja] ① C 標準ライブラリの strlen を JNI なしで呼ぶ
        // [zh] ① 不用 JNI，直接调用 C 标准库的 strlen
        // [en] (1) Call strlen from the C standard library without JNI
        MethodHandle strlen = CLinker.getInstance().downcallHandle(
                CLinker.systemLookup().lookup("strlen").orElseThrow(),
                MethodType.methodType(long.class, MemoryAddress.class),
                FunctionDescriptor.of(CLinker.C_LONG, CLinker.C_POINTER));

        // [ja] ResourceScope を閉じると、その中で確保したネイティブメモリがまとめて解放される
        // [zh] 关闭 ResourceScope 时，在其中分配的本地内存会被一起释放
        // [en] Closing the ResourceScope frees all the native memory allocated in it
        try (ResourceScope scope = ResourceScope.newConfinedScope()) {
            MemorySegment cString = CLinker.toCString("Hello, Java 17!", scope);
            long length = (long) strlen.invokeExact(cString.address());
            System.out.println("strlen(\"Hello, Java 17!\") = " + length);

            // [ja] ② ヒープ外（オフヒープ）に int を 5 個分確保して読み書きする
            // [zh] ② 在堆外分配 5 个 int 的空间并读写
            // [en] (2) Allocate room for five ints off-heap, then write and read them
            MemorySegment ints = MemorySegment.allocateNative(5 * 4, scope);
            for (int i = 0; i < 5; i++) {
                MemoryAccess.setIntAtIndex(ints, i, i * i);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                sb.append(MemoryAccess.getIntAtIndex(ints, i)).append(' ');
            }
            System.out.println("off-heap ints = " + sb.toString().trim() + " (" + ints.byteSize() + " bytes)");

            // [ja] ③ 範囲外アクセスは JVM のクラッシュではなく例外になる
            // [zh] ③ 越界访问会抛出异常，而不是让 JVM 崩溃
            // [en] (3) An out-of-bounds access throws an exception instead of crashing the JVM
            try {
                MemoryAccess.setIntAtIndex(ints, 5, 25);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("index 5 -> " + e.getClass().getSimpleName());
            }
        }
        System.out.println("scope closed: native memory freed");
    }
}
