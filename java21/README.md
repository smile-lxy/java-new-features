# Java 21 samples

Runnable samples for the 15 JEPs of Java 21 (LTS).

- Every sample is a single file with no external libraries. Run it directly from source (JEP 330); no compilation needed.
- Comments are written in Japanese (`[ja]`), Chinese (`[zh]`) and English (`[en]`). All output is in English.
- Tested on OpenJDK 21 (21.0.12).
- Article: [日本語](https://blog.issyuu.com/post/java21-new-features/) / [中文](https://blog.issyuu.com/zh/post/java21-new-features/) / [English](https://blog.issyuu.com/en/post/java21-new-features/)

| File | Topic | Command |
| --- | --- | --- |
| [`RecordPatternsDemo.java`](RecordPatternsDemo.java) | JEP 440: Record Patterns | `java RecordPatternsDemo.java` |
| [`SwitchPatternDemo.java`](SwitchPatternDemo.java) | JEP 441: Pattern Matching for switch | `java SwitchPatternDemo.java` |
| [`UnnamedVariablesDemo.java`](UnnamedVariablesDemo.java) | JEP 443: Unnamed Patterns and Variables (preview in Java 21) | `java --enable-preview --source 21 UnnamedVariablesDemo.java` |
| [`StringTemplatesDemo.java`](StringTemplatesDemo.java) | JEP 430: String Templates (preview in Java 21, withdrawn in JDK 23) | `java --enable-preview --source 21 StringTemplatesDemo.java` |
| [`InstanceMainDemo.java`](InstanceMainDemo.java) | JEP 445: Unnamed Classes and Instance Main Methods (preview in Java 21) | `java --enable-preview --source 21 InstanceMainDemo.java` |
| [`SequencedCollectionsDemo.java`](SequencedCollectionsDemo.java) | JEP 431: Sequenced Collections | `java SequencedCollectionsDemo.java` |
| [`VirtualThreadCreateDemo.java`](VirtualThreadCreateDemo.java) | JEP 444: Virtual Threads - four ways to create a virtual thread | `java VirtualThreadCreateDemo.java` |
| [`VirtualThreadScheduleDemo.java`](VirtualThreadScheduleDemo.java) | JEP 444: Virtual Threads - run 4 virtual threads on 2 carrier threads and watch them mount / unmount | `java VirtualThreadScheduleDemo.java` |
| [`ThreadPoolVsVirtualDemo.java`](ThreadPoolVsVirtualDemo.java) | JEP 444: Virtual Threads - limits of a thread pool vs virtual threads on a single carrier | `java ThreadPoolVsVirtualDemo.java` |
| [`ThroughputDemo.java`](ThroughputDemo.java) | JEP 444: Virtual Threads - 10,000 tasks that block for 1 s: fixed thread pool vs virtual threads | `java ThroughputDemo.java` |
| [`MemoryCostDemo.java`](MemoryCostDemo.java) | JEP 444: Virtual Threads - memory per platform thread vs per virtual thread | `java MemoryCostDemo.java` |
| [`VirtualThreadOomDemo.java`](VirtualThreadOomDemo.java) | JEP 444: Virtual Threads - what happens if you keep creating them without a limit | `java -Xmx128m VirtualThreadOomDemo.java` |
| [`PinningDemo.java`](PinningDemo.java) | JEP 444: Virtual Threads - pinning caused by synchronized (JDK 21-23) | `java -Djdk.tracePinnedThreads=short PinningDemo.java` |
| [`ScopedValueDemo.java`](ScopedValueDemo.java) | JEP 446: Scoped Values (preview in Java 21) | `java --enable-preview --source 21 ScopedValueDemo.java` |
| [`StructuredConcurrencyDemo.java`](StructuredConcurrencyDemo.java) | JEP 453: Structured Concurrency (preview in Java 21) | `java --enable-preview --source 21 StructuredConcurrencyDemo.java` |
| [`GenerationalZgcDemo.java`](GenerationalZgcDemo.java) | JEP 439: Generational ZGC | `java -XX:+UseZGC -XX:+ZGenerational -Xmx1g GenerationalZgcDemo.java`<br>`java -XX:+UseZGC -Xmx1g GenerationalZgcDemo.java` |
| [`ForeignFunctionDemo.java`](ForeignFunctionDemo.java) | JEP 442: Foreign Function & Memory API (third preview in Java 21) | `java --enable-preview --source 21 --enable-native-access=ALL-UNNAMED ForeignFunctionDemo.java` |
| [`VectorApiDemo.java`](VectorApiDemo.java) | JEP 448: Vector API (sixth incubator in Java 21) | `java --add-modules jdk.incubator.vector VectorApiDemo.java` |
| [`KemDemo.java`](KemDemo.java) | JEP 452: Key Encapsulation Mechanism API | `java KemDemo.java` |

Preview features (`--enable-preview --source 21`) use the Java 21 APIs. String Templates were withdrawn in JDK 23, so that sample does not compile on JDK 23 or later. The APIs of Scoped Values, Structured Concurrency and the FFM API changed in later releases; see the "Since then" notes in the article.

---

## 日本語

Java 21 の 15 個の JEP を動かして確かめるサンプル。すべて 1 ファイルで完結し、外部ライブラリは不要。ソースファイルを直接起動する方式（JEP 330）で実行するので、コンパイルは不要。コメントは日本語・中国語・英語の 3 言語、出力は英語。実行コマンドは上の表と各ファイル先頭の `// Run:` を参照。

## 中文

用来实际运行、验证 Java 21 的 15 个 JEP 的示例代码。每个示例都是单个文件，不依赖外部库，直接运行源文件（JEP 330），不需要编译。注释用日语、中文、英语三种语言写成，输出为英文。运行命令见上表和每个文件开头的 `// Run:`。
