package org.taiji.geo.tool.geohash;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Base16 extends GeoHash {
    public static final int CHAR_BIT_LEN = 4;

    private static final Map<Integer, Base16> map = new ConcurrentHashMap<>();

    private static final char[] chars = new char[]{
        '0', // 0000 -> 0
        '1', // 0001 -> 1
        '2', // 0010 -> 2
        '3', // 0011 -> 3
        '4', // 0100 -> 4
        '5', // 0101 -> 5
        '6', // 0110 -> 6
        '7', // 0111 -> 7
        'a', // 1000 -> a
        'b', // 1001 -> b
        'c', // 1010 -> c
        'd', // 1011 -> d
        'e', // 1100 -> f
        'f', // 1101 -> f
        'g', // 1110 -> g
        'h', // 1111 -> h
    };

    private Base16(int length) {
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

    public static Base16 getBase16(int length) {
        return map.computeIfAbsent(length, Base16::new);
    }

    public static Base16 getBase16ByDistance(double distance) {
        return getBase16(minimalHashLenByDistance(distance));
    }
}
