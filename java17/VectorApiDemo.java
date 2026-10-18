// JEP 414: Vector API (second incubator in Java 17)
// Run: java --add-modules jdk.incubator.vector VectorApiDemo.java

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class VectorApiDemo {

    static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    // [ja] 2 つの配列の内積（a[0]*b[0] + a[1]*b[1] + ...）を普通のループで計算する
    // [zh] 用普通循环计算两个数组的点积（a[0]*b[0] + a[1]*b[1] + ...）
    // [en] Compute the dot product of two arrays (a[0]*b[0] + a[1]*b[1] + ...) with a plain loop
    static float scalarDot(float[] a, float[] b) {
        float sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    // [ja] 同じ計算を Vector API で、SPECIES.length() 個ずつまとめて行う
    // [zh] 用 Vector API 做同样的计算，每次处理 SPECIES.length() 个元素
    // [en] The same computation with the Vector API, SPECIES.length() elements at a time
    static float vectorDot(float[] a, float[] b) {
        var acc = FloatVector.zero(SPECIES);
        int i = 0;
        int upper = SPECIES.loopBound(a.length);
        for (; i < upper; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, a, i);
            var vb = FloatVector.fromArray(SPECIES, b, i);
            // [ja] acc += va * vb（積和演算）
            // [zh] acc += va * vb（乘加运算）
            // [en] acc += va * vb (fused multiply-add)
            acc = va.fma(vb, acc);
        }
        float sum = acc.reduceLanes(VectorOperators.ADD);
        // [ja] 端数はスカラーで処理する
        // [zh] 剩余的元素用普通（标量）方式处理
        // [en] Handle the remaining elements one by one (scalar)
        for (; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("species = " + SPECIES + ", lanes per operation = " + SPECIES.length());

        float[] small = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println("dot(small, small): scalar = " + scalarDot(small, small) + ", vector = " + vectorDot(small, small));

        // [ja] 100 万要素の内積を 500 回計算して時間を比べる
        // [zh] 对 100 万个元素的点积计算 500 次并比较耗时
        // [en] Time 500 dot products of 1,000,000 elements
        float check = 0;
        float[] a = new float[1_000_000];
        float[] b = new float[1_000_000];
        for (int i = 0; i < a.length; i++) {
            a[i] = (i % 100) / 100f;
            b[i] = (i % 7) / 7f;
        }
        // [ja] 計測の前に、両方のメソッドを JIT コンパイルさせておく（ウォームアップ）
        // [zh] 计时前先让两个方法都被 JIT 编译（预热）
        // [en] Let the JIT compile both methods before timing (warm-up)
        float[] tiny = new float[1024];
        for (int r = 0; r < 20_000; r++) {
            check += scalarDot(tiny, tiny) + vectorDot(tiny, tiny);
        }
        for (int round = 1; round <= 3; round++) {
            long t0 = System.nanoTime();
            for (int r = 0; r < 500; r++) check += scalarDot(a, b);
            long t1 = System.nanoTime();
            for (int r = 0; r < 500; r++) check += vectorDot(a, b);
            long t2 = System.nanoTime();
            System.out.printf("round %d: scalar %4d ms | vector %4d ms%n", round, (t1 - t0) / 1_000_000, (t2 - t1) / 1_000_000);
        }
        // [ja] 結果を使って、計算が最適化で消されないようにする
        // [zh] 使用计算结果，防止计算被优化掉
        // [en] Use the result so the computation is not optimized away
        if (check == 42) System.out.println();
    }
}
