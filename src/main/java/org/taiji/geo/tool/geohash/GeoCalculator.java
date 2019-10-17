package org.taiji.geo.tool.geohash;

import static java.lang.Math.*;

/**
 * geo计算器
 *
 * @author tim
 * @version V1.0
 * @date 2018/8/8 3:38 PM
 */
@SuppressWarnings("unused")
public class GeoCalculator {
    private static final double EARTH_DIAMETER = 6370.99681 * 1000; //meters

    /**
     * 大圆上两点之间的中点
     *
     * @param standPosition standPosition
     * @param forePosition  standPosition
     * @return mid point
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong.html"></a>
     */
    public static Position midPosition(Position standPosition, Position forePosition) {
        double λ1 = toRadians(standPosition.getLng());
        double λ2 = toRadians(forePosition.getLng());

        double φ1 = toRadians(standPosition.getLat());
        double φ2 = toRadians(forePosition.getLat());

        double Bx = cos(φ2) * cos(λ2 - λ1);
        double By = cos(φ2) * sin(λ2 - λ1);

        double φ3 = atan2(sin(φ1) + sin(φ2), sqrt((cos(φ1) + Bx) * (cos(φ1) + Bx) + By * By));
        double λ3 = λ1 + atan2(By, cos(φ1) + Bx);

        return new Position(Math.toDegrees(φ3), Math.toDegrees(λ3));
    }

    /**
     * 大圆距离,harvesine公式求算距离(半正矢解法)
     * {@link #harvesineDistance(Position, Position) 默认采用harvesine算法计算距离}
     *
     * @param standPosition The stand point
     * @param forePosition  The fore point
     * @return The distance, in meters
     */
    public static double distance(Position standPosition, Position forePosition) {
        return harvesineDistance(standPosition, forePosition);
    }

    /**
     * 大圆距离,球面余弦解法
     * 球面上任意两点之间的距离计算公式(计算公式,假定地球为一个球体,采用球面三角学-余弦定理推导球面两点距离)
     * 当求算两点比较接近时(如1km),导致公式内:cos(d/R)=0.99999999,会引起较大误差(舍入误差)
     * Great-circle distance
     *
     * @param standPosition The stand point
     * @param forePosition  The fore point
     * @return The distance, in meters
     */
    public static double gcdDistance(Position standPosition, Position forePosition) {

        double diffLongitudes = toRadians(abs(forePosition.getLng() - standPosition.getLng()));
        double slat = toRadians(standPosition.getLat());
        double flat = toRadians(forePosition.getLat());

        //spherical law of cosines

        double sphereCos = (sin(slat) * sin(flat)) + (cos(slat) * cos(flat) * cos(diffLongitudes));
        double c = acos(max(min(sphereCos, 1d), -1d));

        return EARTH_DIAMETER * c;
    }

    /**
     * 大圆距离,harvesine公式求算距离(半正矢解法)
     * 球面上任意两点之间的距离计算公式(计算公式,假定地球为一个球体,采用球面三角学-正弦定理推导球面两点距离)
     * harvesine公式,在求算对映点距离会产生较大的误差(舍入误差)
     *
     * @param standPosition The stand point
     * @param forePosition  The fore point
     * @return The distance, in meters
     */
    public static double harvesineDistance(Position standPosition, Position forePosition) {

        double diffLongitudes = toRadians(abs(forePosition.getLng() - standPosition.getLng()));
        double slat = toRadians(standPosition.getLat());
        double flat = toRadians(forePosition.getLat());

        // haversine formula
        double diffLatitudes = toRadians(abs(forePosition.getLat() - standPosition.getLat()));
        double a = sin(diffLatitudes / 2) * sin(diffLatitudes / 2) + cos(slat) * cos(flat) * sin(diffLongitudes / 2) * sin(diffLongitudes / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a)); //angular distance in radians

