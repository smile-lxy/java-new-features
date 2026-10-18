// JEP 452: Key Encapsulation Mechanism API
// Run: java KemDemo.java

import javax.crypto.KEM;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HexFormat;

public class KemDemo {

    public static void main(String[] args) throws Exception {
        // [ja] 受信者：鍵ペアを作り、公開鍵を送信者に渡す
        // [zh] 接收方：生成密钥对，把公钥交给发送方
        // [en] Receiver: create a key pair and hand the public key to the sender
        KeyPair receiverKeys = KeyPairGenerator.getInstance("X25519").generateKeyPair();

        // [ja] 送信者：公開鍵から「共有鍵」と「カプセル（暗号化された鍵情報）」を作る
        // [zh] 发送方：用公钥生成「共享密钥」和「封装数据（加密后的密钥信息）」
        // [en] Sender: from the public key, create the shared key and the encapsulation (the encrypted key material)
        KEM kem = KEM.getInstance("DHKEM");
        KEM.Encapsulated enc = kem.newEncapsulator(receiverKeys.getPublic()).encapsulate();
        SecretKey senderKey = enc.key();
        // [ja] 受信者に送るのはカプセルだけ
        // [zh] 发给接收方的只有封装数据
        // [en] Only the encapsulation is sent to the receiver
        byte[] capsule = enc.encapsulation();

        // [ja] 受信者：秘密鍵でカプセルを開け、同じ共有鍵を取り出す
        // [zh] 接收方：用私钥打开封装数据，取出同一个共享密钥
        // [en] Receiver: open the encapsulation with the private key and recover the same shared key
        SecretKey receiverKey = kem.newDecapsulator(receiverKeys.getPrivate()).decapsulate(capsule);

        HexFormat hex = HexFormat.of();
        System.out.println("encapsulation (" + capsule.length + " bytes): " + hex.formatHex(capsule).substring(0, 32) + "...");
        System.out.println("sender's shared key  : " + hex.formatHex(senderKey.getEncoded()));
        System.out.println("receiver's shared key: " + hex.formatHex(receiverKey.getEncoded()));
        System.out.println("equal: " + java.util.Arrays.equals(senderKey.getEncoded(), receiverKey.getEncoded()));
    }
}
