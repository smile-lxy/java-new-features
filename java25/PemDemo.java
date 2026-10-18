// JEP 470: PEM Encodings of Cryptographic Objects (preview in JDK 25)
// Run: java --enable-preview PemDemo.java

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PEMDecoder;
import java.security.PEMEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class PemDemo {

    public static void main(String[] args) throws Exception {
        KeyPair pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();

        // [ja] 公開鍵を PEM 文字列にする（これまでは Base64 と BEGIN/END 行を自分で組み立てていた）
        // [zh] 把公钥转成 PEM 字符串（以前需要自己拼 Base64 和 BEGIN/END 行）
        // [en] Turn a public key into a PEM string (previously you assembled Base64 and the BEGIN/END lines yourself)
        String publicPem = PEMEncoder.of().encodeToString(pair.getPublic());
        System.out.print(publicPem);

        // [ja] PEM 文字列から鍵オブジェクトに戻す
        // [zh] 从 PEM 字符串还原为密钥对象
        // [en] Turn the PEM string back into a key object
        PublicKey decoded = PEMDecoder.of().decode(publicPem, PublicKey.class);
        System.out.println("decoded: " + decoded.getAlgorithm() + ", equal = "
                + Arrays.equals(decoded.getEncoded(), pair.getPublic().getEncoded()));

        // [ja] 秘密鍵はパスワードで暗号化して PEM にできる
        // [zh] 私钥可以用密码加密后再转成 PEM
        // [en] A private key can be encrypted with a password before encoding
        char[] password = "correct horse battery staple".toCharArray();
        String encryptedPem = PEMEncoder.of().withEncryption(password).encodeToString(pair.getPrivate());
        System.out.println(encryptedPem.lines().findFirst().orElseThrow() + " ... ("
                + encryptedPem.lines().count() + " lines)");
        PrivateKey privateKey = PEMDecoder.of().withDecryption(password).decode(encryptedPem, PrivateKey.class);
        System.out.println("decrypted: " + privateKey.getAlgorithm() + ", equal = "
                + Arrays.equals(privateKey.getEncoded(), pair.getPrivate().getEncoded()));
    }
}
