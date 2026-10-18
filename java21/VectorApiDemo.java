// JEP 448: Vector API (sixth incubator in Java 21)
// Run: java --add-modules jdk.incubator.vector VectorApiDemo.java

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;

public class VectorApiDemo {

    static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    // [ja] c[i] = a[i] * a[i] + b[i] を SIMD でまとめて計算する
    // [zh] 用 SIMD 批量计算 c[i] = a[i] * a[i] + b[i]
    // [en] Compute c[i] = a[i] * a[i] + b[i] in bulk with SIMD
    static void vectorized(float[] a, float[] b, float[] c) {
        int i = 0;
        int upper = SPECIES.loopBound(a.length);
        for (; i < upper; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, a, i);
            var vb = FloatVector.fromArray(SPECIES, b, i);
            va.mul(va).add(vb).intoArray(c, i);
        }
        // [ja] 端数はスカラーで処理する
        // [zh] 剩余的元素用普通（标量）方式处理
        // [en] Handle the remaining elements one by one (scalar)
        for (; i < a.length; i++) {
            c[i] = a[i] * a[i] + b[i];
        }
    }

    public static void main(String[] args) {
        float[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        float[] b = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
        float[] c = new float[a.length];
        vectorized(a, b, c);
        System.out.println("lanes per operation = " + SPECIES.length());
        System.out.println(java.util.Arrays.toString(c));
    }
}
