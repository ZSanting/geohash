package org.taiji.geo.tool.geohash;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class GeoHashTest {
    private static final double LAT = 31.192911;
    private static final double LNG = 121.437013;
    private static final String BASE16_HASH = "g67a33fahfheccd";
    private static final String BASE32_HASH = "wtw37q7xzkpc";
    private static final String BIT_STR = "111001100111100000110011110110001111110111111100101010101011";
    private static final String LAT_BIT_STR = "101011000101110011111110000001";
    private static final String LNG_BIT_STR = "110101100101101011101110111111";
    private static final Logger logger = LoggerFactory.getLogger(GeoHashTest.class);

    @Test
    public void testBitString() {
        assertEquals(GeoHash.toBitString((1 << 4) - 1, 4), "1111");
        assertEquals(GeoHash.toBitString((1 << 30) - 1, 30), "111111111111111111111111111111");
        LinkedHashMap<Long, String> map = new LinkedHashMap<>();
        map.put(0L, "0000");
        map.put(1L, "0001");
        map.put(2L, "0010");
        map.put(3L, "0011");
        map.put(4L, "0100");
        map.put(5L, "0101");
        map.put(6L, "0110");
        map.put(7L, "0111");
        map.put(8L, "1000");
        map.put(9L, "1001");
        map.put(10L, "1010");
        map.put(11L, "1011");
        map.put(12L, "1100");
        map.put(13L, "1101");
        map.put(14L, "1110");
        map.put(15L, "1111");
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            long key = entry.getKey();
            String bitStr = GeoHash.toBitString(key, 4);
            logger.info("input: {}, output: {}", key, bitStr);
            assertEquals(bitStr, entry.getValue());
        }
        map.clear();
        map.put(0L, "00000");
        map.put(1L, "00001");
        map.put(2L, "00010");
        map.put(3L, "00011");
        map.put(4L, "00100");
        map.put(5L, "00101");
        map.put(6L, "00110");
        map.put(7L, "00111");
        map.put(8L, "01000");
        map.put(9L, "01001");
        map.put(10L, "01010");
        map.put(11L, "01011");
        map.put(12L, "01100");
        map.put(13L, "01101");
        map.put(14L, "01110");
        map.put(15L, "01111");
        map.put(16L, "10000");
        map.put(17L, "10001");
        map.put(18L, "10010");
        map.put(19L, "10011");
        map.put(20L, "10100");
        map.put(21L, "10101");
        map.put(22L, "10110");
        map.put(23L, "10111");
        map.put(24L, "11000");
        map.put(25L, "11001");
        map.put(26L, "11010");
        map.put(27L, "11011");
        map.put(28L, "11100");
        map.put(29L, "11101");
        map.put(30L, "11110");
        map.put(31L, "11111");
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            long key = entry.getKey();
            String bitStr = GeoHash.toBitString(key, 5);
            logger.info("input: {}, output: {}", key, bitStr);
            assertEquals(bitStr, entry.getValue());
        }

        Base16 base16 = Base16.getBase16(15);
        long bits = base16.toBits(LAT, LNG);
        String bitStr = GeoHash.toBitString(bits, base16.getBitsLength());
        logger.info(bitStr);
        assertEquals(bitStr, BIT_STR);
    }

    @Test
    public void testBinarySearch() {
        int times = 30;
        int latBits = GeoHash.binarySearch(GeoHash.MIN_LAT, GeoHash.MAX_LAT, LAT, times);
        int lngBits = GeoHash.binarySearch(GeoHash.MIN_LNG, GeoHash.MAX_LNG, LNG, times);
        String latBitStr = GeoHash.toBitString(latBits, 30);
        String lngBitStr = GeoHash.toBitString(lngBits, 30);
        logger.info("lat: {}", latBitStr);
        logger.info("lng: {}", lngBitStr);
        assertEquals(latBitStr, LAT_BIT_STR);
        assertEquals(lngBitStr, LNG_BIT_STR);
        Position position = new Position(GeoHash.binarySearch(GeoHash.MIN_LAT, GeoHash.MAX_LAT, latBits, times), GeoHash.binarySearch(GeoHash.MIN_LNG, GeoHash.MAX_LNG, lngBits, times));
        position.setPrecision(6);
        logger.info("binary search lat: {}", position.getLat());
        logger.info("binary search lng: {}", position.getLng());
        assertTrue(Math.abs(position.getLat() - LAT) < Math.pow(10, position.getPrecision()));
        assertTrue(Math.abs(position.getLng() - LNG) < Math.pow(10, position.getPrecision()) );
    }

    @Test
    public void testBase16() {
        Base16 base16 = Base16.getBase16(15);
        String hash = base16.encode(LAT, LNG);
        logger.info(hash);
        assertEquals(hash, BASE16_HASH);
        String bitStr = GeoHash.toBitString(base16.toBits(LAT, LNG), base16.getBitsLength());
        logger.info(bitStr);
        assertEquals(bitStr, BIT_STR);
        Position position = base16.decode(hash);
        position.setPrecision(6);
        logger.info(position.toString());
        assert (Math.abs(position.getLat() - LAT)) < Math.pow(10, -1 * position.getPrecision());
        assert (Math.abs(position.getLng() - LNG)) < Math.pow(10, -1 * position.getPrecision());
    }

    @Test
    public void testBase16Char() {
        Base16 base16 = Base16.getBase16(15);
        for (char c : base16.charSet()) {
            byte b = base16.charDecode(c);
            logger.info("char: {}, byte: {}", c, b);
            assertEquals(base16.charEncode(b), c);
        }
    }

    @Test
    public void testBase32() {
        Base32 base32 = Base32.getBase32(12);
        String hash = base32.encode(LAT, LNG);
        logger.info(hash);
        assertEquals(hash, BASE32_HASH);
        String bitStr = GeoHash.toBitString(base32.toBits(LAT, LNG), base32.getBitsLength());
        logger.info(bitStr);
        assertEquals(bitStr, BIT_STR);
        Position position = base32.decode(hash);
        position.setPrecision(6);
        logger.info(position.toString());
        assert (Math.abs(position.getLat() - LAT)) < Math.pow(10,-1 *  position.getPrecision());
        assert (Math.abs(position.getLng() - LNG)) < Math.pow(10,-1 *  position.getPrecision());
    }

    @Test
    public void testBase32Char() {
        Base32 base32 = Base32.getBase32(12);
        for (char c : base32.charSet()) {
            byte b = base32.charDecode(c);
            logger.info("char: {}, byte: {}", c, b);
            assertEquals(base32.charEncode(b), c);
        }
    }

    @Test
    public void testPositionPrecision() {
        Position position = new Position(LAT, LNG);
        position.setPrecision(6);
        logger.info("lat: {}, lng: {}", position.getLat(), position.getLng());
        assert (Math.abs(position.getLat() - LAT) < Math.pow(10, -1 * position.getPrecision()));
        assert (Math.abs(position.getLng() - LNG) < Math.pow(10, -1 * position.getPrecision()));
    }

    @Test
    public void testNeibor() {
        Position position = new Position(LAT, LNG);
        Base32 base32 = Base32.getBase32(3);
        Neibor neibor = base32.getNeibor(position);
        logger.info("northwest: {}", base32.decode(neibor.getNorthwest()));
        logger.info("north:     {}", base32.decode(neibor.getNorth()));
        logger.info("northeast: {}", base32.decode(neibor.getNortheast()));
        logger.info("west:      {}", base32.decode(neibor.getWest()));
        logger.info("center:    {}", base32.decode(neibor.getCenter()));
        logger.info("east:      {}", base32.decode(neibor.getEast()));
        logger.info("southwest: {}", base32.decode(neibor.getSouthwest()));
        logger.info("south:     {}", base32.decode(neibor.getSouth()));
        logger.info("southeast: {}", base32.decode(neibor.getSoutheast()));
        for (String hash : neibor.toArray()) {
            logger.info(base32.decode(hash).toString());
        }
        logger.info(neibor.toString());
    }

    @Test
    public void testDistince() {
        double[] distinces = GeoHash.charDistance(Base32.CHAR_BIT_LEN);
        for (int i = 0; i < distinces.length; i++) {
            logger.info("{}: {}", i, distinces[i]);
        }
        int base16Len1 = Base16.minimalHashLenByDistance(1.0);
        assertEquals(base16Len1, 15);
        int base32Len1 = Base32.minimalHashLenByDistance(1.0);
        assertEquals(base32Len1, 12);
        logger.info("base16 length by 1.0cm: {}", base16Len1);
        logger.info("base32 length by 1.0cm: {}", base32Len1);
        int base16Len2 = Base16.minimalHashLenByDistance(2.0);
        assertEquals(base16Len2, 14);
        int base32Len2 = Base32.minimalHashLenByDistance(2.0);
        assertEquals(base32Len2, 11);
        logger.info("base16 length by 2.0cm: {}", base16Len2);
        logger.info("base32 length by 2.0cm: {}", base32Len2);
    }
}
