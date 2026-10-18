# Java 25 samples

Runnable samples for the JEPs of Java 25 (LTS).

- Every sample is a single file with no external libraries. Run it directly from source (JEP 330); no compilation needed (only `AotCacheDemo.java` builds a JAR, because the AOT cache does not accept directories on the class path).
- Comments are written in Japanese (`[ja]`), Chinese (`[zh]`) and English (`[en]`). All output is in English.
- Tested on OpenJDK 25 (25.0.4).
- Article: [日本語](https://blog.issyuu.com/post/java25-new-features/) / [中文](https://blog.issyuu.com/zh/post/java25-new-features/) / [English](https://blog.issyuu.com/en/post/java25-new-features/)

| File | Topic | Command |
| --- | --- | --- |
| [`CompactSourceDemo.java`](CompactSourceDemo.java) | JEP 512: Compact Source Files and Instance Main Methods | `java CompactSourceDemo.java` |
| [`ModuleImportDemo.java`](ModuleImportDemo.java) | JEP 511: Module Import Declarations | `java ModuleImportDemo.java` |
| [`FlexibleConstructorDemo.java`](FlexibleConstructorDemo.java) | JEP 513: Flexible Constructor Bodies | `java FlexibleConstructorDemo.java` |
| [`PrimitivePatternsDemo.java`](PrimitivePatternsDemo.java) | JEP 507: Primitive Types in Patterns, instanceof, and switch (third preview in JDK 25) | `java --enable-preview PrimitivePatternsDemo.java` |
| [`ScopedValueDemo.java`](ScopedValueDemo.java) | JEP 506: Scoped Values | `java ScopedValueDemo.java` |
| [`StructuredConcurrencyDemo.java`](StructuredConcurrencyDemo.java) | JEP 505: Structured Concurrency (fifth preview in JDK 25) | `java --enable-preview StructuredConcurrencyDemo.java` |
| [`StableValueDemo.java`](StableValueDemo.java) | JEP 502: Stable Values (preview in JDK 25) | `java --enable-preview StableValueDemo.java` |
| [`KdfDemo.java`](KdfDemo.java) | JEP 510: Key Derivation Function API | `java KdfDemo.java` |
| [`PemDemo.java`](PemDemo.java) | JEP 470: PEM Encodings of Cryptographic Objects (preview in JDK 25) | `java --enable-preview PemDemo.java` |
| [`VectorApiDemo.java`](VectorApiDemo.java) | JEP 508: Vector API (tenth incubator in JDK 25) | `java --add-modules jdk.incubator.vector VectorApiDemo.java` |
| [`CompactHeadersDemo.java`](CompactHeadersDemo.java) | JEP 519: Compact Object Headers - heap used by 10 million small objects | `java -Xmx2g CompactHeadersDemo.java`<br>`java -Xmx2g -XX:+UseCompactObjectHeaders CompactHeadersDemo.java` |
| [`AotCacheDemo.java`](AotCacheDemo.java) | JEP 514 / 515: AOT cache - startup time with and without the cache | `javac -d out AotCacheDemo.java && jar --create --file app.jar -C out .`<br>`java -cp app.jar AotCacheDemo`<br>`java -XX:AOTCacheOutput=app.aot -cp app.jar AotCacheDemo`<br>`java -XX:AOTCache=app.aot -cp app.jar AotCacheDemo` |
| [`GenerationalShenandoahDemo.java`](GenerationalShenandoahDemo.java) | JEP 521: Generational Shenandoah | `java -XX:+UseShenandoahGC -XX:ShenandoahGCMode=generational -Xmx2g GenerationalShenandoahDemo.java`<br>`java -XX:+UseShenandoahGC -Xmx2g GenerationalShenandoahDemo.java` |
| [`MethodTimingDemo.java`](MethodTimingDemo.java) | JEP 520: JFR Method Timing & Tracing | `java '-XX:StartFlightRecording:method-timing=MethodTimingDemo::parse;java.lang.Integer::parseInt,filename=timing.jfr' MethodTimingDemo.java`<br>`jfr view method-timing timing.jfr` |

Preview features (`--enable-preview`) use the JDK 25 APIs. Stable Values were renamed Lazy Constants in JDK 26 (JEP 526), and the Structured Concurrency API changed slightly in JDK 26 (JEP 525), so those samples need changes on later JDKs. JEP 509 (JFR CPU-time profiling) is Linux-only and has no sample here; JEP 503 and JEP 518 have nothing to run.

---

## 日本語

Java 25 の JEP を動かして確かめるサンプル。すべて 1 ファイルで完結し、外部ライブラリは不要。ソースファイルを直接起動する方式（JEP 330）で実行するので、コンパイルは不要（AOT キャッシュのサンプルだけは JAR を作る）。コメントは日本語・中国語・英語の 3 言語、出力は英語。実行コマンドは上の表と各ファイル先頭の `// Run:` を参照。プレビュー機能のサンプルは JDK 25 専用（Stable Values は JDK 26 で Lazy Constants に改名された）。

## 中文

用来实际运行、验证 Java 25 各个 JEP 的示例代码。每个示例都是单个文件，不依赖外部库，直接运行源文件（JEP 330），不需要编译（只有 AOT 缓存的示例需要打 JAR 包）。注释用日语、中文、英语三种语言写成，输出为英文。运行命令见上表和每个文件开头的 `// Run:`。预览特性的示例只能在 JDK 25 上运行（Stable Values 在 JDK 26 改名为 Lazy Constants）。
