package util;

import java.util.Random;

/**
 * Created by adamz on 14.10.2016.
 */
public class DiffieHellman {


    public static Integer generateP() {
        return 7;
    }

    public static Integer generateG() {
        return 9;
    }

    public static Integer makeClientSecret(ClientInfo info) {
        System.out.println("DH:\n\tclientSecret = " + (int) Math.pow(info.getB(), info.getSecretA()) % info.getP());

        return (int) Math.pow(info.getB(), info.getSecretA()) % info.getP();
    }

    public static Integer makeServerSecret(ClientInfo info) {
        System.out.println("DH:\n\tserverSecret = " + (int) Math.pow(info.getA(), info.getSecretB()) % info.getP());

        return (int) Math.pow(info.getA(), info.getSecretB()) % info.getP();
    }

    public static Integer getInitialSecret() {
        Random r = new Random();
        int i = r.nextInt(20)+5;
        System.out.println("DH:\n\tinitialSecret = " + i);

        return i;
    }

    public static Integer makeA(ClientInfo info) {
        System.out.println("DH:\n\tmakeA = " + (int) Math.pow(info.getG(), info.getSecretA()) % info.getP());
        return (int) Math.pow(info.getG(), info.getSecretA()) % info.getP();
    }

    public static Integer makeB(ClientInfo info) {
        System.out.println("DH:\n\tmakeB = " + (int) Math.pow(info.getG(), info.getSecretB()) % info.getP());

        return (int) Math.pow(info.getG(), info.getSecretB()) % info.getP();
    }
}
