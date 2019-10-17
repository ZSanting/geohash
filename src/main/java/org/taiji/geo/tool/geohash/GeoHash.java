package org.taiji.geo.tool.geohash;

import java.security.InvalidParameterException;

/**
 * geohash编码
 *
 * @author tim
 * @author laifeng.luo
 */
public abstract class GeoHash {
    public static final int MAX_LAT = 90;   // 最大纬度
    public static final int MIN_LAT = -90;  // 最小经度
    public static final int MAX_LNG = 180;  // 最大经度
    public static final int MIN_LNG = -180; // 最小经度

    private static final int MAX_BITS = 64; // 比特串的最大长度

    /**
     * 最终的编码长度
     */
    private final int length;

    /**
     * 单个字符的编码比特位数
     */
    private final int charLen;

    /**
     * 纬度二分查找次数
     */
    private final int latSearchTimes;

    /**
     * 经度二分查找次数
     */
    private final int lngSearchTimes;

    GeoHash(int length, int charLen) {
        int times = length * charLen;
        if (times <= 0 || times > MAX_BITS) {
            throw new InvalidParameterException("length * charLen expected to be in (0, " + MAX_BITS + "], " + times + " found.");
        }
        this.length = length;
        this.charLen = charLen;
        if (times % 2 == 0) {
            this.lngSearchTimes = times / 2;
        } else {
            this.lngSearchTimes = times / 2 + 1;
        }
        this.latSearchTimes = times / 2;
    }

    /**
     * 字符编码
     * 每个字符用一个字节表示
     *
     * @param b 输入字节
     * @return char
     */
    abstract protected char charEncode(byte b);

    /**
     * 字符解码
     * 每个字符用一个字节表示
     *
     * @param c 输入字符
     * @return byte
     */
    abstract protected byte charDecode(char c);

    /**
     * 编码字符集
     *
     * @return char[]
     */
    abstract public char[] charSet();

