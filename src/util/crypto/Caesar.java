package util.crypto;

import java.math.BigInteger;

/**
 * Created by adamz on 14.10.2016.
 */
public class Caesar {
    public static String encrypt(String string, BigInteger shift) {
        StringBuilder encrypted = new StringBuilder();

        for (char c : string.toCharArray()) {
            int ascii = (int) c;
            ascii += (shift.mod(BigInteger.valueOf(26))).intValue();
            if (ascii > (Character.isLowerCase(c) ? 'z' : 'Z')) ascii -= 26;
            encrypted.append((char) ascii);
        }
        return encrypted.toString();
    }

    public static String decrypt(String string, BigInteger shift) {

        StringBuilder decrypted = new StringBuilder();
        for (char c : string.toCharArray()) {
            int ascii = (int) c;
            ascii -= (shift.mod(BigInteger.valueOf(26))).intValue(); if (ascii < (Character.isLowerCase(c) ? 'a' : 'A')) ascii += 26;
            decrypted.append((char) ascii);
        }
        return decrypted.toString();
    }
}
