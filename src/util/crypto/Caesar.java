package util.crypto;

/**
 * Created by adamz on 14.10.2016.
 */
public class Caesar {
    public static String encrypt(String string, int shift) {
        StringBuilder encrypted = new StringBuilder();

        for (char c : string.toCharArray()) {
            int ascii = (int) c;
            ascii += (shift % 26);
            if (ascii > (Character.isLowerCase(c) ? 'z' : 'Z')) ascii -= 26;
            encrypted.append((char) ascii);
        }
        return encrypted.toString();
    }

    public static String decrypt(String string, int shift) {

        StringBuilder decrypted = new StringBuilder();
        for (char c : string.toCharArray()) {
            int ascii = (int) c;
            ascii -= (shift % 26);
            if (ascii < (Character.isLowerCase(c) ? 'a' : 'A')) ascii += 26;
            decrypted.append((char) ascii);
        }
        return decrypted.toString();
    }
}
