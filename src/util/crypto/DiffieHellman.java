package util.crypto;

import util.ClientInfo;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by adamz on 14.10.2016.
 */
public class DiffieHellman {
    private static boolean[] primes;
    public static final int primeSize = 100;

    /**
     * Tworzy tablicę z informacją, czy liczba jest liczbą pierwszą dla liczb od 0 do <i>size</i>
     *
     * @param size rozmiar tablicy
     */
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

    /**
     * Sprawdza, czy zadana liczba jest liczbą pierwszą.
     *
     * @param n sprawdzana liczba
     * @return <b>true</b> jeśli <i>n</i> jest liczbą pierwszą lub <b>false</b> jeśli nie jest.
     */
    private static boolean isPrime(int n) {
        if (primes == null) initPrimes(DiffieHellman.primeSize);
        return n < primes.length && primes[n];

    }

    /**
     * Zwraca losową liczbę pierwszą z zakresu 0 - {@value #primeSize}
     *
     * @return liczba pierwsza
     */
    public static Integer generateP() {
        Random r = new Random();
        if (primes == null) initPrimes(primeSize);
        int i = r.nextInt(primes.length);
        while (!isPrime(i)) i = r.nextInt(primes.length);
        return i;
    }

    public static Integer generateG() {
        return 9;
    }

    /**
     * Tworzy klucz szyfrowania na podstawie zadanych wartości po stronie klienta
     *
     * @param info wartości parametrów protokołu Diffiego-Hellmana
     * @return s, klucz szyfrowania
     */
    public static Integer makeClientSecret(ClientInfo info) {
        return (int) Math.pow(info.getB(), info.getSecretA()) % info.getP();
    }

    /**
     * Tworzy klucz szyfrowania na podstawie zadanych wartości po stronie serwera
     *
     * @param info wartości parametrów protokołu Diffiego-Hellmana
     * @return s, klucz szyfrowania
     */
    public static Integer makeServerSecret(ClientInfo info) {
        return (int) Math.pow(info.getA(), info.getSecretB()) % info.getP();
    }

    public static Integer getInitialSecret() {
        Random r = new Random();
        int i = r.nextInt(20) + 5;
        return i;
    }

    public static Integer makeA(ClientInfo info) {
        return (int) Math.pow(info.getG(), info.getSecretA()) % info.getP();
    }

    public static Integer makeB(ClientInfo info) {
        return (int) Math.pow(info.getG(), info.getSecretB()) % info.getP();
    }
}
