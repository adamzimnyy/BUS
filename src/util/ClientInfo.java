package util;

import java.net.Socket;

/**
 * Created by adamz on 10.10.2016.
 */
public class ClientInfo {
   private Integer p,g,A,B,secretA,secretB,s;
   private String encryption;

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public Integer getSecretA() {
        return secretA;
    }

    public void setSecretA(Integer secretA) {
        this.secretA = secretA;
    }

    public Integer getSecretB() {
        return secretB;
    }

    public void setSecretB(Integer secretB) {
        this.secretB = secretB;
    }

    private int port, id;
    private String name;



    public Integer getP() {
        return p;
    }

    public void setP(Integer p) {
        this.p = p;
    }

    public Integer getG() {
        return g;
    }

    public void setG(Integer g) {
        this.g = g;
    }

    public Integer getA() {
        return A;
    }

    public void setA(Integer a) {
        A = a;
    }

    public Integer getB() {
        return B;
    }

    public void setB(Integer b) {
        B = b;
    }

    public Integer getS() {
        return s;
    }

    public void setS(Integer s) {
        this.s = s;
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
}
