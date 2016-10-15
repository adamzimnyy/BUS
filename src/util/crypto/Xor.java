package util.crypto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by adamz on 15.10.2016.
 */
public class Xor {

    public static String encrypt(String string, int secret){
        byte b = (byte) secret;
        StringBuilder encrypted = new StringBuilder();
        for(byte c : string.getBytes(StandardCharsets.UTF_8)){
           encrypted.append((char)(c ^ b));
        }
        return encrypted.toString();
    }
}
