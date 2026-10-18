// JEP 510: Key Derivation Function API
// Run: java KdfDemo.java

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.KDF;
import javax.crypto.SecretKey;
import javax.crypto.spec.HKDFParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class KdfDemo {

    public static void main(String[] args) throws Exception {
        // [ja] 鍵交換などで得た「元になる秘密」（本来はランダムな値）
        // [zh] 通过密钥交换等得到的“原始秘密”（实际中应是随机值）
        // [en] The "input keying material", e.g. from a key exchange (random in real use)
        byte[] ikm = HexFormat.of().parseHex("0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b");
        byte[] salt = HexFormat.of().parseHex("000102030405060708090a0b0c");

        KDF hkdf = KDF.getInstance("HKDF-SHA256");

        // [ja] 同じ秘密から、用途（info）ごとに別々の鍵を導出する
        // [zh] 从同一个秘密出发，按用途（info）派生出不同的密钥
        // [en] Derive separate keys from the same secret, one per purpose (info)
        for (String purpose : new String[] {"encryption", "mac"}) {
            var params = HKDFParameterSpec.ofExtract()
                    .addIKM(new SecretKeySpec(ikm, "Generic"))
                    .addSalt(salt)
                    .thenExpand(purpose.getBytes(StandardCharsets.UTF_8), 32);
            SecretKey key = hkdf.deriveKey(purpose.equals("mac") ? "HmacSHA256" : "AES", params);
            System.out.printf("%-10s -> %s (%s, %d bytes)%n", purpose,
                    HexFormat.of().formatHex(key.getEncoded()), key.getAlgorithm(), key.getEncoded().length);
        }

        // [ja] RFC 5869 のテストケース 1 と同じ入力：OKM が仕様の値と一致するか確かめる
        // [zh] 与 RFC 5869 测试用例 1 相同的输入：检查 OKM 是否与规范中的值一致
        // [en] The same input as RFC 5869 test case 1: check that the OKM matches the spec
        byte[] info = HexFormat.of().parseHex("f0f1f2f3f4f5f6f7f8f9");
        byte[] okm = hkdf.deriveData(HKDFParameterSpec.ofExtract()
                .addIKM(new SecretKeySpec(ikm, "Generic")).addSalt(salt).thenExpand(info, 42));
        String expected = "3cb25f25faacd57a90434f64d0362f2a2d2d0a90cf1a5a4c5db02d56ecc4c5bf34007208d5b887185865";
        System.out.println("RFC 5869 test case 1 matches: " + HexFormat.of().formatHex(okm).equals(expected));
    }
}
