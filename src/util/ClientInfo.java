package util;

import java.math.BigInteger;
import java.net.Socket;

/**
 * Created by adamz on 10.10.2016.
 */
public class ClientInfo {
   private BigInteger p,g,A,B,secretA,secretB,s;
   private String encryption;

    private int port, id;
    private String name;

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }

    public BigInteger getA() {
        return A;
    }

    public void setA(BigInteger a) {
        A = a;
    }

    public BigInteger getB() {
        return B;
    }

    public void setB(BigInteger b) {
        B = b;
    }

    public BigInteger getSecretA() {
        return secretA;
    }

    public void setSecretA(BigInteger secretA) {
        this.secretA = secretA;
    }

    public BigInteger getSecretB() {
        return secretB;
    }

    public void setSecretB(BigInteger secretB) {
        this.secretB = secretB;
    }

    public BigInteger getS() {
        return s;
    }

    public void setS(BigInteger s) {
        this.s = s;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return A != null && B != null && p != null & g != null;
    }

    @Override
    public String toString() {
        return "ClientInfo\n{" +
                "\n\tid=" + id +
                ", \n\tp=" + p +
                ", \n\tg=" + g +
                ", \n\tA=" + A +
                ", \n\tB=" + B +
                ", \n\tsecretA=" + secretA +
                ", \n\tsecretB=" + secretB +
                ", \n\ts=" + s +
                "\n}";
    }
}
