package util.crypto;

/**
 * Created by adamz on 14.10.2016.
 */
public class Caesar {
    public static String encrypt(String string, int shift) {
        System.out.println("Caesar:\n\t encrypt "+shift+" -> "+string);
        StringBuilder encrypted = new StringBuilder();

        for (char c : string.toCharArray()) {
            int ascii = (int) c;
            ascii += (shift % 26);
            if (ascii > (Character.isLowerCase(c) ? 'z' : 'Z')) ascii -= 26;
            encrypted.append((char) ascii);
        }
        System.out.println("\t"+encrypted.toString());

        return encrypted.toString();
    }

    public static String decrypt(String string, int shift) {
        System.out.println("Caesar:\n\t encrypt "+shift+" -> "+string);

        StringBuilder decrypted = new StringBuilder();
        for (char c : string.toCharArray()) {
            int ascii = (int) c;
            ascii -= (shift % 26);
            if (ascii < (Character.isLowerCase(c) ? 'a' : 'A')) ascii += 26;
            decrypted.append((char) ascii);
        }
        System.out.println("\t"+decrypted.toString());

        return decrypted.toString();
    }
}
