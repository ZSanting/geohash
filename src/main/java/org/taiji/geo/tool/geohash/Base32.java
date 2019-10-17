package org.taiji.geo.tool.geohash;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Base32 extends GeoHash {
    public static final int CHAR_BIT_LEN = 5;

    private static final Map<Integer, Base32> map = new ConcurrentHashMap<>();

    private static final char[] chars = new char[]{
        '0', // 00000 -> 0
        '1', // 00001 -> 1
        '2', // 00010 -> 2
        '3', // 00011 -> 3
        '4', // 00100 -> 4
        '5', // 00101 -> 5
        '6', // 00110 -> 6
        '7', // 00111 -> 7
        '8', // 01000 -> 8
        '9', // 01001 -> 9
        'b', // 01010 -> b
        'c', // 01011 -> c
        'd', // 01100 -> d
        'e', // 01101 -> e
        'f', // 01110 -> f
        'g', // 01111 -> g
        'h', // 10000 -> h
        'j', // 10001 -> j
        'k', // 10010 -> k
        'm', // 10011 -> m
        'n', // 10100 -> n
        'p', // 10101 -> p
        'q', // 10110 -> q
        'r', // 10111 -> r
        's', // 11000 -> s
        't', // 11001 -> t
        'u', // 11010 -> u
        'v', // 11011 -> v
        'w', // 11100 -> w
        'x', // 11101 -> x
        'y', // 11110 -> y
        'z', // 11111 -> z
    };

    private Base32(int length) {
        super(length, CHAR_BIT_LEN);
    }

    @Override
    protected char charEncode(byte b) {
        return chars[b];
    }

    @Override
    protected byte charDecode(char c) {
        return (byte) Arrays.binarySearch(chars, c);
    }

    @Override
    public char[] charSet() {
        return chars;
    }

    public static int minimalHashLenByDistance(double distance) {
        int bitLen = minimalBitLenByDistance(distance);
        return bitLen / CHAR_BIT_LEN;
    }

    public static Base32 getBase32(int length) {
        return map.computeIfAbsent(length, Base32::new);
    }

    public static Base32 getBase32ByDistance(double distance) {
        return getBase32(minimalHashLenByDistance(distance));
    }
}