        return EARTH_DIAMETER * c;
    }

    /**
     * Vincenty算法,椭球体两点距离(假定地球为椭球体)
     * 通过迭代方式计算地理距离
     * 精度高,计算效率低
     *
     * @param standPosition The stand point
     * @param forePosition  The fore point
     * @return Vincenty object which holds all 3 values
     */
    private static Vincenty vincenty(Position standPosition, Position forePosition) {
        double λ1 = toRadians(standPosition.getLng());
        double λ2 = toRadians(forePosition.getLng());

        double φ1 = toRadians(standPosition.getLat());
        double φ2 = toRadians(forePosition.getLat());

        double a = 6_378_137;
        double b = 6_356_752.314245;
        double f = 1 / 298.257223563;

        double L = λ2 - λ1;
        double tanU1 = (1 - f) * tan(φ1), cosU1 = 1 / sqrt((1 + tanU1 * tanU1)), sinU1 = tanU1 * cosU1;
        double tanU2 = (1 - f) * tan(φ2), cosU2 = 1 / sqrt((1 + tanU2 * tanU2)), sinU2 = tanU2 * cosU2;

        double λ = L, λʹ, iterationLimit = 100, cosSqα, σ, cos2σM, cosσ, sinσ, sinλ, cosλ;
        do {
            sinλ = sin(λ);
            cosλ = cos(λ);
            double sinSqσ = (cosU2 * sinλ) * (cosU2 * sinλ) + (cosU1 * sinU2 - sinU1 * cosU2 * cosλ) * (cosU1 * sinU2 - sinU1 * cosU2 * cosλ);
            sinσ = sqrt(sinSqσ);
            if (sinσ == 0) {
                return new Vincenty(0, 0, 0);  // co-incident points
            }
            cosσ = sinU1 * sinU2 + cosU1 * cosU2 * cosλ;
            σ = atan2(sinσ, cosσ);
            double sinα = cosU1 * cosU2 * sinλ / sinσ;
            cosSqα = 1 - sinα * sinα;
            cos2σM = cosσ - 2 * sinU1 * sinU2 / cosSqα;

            if (Double.isNaN(cos2σM)) {
                cos2σM = 0;  // equatorial line: cosSqα=0 (§6)
            }
            double C = f / 16 * cosSqα * (4 + f * (4 - 3 * cosSqα));
            λʹ = λ;
            λ = L + (1 - C) * f * sinα * (σ + C * sinσ * (cos2σM + C * cosσ * (-1 + 2 * cos2σM * cos2σM)));
        } while (abs(λ - λʹ) > 1e-12 && --iterationLimit > 0);

        if (iterationLimit == 0) {
            throw new IllegalStateException("Formula failed to converge");
        }

        double uSq = cosSqα * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double Δσ = B * sinσ * (cos2σM + B / 4 * (cosσ * (-1 + 2 * cos2σM * cos2σM) -
            B / 6 * cos2σM * (-3 + 4 * sinσ * sinσ) * (-3 + 4 * cos2σM * cos2σM)));

        double distance = b * A * (σ - Δσ);

        double initialBearing = atan2(cosU2 * sinλ, cosU1 * sinU2 - sinU1 * cosU2 * cosλ);
        initialBearing = (initialBearing + 2 * PI) % (2 * PI); //turning value to trigonometric direction

        double finalBearing = atan2(cosU1 * sinλ, -sinU1 * cosU2 + cosU1 * sinU2 * cosλ);
        finalBearing = (finalBearing + 2 * PI) % (2 * PI);  //turning value to trigonometric direction

        return new Vincenty(distance, toDegrees(initialBearing), toDegrees(finalBearing));
    }

    /**
     * Vincenty算法,椭球体两点距离(假定地球为椭球体)
     * 通过迭代方式计算地理距离
     * 精度高,计算效率低
     *
     * @param standPosition 起点
     * @param forePosition  终点
     * @return Vincenty距离
     */
    public static double vincentyDistance(Position standPosition, Position forePosition) {
        return vincenty(standPosition, forePosition).distance;
    }

    /**
     * Returns (azimuth) bearing at Vincenty formula.
     *
     * @param standPosition The stand point
     * @param forePosition  The fore point
     * @return (azimuth) bearing in degrees to the North
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong.html"></a>
     */
    public static double vincentyBearing(Position standPosition, Position forePosition) {
        return vincenty(standPosition, forePosition).initialBearing;
    }

    /**
     * Returns final bearing in direction of standPosition→forePosition at Vincenty formula.
     *
     * @param standPosition The stand point
     * @param forePosition  The fore point
     * @return (azimuth) bearing in degrees to the North
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong.html"></a>
     */
    public static double vincentyFinalBearing(Position standPosition, Position forePosition) {
        return vincenty(standPosition, forePosition).finalBearing;
    }

    /**
     * Returns the coordinates of a point which is "distance" away
     * from standPosition in the direction of "bearing"
     * <p>
     * Note: North is equal to 0 for bearing value
     *
     * @param standPosition Origin
     * @param bearing       Direction in degrees, clockwise from north
     * @param distance      distance in meters
     * @return forePosition coordinates
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong.html"></a>
     */
    public static Position pointAt(Position standPosition, double bearing, double distance) {
        /**
         φ2 = asin( sin φ1 ⋅ cos δ + cos φ1 ⋅ sin δ ⋅ cos θ )
         λ2 = λ1 + atan2( sin θ ⋅ sin δ ⋅ cos φ1, cos δ − sin φ1 ⋅ sin φ2 )

         where
         φ is latitude,
         λ is longitude,
         θ is the bearing (clockwise from north),
         δ is the angular distance d/R;
         d being the distance travelled, R the earth’s radius
         */

        double φ1 = toRadians(standPosition.getLat());
        double λ1 = toRadians(standPosition.getLng());
        double θ = toRadians(bearing);
        double δ = distance / EARTH_DIAMETER; // normalize linear distance to radian angle

        double φ2 = asin(sin(φ1) * cos(δ) + cos(φ1) * sin(δ) * cos(θ));
        double λ2 = λ1 + atan2(sin(θ) * sin(δ) * cos(φ1), cos(δ) - sin(φ1) * sin(φ2));

        double λ2_harmonised = (λ2 + 3 * PI) % (2 * PI) - PI; // normalise to −180..+180°

        return new Position(Math.toDegrees(φ2), Math.toDegrees(λ2_harmonised));
    }

    /**
     * Returns the (azimuth) bearing, in decimal degrees, from standPosition to forePosition
     *
     * @param standPosition Origin point
     * @param forePosition  Destination point
     * @return (azimuth) bearing, in decimal degrees
     */
    public static double bearing(Position standPosition, Position forePosition) {
        /**
         * Formula: θ = atan2( 	sin(Δlong).cos(lat2), cos(lat1).sin(lat2) − sin(lat1).cos(lat2).cos(Δlong) )
         */

        double y = sin(toRadians(forePosition.getLng() - standPosition.getLng())) * cos(toRadians(forePosition.getLat()));
        double x = cos(toRadians(standPosition.getLat())) * sin(toRadians(forePosition.getLat()))
            - sin(toRadians(standPosition.getLat())) * cos(toRadians(forePosition.getLat())) * cos(toRadians(forePosition.getLng() - standPosition.getLng()));

        double bearing = (atan2(y, x) + 2 * PI) % (2 * PI);

        return toDegrees(bearing);
    }

    private static class Vincenty {
        /**
         * distance is the distance in meter
         * initialBearing is the initial bearing, or forward azimuth (in reference to North point), in degrees
         * finalBearing is the final bearing (in direction p1→p2), in degrees
         */
        final double distance, initialBearing, finalBearing;

        public Vincenty(double distance, double initialBearing, double finalBearing) {
            this.distance = distance;
            this.initialBearing = initialBearing;
            this.finalBearing = finalBearing;
        }
    }
}
