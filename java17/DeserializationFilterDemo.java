// JEP 415: Context-Specific Deserialization Filters
// Run: java DeserializationFilterDemo.java

import java.io.*;
import java.util.ArrayList;
import java.util.function.BinaryOperator;

public class DeserializationFilterDemo {

    record Order(String id, int amount) implements Serializable {}

    // [ja] 攻撃に使われうる「想定外のクラス」の例
    // [zh] 可能被用于攻击的“意料之外的类”的示例
    // [en] An example of an "unexpected class" that an attacker might send
    static class Gadget implements Serializable {}

    static byte[] serialize(Object o) throws IOException {
        var out = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(out)) {
            oos.writeObject(o);
        }
        return out.toByteArray();
    }

    static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (var ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return ois.readObject();
        }
    }

    public static void main(String[] args) throws Exception {
        // [ja] JVM 全体の基本ルール：ネストの深さと配列の長さを制限する
        // [zh] 整个 JVM 的基本规则：限制嵌套深度和数组长度
        // [en] JVM-wide base rule: limit the nesting depth and array length
        ObjectInputFilter base = ObjectInputFilter.Config.createFilter("maxdepth=10;maxarray=1000");

        // [ja] Java 17 の新機能：フィルターファクトリ。ストリームごとに「どのフィルターを使うか」を決める
        // [zh] Java 17 新功能：过滤器工厂。为每个流决定“使用哪个过滤器”
        // [en] New in Java 17: a filter factory decides which filter each stream uses
        BinaryOperator<ObjectInputFilter> factory = (current, requested) -> {
            if (requested == null) {
                // [ja] ストリーム作成時：基本ルールを適用する
                // [zh] 创建流时：应用基本规则
                // [en] When the stream is created: apply the base rule
                return base;
            }
            // [ja] setObjectInputFilter() が呼ばれたとき：基本ルールと個別ルールを合成し、どちらも未判定なら拒否する
            // [zh] 调用 setObjectInputFilter() 时：合并基本规则和个别规则，都无法判断时拒绝
            // [en] When setObjectInputFilter() is called: merge the base and the specific rule, and reject if both are undecided
            return ObjectInputFilter.rejectUndecidedClass(ObjectInputFilter.merge(requested, current));
        };
        ObjectInputFilter.Config.setSerialFilterFactory(factory);

        // [ja] 許可リスト（DeserializationFilterDemo$Order と java.lang の一部だけ）
        // [zh] 白名单（只允许 DeserializationFilterDemo$Order 和 java.lang 的一部分）
        // [en] An allowlist (only DeserializationFilterDemo$Order and part of java.lang)
        ObjectInputFilter allowOrders = ObjectInputFilter.Config.createFilter(
                "DeserializationFilterDemo$Order;java.lang.String;java.lang.Number;java.lang.Integer");

        Object[] inputs = {new Order("A-1", 300), new Gadget(), new int[5000]};
        for (Object input : inputs) {
            byte[] bytes = serialize(input);
            try (var ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                ois.setObjectInputFilter(allowOrders);
                Object result = ois.readObject();
                System.out.println("ACCEPTED : " + result);
            } catch (InvalidClassException e) {
                System.out.println("REJECTED : " + input.getClass().getSimpleName() + " (" + e.getMessage() + ")");
            }
        }

        // [ja] setObjectInputFilter() を呼ばないストリームにも基本ルール（maxarray=1000）は効く
        // [zh] 即使没有调用 setObjectInputFilter()，基本规则（maxarray=1000）也会生效
        // [en] The base rule (maxarray=1000) also applies to streams that never call setObjectInputFilter()
        System.out.println();
        for (Object input : new Object[] {new ArrayList<>(java.util.List.of(1, 2, 3)), new int[5000]}) {
            try {
                System.out.println("ACCEPTED : " + input.getClass().getSimpleName() + " -> " + deserialize(serialize(input)).getClass().getSimpleName());
            } catch (InvalidClassException e) {
                System.out.println("REJECTED : " + input.getClass().getSimpleName() + " (" + e.getMessage() + ")");
            }
        }
    }
}
