package org.taiji.geo.tool.geohash;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Base4 extends GeoHash{
    public static final int CHAR_BIT_LEN = 2;

    private static final Map<Integer, Base4> map = new ConcurrentHashMap<>();

    private static final char[] chars = new char[]{
        '0', // 0000 -> 0
        '1', // 0001 -> 1
        '2', // 0010 -> 2
        '3', // 0011 -> 3
    };

    private Base4(int length) {
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

    public static Base4 getBase4(int length) {
        return map.computeIfAbsent(length, Base4::new);
    }

    public static Base4 getBase4ByDistance(double distance) {
        return getBase4(minimalHashLenByDistance(distance));
    }
}