    /**
     * @param bits 输入的长比特串
     * @return byte[]
     * @see #charLen
     * 将一个长比特串按charLen切分比特串的数组
     * 长比特串用long表示
     * 每个短比特串用byte表示
     */
    private byte[] split(long bits) {
        byte[] bytes = new byte[length];
        int prefix = MAX_BITS - length * charLen;
        int suffix = MAX_BITS - charLen;
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) ((bits << (prefix + i * charLen)) >>> suffix);
        }
        return bytes;
    }

    /**
     * 对输入的比特串进行编码
     * 比特串用长整型表示
     *
     * @param bits 比特串
     * @return String
     */
    private String hash(long bits) {
        byte[] bytes = split(bits);
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(charEncode(b));
        }
        return builder.toString();
    }

    /**
     * 将两个比特串按奇偶错位组合起来
     * 位数从0开始计算
     *
     * @param even 放在偶数位的比特串, 默认为经度, 以整型表示
     * @param odd  放在奇数位的比特串, 默认为纬度, 以整型表示
     * @return long
     */
    final protected long compact(int even, int odd) {
        long eL = (long) even;
        long oL = (long) odd;
        long e = 0;
        long o = 0;
        for (int i = 0; i < 32; i++) {
            e |= (eL << (i + 32) >>> 63) << (63 - i * 2);
            o |= (oL << (i + 32) >>> 63) << (62 - i * 2);
        }
        if (lngSearchTimes > latSearchTimes) {
            e >>= 1;
            o <<= 1;
        }
        return e | o;
    }

    /**
     * 将geohash编码按奇偶分组分成两个比特串, 都用整型表示
     *
     * @param hash 输入的geohash编码
     * @return int[] 第0位表示纬度, 第1位表示经度
     */
    final protected int[] separate(String hash) {
        int latBits = 0;
        int lngBits = 0;
        char[] chars = hash.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            byte b = (byte) ((charDecode(chars[i]) << (8 - charLen)) >> (8 - charLen));
            for (int j = 0; j < 8; j++) {
                int bit = b << (j + 24) >>> 31;
                if (bit == 0) {
                    continue;
                }
                int k = i * charLen + j - (8 - charLen);
                if (k % 2 == 0) {
                    lngBits |= 1 << lngSearchTimes - k / 2 - 1;
                } else {
                    latBits |= 1 << latSearchTimes - (k - 1) / 2 - 1;
                }
            }
        }
        return new int[]{latBits, lngBits};
    }

    /**
     * 把经纬度转换成比特串
     *
     * @param lat 纬度
     * @param lng 经度
     * @return long 比特串
     */
    final public long toBits(double lat, double lng) {
        return toBits(new Position(lat, lng));
    }

    /**
     * 把经纬度转换成比特串
     *
     * @param position 位置
     * @return long 比特串
     */
    final public long toBits(Position position) {
        int latBits = binarySearch(MIN_LAT, MAX_LAT, position.getLat(), latSearchTimes);
        int lngBits = binarySearch(MIN_LNG, MAX_LNG, position.getLng(), lngSearchTimes);
        return compact(lngBits, latBits);
    }

    /**
     * 对经纬度Hash编码
     *
     * @param lat 纬度
     * @param lng 经度
     * @return String hash串
     */
    final public String encode(double lat, double lng) {
        return encode(new Position(lat, lng));
    }

    /**
     * 对位置进行Hash编码
     *
     * @param position 位置
     * @return String hash串
     */
    final public String encode(Position position) {
        int latBits = binarySearch(MIN_LAT, MAX_LAT, position.getLat(), latSearchTimes);
        int lngBits = binarySearch(MIN_LNG, MAX_LNG, position.getLng(), lngSearchTimes);
        return hash(compact(lngBits, latBits));
    }

    /**
     * 对hash串进行解码
     *
     * @param hash hash串
     * @return Position 位置
     */
    final public Position decode(String hash) {
        int[] ps = separate(hash);
        double lat = binarySearch(MIN_LAT, MAX_LAT, ps[0], latSearchTimes);
        double lng = binarySearch(MIN_LNG, MAX_LNG, ps[1], lngSearchTimes);
        return new Position(lat, lng);
    }

    /**
     * 获取某个位置的周围的八个方块
     *
     * @param lat 纬度
     * @param lng 经度
     * @return {@link Neibor}
     */
    final public Neibor getNeibor(double lat, double lng) {
        return getNeibor(new Position(lat, lng));
    }

    /**
     * 获取某个位置的周围的八个方块
     *
     * @param position 输入位置
     * @return {@link Neibor}
     */
    public Neibor getNeibor(Position position) {
        return getNeibor(encode(position));
    }

    public Neibor getNeibor(String hash) {
        int[] ints = separate(hash);
        int latBits = ints[0];
        if (latBits < 0 && latBits > (1 << latSearchTimes)) {
            throw new RuntimeException("lat: " + latBits + " is out of range");
        }
        int lngBits = ints[1];
        if (lngBits < 0 && lngBits < (1 << lngSearchTimes)) {
            throw new RuntimeException("lng: " + lngBits + " is out of range");
        }
        Neibor neibor = new Neibor(hash);
        int south = latBits > 0 ? latBits - 1 : -1;
        int north = latBits < (1 << latSearchTimes) ? latBits + 1 : -1;
        int west = lngBits > 0 ? lngBits - 1 : -1;
        int east = lngBits < (1 << lngSearchTimes) ? lngBits + 1 : -1;
        if (south >= 0) {
            neibor.setSouth(hash(compact(lngBits, south)));
            if (west >= 0) {
                neibor.setSouthwest(hash(compact(west, south)));
            }
            if (east >= 0) {
                neibor.setSoutheast(hash(compact(east, south)));
            }
        }
        if (north >= 0) {
            neibor.setNorth(hash(compact(lngBits, north)));
            if (west >= 0) {
                neibor.setNorthwest(hash(compact(west, north)));
            }
            if (east >= 0) {
                neibor.setNortheast(hash(compact(east, north)));
            }
        }
        if (west >= 0) {
            neibor.setWest(hash(compact(west, latBits)));
        }
        if (east >= 0) {
            neibor.setEast(hash(compact(east, latBits)));
        }
        return neibor;
    }

    public int getBitsLength() {
        return length * charLen;
    }

    /**
     * 精度对照映射表
     * 单位: cm
     *
     * @return double[]
     */
    public static double[] bitsDistance() {
        return new double[]{
            2001508700, // 0 bit    20015.087 km
            1000754300, // 2 bit    10007.543 km
            500377200,  // 4 bit    5003.772 km
            250188600,  // 6 bit    2501.886 km
            125094300,  // 8 bit    1250.943 km
            62547100,   // 10 bit   625.471 km
            31273600,   // 12 bit   312.736 km
            15636800,   // 14 bit   156.368 km
            7818400,    // 16 bit   78.184 km
            3909200,    // 18 bit   39.092 km
            1954600,    // 20 bit   19.546 km
            9772992,    // 22 bit   9772.992 m
            4886496,    // 24 bit   4886.496 m
            2443248,    // 26 bit   2443.248 m
            1221624,    // 28 bit   1221.624 m
            610812,     // 30 bit   610.812 m
            305406,     // 32 bit   305.406 m
            152703,     // 34 bit   152.703 m
            76351,      // 36 bit   76.351 m
            38176,      // 38 bit   38.176 m
            19088,      // 40 bit   19.088 m
            954.394,    // 42 bit   954.394 cm
            477.197,    // 44 bit   477.197 cm
            238.598,    // 46 bit   238.598 cm
            119.299,    // 48 bit   119.299 cm
            59.650,     // 50 bit   59.650 cm
            29.825,     // 52 bit   29.825 cm
            14.912,     // 54 bit   14.912 cm
            7.456,      // 56 bit   7.456 cm
            3.728,      // 58 bit   3.728 cm
            1.864,      // 60 bit   1.864 cm
            0.932,      // 62 bit   0.932 cm
            0.466       // 64 bit   0.466 cm
        };
    }

    public static double[] charDistance(int length) {
        double[] distinces = bitsDistance();
        double[] res = new double[(distinces.length - 1) * 2 / length + 1];
        for (int i = 0; i < distinces.length; i++) {
            if (i * 2 % length == 0) {
                res[i * 2 / length] = distinces[i];
            }
        }
        return res;
    }

    protected static int minimalBitLenByDistance(double distance) {
        double[] distances = bitsDistance();
        if (distance >= distances[0]) {
            throw new InvalidParameterException(distance + " is too large.");
        }
        for (int i = 1; i < distances.length; i++) {
            if (distances[i] < distance) {
                return (i - 1) * 2;
            }
        }
        return (distances.length - 1) * 2;
    }

    public static String toBitString(long bits, int length) {
        if (length > 64 || length <= 0) {
            throw new InvalidParameterException("length is expected to in (0, 64], " + length + " found.");
        }
        if (length < 64) {
            bits &= (1L << length) - 1; // 抹去高位多余数值
        }
        int prefix = MAX_BITS - length;
        int suffix = MAX_BITS - 1;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(bits << (i + prefix) >>> suffix);
        }
        return builder.toString();
    }

    static int binarySearch(double begin, double end, double pos, int times) {
        if (times <= 0 || times > MAX_BITS / 2) {
            throw new InvalidParameterException("length * charLen expected to be in (0, " + MAX_BITS / 2 + "], " + times + " found.");
        }
        int bits = 0;
        for (int i = 0; i < times; i++) {
            double mid = (begin + end) / 2;
            if (pos == begin) {
                break;
            } else if (pos > begin && pos < mid) {
                end = mid;
            } else {
                begin = mid;
                bits |= (1 << (times - i - 1));
            }
        }
        return bits;
    }

    static double binarySearch(double begin, double end, int pos, int times) {
        if (times <= 0 || times > MAX_BITS / 2) {
            throw new InvalidParameterException("length * charLen expected to be in (0, " + MAX_BITS / 2 + "], " + times + " found.");
        }
        if (times < 32) {
            pos &= (1 << times) - 1; // 抹去高位多余数值
        }
        int prefix = 32 - times;
        for (int i = 0; i < times; i++) {
            double mid = (begin + end) / 2;
            int bit = (pos << (prefix + i)) >>> 31;
            if (bit == 0) {
                end = mid;
            } else {
                begin = mid;
            }
        }
        return (begin + end) / 2;
    }
}
