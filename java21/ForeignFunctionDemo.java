// JEP 442: Foreign Function & Memory API (third preview in Java 21)
// Run: java --enable-preview --source 21 --enable-native-access=ALL-UNNAMED ForeignFunctionDemo.java

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class ForeignFunctionDemo {

    public static void main(String[] args) throws Throwable {
        Linker linker = Linker.nativeLinker();

        // [ja] C 標準ライブラリの size_t strlen(const char *s) を探して、Java のメソッドハンドルにする
        // [zh] 找到 C 标准库的 size_t strlen(const char *s)，把它变成 Java 的方法句柄
        // [en] Look up size_t strlen(const char *s) in the C library and turn it into a Java method handle
        MethodHandle strlen = linker.downcallHandle(
                linker.defaultLookup().find("strlen").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));

        // [ja] スコープを抜けるとメモリを解放する
        // [zh] 离开作用域时释放内存
        // [en] The memory is freed when the scope ends
        try (Arena arena = Arena.ofConfined()) {
            // [ja] ヒープ外に C 文字列を確保する
            // [zh] 在堆外分配一个 C 字符串
            // [en] Allocate a C string off-heap
            MemorySegment str = arena.allocateUtf8String("Hello, FFM!");
            long len = (long) strlen.invokeExact(str);
            System.out.println("strlen(\"Hello, FFM!\") = " + len);

            // [ja] ヒープ外メモリに int 配列を書いて読む
            // [zh] 在堆外内存中写入并读取一个 int 数组
            // [en] Write and read an int array in off-heap memory
            MemorySegment ints = arena.allocateArray(ValueLayout.JAVA_INT, 5);
            for (int i = 0; i < 5; i++) ints.setAtIndex(ValueLayout.JAVA_INT, i, i * i);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) sb.append(ints.getAtIndex(ValueLayout.JAVA_INT, i)).append(' ');
            System.out.println("off-heap int[] = " + sb.toString().trim());
        }
    }
}
