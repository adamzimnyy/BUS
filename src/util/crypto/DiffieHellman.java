package util.crypto;

import util.ClientInfo;
import util.Variables;

import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by adamz on 14.10.2016.
 */
public class DiffieHellman {
    private static boolean[] primes;
    public static final int primeSize = 1000;

    public static void initPrimes(int size) {
        primes = new boolean[size];
        Arrays.fill(primes, true);        // assume all integers are prime.
        primes[0] = primes[1] = false;       // we know 0 and 1 are not prime.
        for (int i = 2; i < primes.length; i++) {
            //if the number is prime,
            //then go through all its multiples and make their values false.
            if (primes[i]) {
                for (int j = 2; i * j < primes.length; j++) {
                    primes[i * j] = false;
                }
            }
        }
    }

    private static boolean isPrime(int n) {
        if (primes == null) initPrimes(DiffieHellman.primeSize);
        return n < primes.length && primes[n];
    }

    public static BigInteger generateP() throws NoSuchAlgorithmException, InvalidParameterSpecException {
        if (Variables.useBigNumbers) {
            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(512); // number of bits
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec dhSpec = params.getParameterSpec(DHParameterSpec.class);
           return dhSpec.getP();
        } else {

            Random r = new Random();
            if (primes == null) initPrimes(primeSize);
            int i = r.nextInt(primes.length);
            while (!isPrime(i)) i = r.nextInt(primes.length);
            return BigInteger.valueOf(i);
        }
    }

    public static BigInteger generateG() throws NoSuchAlgorithmException, InvalidParameterSpecException {
        if (Variables.useBigNumbers) {
            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(512); // number of bits
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec dhSpec = params.getParameterSpec(DHParameterSpec.class);

            return dhSpec.getG();
        } else {
            Random r = new Random();

            return BigInteger.valueOf(r.nextInt(30));
        }
    }

    public static BigInteger makeClientSecret(ClientInfo info) {
        return info.getB().pow(info.getSecretA().intValue()).mod(info.getP());
        //  return Math.pow(info.getB(), info.getSecretA()) % info.getP();
    }

    public static BigInteger makeServerSecret(ClientInfo info) {
        return info.getA().pow(info.getSecretB().intValue()).mod(info.getP());

        //  return (int) Math.pow(info.getA(), info.getSecretB()) % info.getP();
    }

    public static BigInteger getInitialSecret() {
        Random r = new Random();
        int i = r.nextInt(50) + 11;
        return BigInteger.valueOf(i);
    }

    public static BigInteger makeA(ClientInfo info) {
        return info.getG().pow(info.getSecretA().intValue()).mod(info.getP());
        //  return (int) Math.pow(info.getG(), info.getSecretA()) % info.getP();
    }

    public static BigInteger makeB(ClientInfo info) {
        return info.getG().pow(info.getSecretB().intValue()).mod(info.getP());
        //  return (int) Math.pow(info.getG(), info.getSecretB()) % info.getP();
    }
}
