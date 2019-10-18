package org.taiji.geo.tool.geohash;

/**
 * 中心geohash编码块最邻近8个geohash编码块
 *
 * @author tim
 */
public class Neibor {
    private String center;
    private String west;
    private String east;
    private String north;
    private String south;
    private String northwest;
    private String northeast;
    private String southwest;
    private String southeast;

    public Neibor() {
    }

    public Neibor(String center) {
        this.center = center;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getWest() {
        return west;
    }

    public void setWest(String west) {
        this.west = west;
    }

    public String getEast() {
        return east;
    }

    public void setEast(String east) {
        this.east = east;
    }

    public String getNorth() {
        return north;
    }

    public void setNorth(String north) {
        this.north = north;
    }

    public String getSouth() {
        return south;
    }

    public void setSouth(String south) {
        this.south = south;
    }

    public String getNorthwest() {
        return northwest;
    }

    public void setNorthwest(String northwest) {
        this.northwest = northwest;
    }

    public String getNortheast() {
        return northeast;
    }

    public void setNortheast(String northeast) {
        this.northeast = northeast;
    }

    public String getSouthwest() {
        return southwest;
    }

    public void setSouthwest(String southwest) {
        this.southwest = southwest;
    }

    public String getSoutheast() {
        return southeast;
    }

    public void setSoutheast(String southeast) {
        this.southeast = southeast;
    }

    public String[] toArray() {
        return new String[]{
            getNorthwest(),
            getNorth(),
            getNortheast(),
            getWest(),
            getCenter(),
            getEast(),
            getSouthwest(),
            getSoutheast(),
        };
    }

    @Override
    public String toString() {
        return "{northwest:" + getNorthwest() + ",north:" + getNorth() + ",northeast:" + getNortheast() + ",west:" + getWest() + ",center:" + getCenter() + ",east:" + getEast() + ",southwest:" + getSouthwest() + ",south:" + getSouth() + ",southeast:" + getSoutheast();
    }
}
