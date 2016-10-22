package util.crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * Created by adamz on 15.10.2016.
 */
public class Xor {

    public static byte[] encrypt(String string, BigInteger secret) {
        byte[] a = string.getBytes();
        byte[] key = secret.toByteArray();
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[key.length-1]);
        }
        return out;
    }
}
