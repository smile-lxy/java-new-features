# Java 17 samples

Runnable samples for the 14 JEPs of Java 17 (LTS), plus the Java 12-16 features you get when moving from Java 11.

- Every sample is a single file with no external libraries. Run it directly from source (JEP 330); no compilation needed.
- Comments are written in Japanese (`[ja]`), Chinese (`[zh]`) and English (`[en]`). All output is in English.
- Tested on OpenJDK 17 (17.0.20).
- Article: [日本語](https://blog.issyuu.com/post/java17-new-features/) / [中文](https://blog.issyuu.com/zh/post/java17-new-features/) / [English](https://blog.issyuu.com/en/post/java17-new-features/)

| File | Topic | Command |
| --- | --- | --- |
| [`Java12To16Demo.java`](Java12To16Demo.java) | Java 12-16: records, text blocks, switch expressions, instanceof patterns, `Stream.toList()`, helpful NPEs | `java Java12To16Demo.java` |
| [`SealedClassesDemo.java`](SealedClassesDemo.java) | JEP 409: Sealed Classes | `java SealedClassesDemo.java` |
| [`SwitchPatternDemo.java`](SwitchPatternDemo.java) | JEP 406: Pattern Matching for switch (preview in Java 17) | `java --enable-preview --source 17 SwitchPatternDemo.java` |
| [`StrictFpDemo.java`](StrictFpDemo.java) | JEP 306: Restore Always-Strict Floating-Point Semantics | `java StrictFpDemo.java` |
| [`RandomGeneratorDemo.java`](RandomGeneratorDemo.java) | JEP 356: Enhanced Pseudo-Random Number Generators | `java RandomGeneratorDemo.java` |
| [`RandomContentionDemo.java`](RandomContentionDemo.java) | JEP 356: sharing one `Random` across threads vs `ThreadLocalRandom` vs `split()` | `java RandomContentionDemo.java` |
| [`EncapsulationDemo.java`](EncapsulationDemo.java) | JEP 403: Strongly Encapsulate JDK Internals | `java EncapsulationDemo.java`<br>`java --add-opens java.base/java.lang=ALL-UNNAMED EncapsulationDemo.java` |
| [`DeserializationFilterDemo.java`](DeserializationFilterDemo.java) | JEP 415: Context-Specific Deserialization Filters | `java DeserializationFilterDemo.java` |
| [`ForeignMemoryDemo.java`](ForeignMemoryDemo.java) | JEP 412: Foreign Function & Memory API (incubator in Java 17) | `java --add-modules jdk.incubator.foreign --enable-native-access=ALL-UNNAMED ForeignMemoryDemo.java` |
| [`VectorApiDemo.java`](VectorApiDemo.java) | JEP 414: Vector API (second incubator in Java 17) | `java --add-modules jdk.incubator.vector VectorApiDemo.java` |
| [`RemovalsDemo.java`](RemovalsDemo.java) | JEP 398 / 407 / 410 / 411: deprecations and removals | `java RemovalsDemo.java` |
| [`HexFormatDemo.java`](HexFormatDemo.java) | Java 17 APIs outside the JEPs: `HexFormat` and `InstantSource` | `java HexFormatDemo.java` |

`SwitchPatternDemo.java` uses the Java 17 preview syntax (`&&` guards), which was replaced by `when` in JDK 19, so it does not compile on later JDKs. `ForeignMemoryDemo.java` uses the Java 17 incubator API (`jdk.incubator.foreign`), which changed completely before it became standard in JDK 22 (`java.lang.foreign`). `RemovalsDemo.java` installs a Security Manager, which throws `UnsupportedOperationException` on JDK 18 and later.

---

## 日本語

Java 17 の 14 個の JEP と、Java 11 から移行するときに一緒に使えるようになる Java 12〜16 の機能を、動かして確かめるサンプル。すべて 1 ファイルで完結し、外部ライブラリは不要。ソースファイルを直接起動する方式（JEP 330）で実行するので、コンパイルは不要。コメントは日本語・中国語・英語の 3 言語、出力は英語。実行コマンドは上の表と各ファイル先頭の `// Run:` を参照。プレビュー・インキュベーターのサンプル（SwitchPatternDemo、ForeignMemoryDemo）と RemovalsDemo は Java 17 専用。

## 中文

用来实际运行、验证 Java 17 的 14 个 JEP，以及从 Java 11 迁移时一起可用的 Java 12〜16 特性的示例代码。每个示例都是单个文件，不依赖外部库，直接运行源文件（JEP 330），不需要编译。注释用日语、中文、英语三种语言写成，输出为英文。运行命令见上表和每个文件开头的 `// Run:`。预览和孵化特性的示例（SwitchPatternDemo、ForeignMemoryDemo）以及 RemovalsDemo 只能在 Java 17 上运行。
