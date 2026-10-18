// JEP 356: Enhanced Pseudo-Random Number Generators
// Run: java RandomGeneratorDemo.java

import java.util.Comparator;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.random.RandomGenerator.SplittableGenerator;
import java.util.random.RandomGenerator.JumpableGenerator;
import java.util.random.RandomGeneratorFactory;

public class RandomGeneratorDemo {

    public static void main(String[] args) {
        // [ja] 使えるアルゴリズムの一覧（グループ・状態ビット数・性質）
        // [zh] 可用算法一览（分组、状态位数、特性）
        // [en] The available algorithms (group, state bits, properties)
        System.out.println("--- algorithms ---");
        RandomGeneratorFactory.all()
                .sorted(Comparator.comparing(RandomGeneratorFactory<RandomGenerator>::group)
                        .thenComparing(RandomGeneratorFactory::name))
                .forEach(f -> System.out.printf("%-10s %-22s %9s  %s%n", f.group(), f.name(),
                        f.stateBits() == Integer.MAX_VALUE ? "-" : f.stateBits() + " bits",
                        f.isSplittable() ? "splittable" : f.isJumpable() ? "jumpable" : f.isStreamable() ? "streamable" : ""));

        // [ja] 既存のクラスも共通インターフェース RandomGenerator を実装している
        // [zh] 已有的类也实现了统一接口 RandomGenerator
        // [en] The existing classes now implement the common RandomGenerator interface
        System.out.println();
        System.out.println("--- one interface ---");
        // [ja] 1 つ目は従来の Random、2 つ目は名前で選んだもの、3 つ目は既定のアルゴリズム
        // [zh] 第 1 个是以前的 Random，第 2 个按名字选择，第 3 个是默认算法
        // [en] The first is the old Random, the second is picked by name, the third is the default algorithm
        RandomGenerator[] generators = {
                new Random(42),
                RandomGenerator.of("L64X128MixRandom"),
                RandomGenerator.getDefault(),
        };
        for (RandomGenerator g : generators) {
            System.out.printf("%-34s nextInt(100) = %d%n", g.getClass().getName(), g.nextInt(100));
        }

        // [ja] 同じシードなら同じ乱数列（再現性）
        // [zh] 种子相同，随机数序列就相同（可复现）
        // [en] The same seed gives the same sequence (reproducible)
        System.out.println();
        System.out.println("--- seeds ---");
        RandomGeneratorFactory<RandomGenerator> factory = RandomGeneratorFactory.of("Xoshiro256PlusPlus");
        System.out.println("seed 2021 run 1: " + factory.create(2021).ints(5, 0, 100).boxed().toList());
        System.out.println("seed 2021 run 2: " + factory.create(2021).ints(5, 0, 100).boxed().toList());

        // [ja] split()：統計的に独立した子ジェネレーターを作る（並列処理向き）
        // [zh] split()：生成统计上相互独立的子生成器（适合并行处理）
        // [en] split(): create statistically independent child generators (for parallel work)
        System.out.println();
        System.out.println("--- split / jump ---");
        SplittableGenerator parent = (SplittableGenerator) RandomGeneratorFactory.of("L64X128MixRandom").create(7);
        System.out.println("child 1: " + parent.split().ints(3, 0, 100).boxed().toList());
        System.out.println("child 2: " + parent.split().ints(3, 0, 100).boxed().toList());

        // [ja] jump()：状態を 2^128 個先まで一気に進める（重ならない部分列を作る）
        // [zh] jump()：把状态一下子推进 2^128 步（得到互不重叠的子序列）
        // [en] jump(): advance the state by 2^128 steps at once (non-overlapping subsequences)
        JumpableGenerator jumpable = (JumpableGenerator) RandomGeneratorFactory.of("Xoshiro256PlusPlus").create(7);
        JumpableGenerator copy = jumpable.copy();
        jumpable.jump();
        System.out.println("before jump: " + copy.nextInt(100) + ", after jump: " + jumpable.nextInt(100)
                + " (jump distance = 2^" + Math.round(Math.log(jumpable.jumpDistance()) / Math.log(2)) + ")");
    }
}
