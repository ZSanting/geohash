package org.taiji.geo.tool.geohash;

public class Position {
    private double lat;
    private double lng;

    private int precision = 0;

    public Position(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        if (precision > 0) {
            return format(lat, precision);
        }
        return lat;
    }

    public double getLng() {
        if (precision > 0) {
            return format(lng, precision);
        }
        return lng;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    @Override
    public String toString() {
        return "(" + getLat() + ", " + getLng() + ")";
    }

    public static double format(double val, int precision) {
        long l = (int) Math.round(val * Math.pow(10, precision));
        return l / Math.pow(10, precision);
    }
}
