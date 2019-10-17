package org.taiji.geo.tool.geohash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.Math.abs;

/**
 * DMS坐标类, Degrees Minutes decimal-seconds (D M s) format
 *
 * @version V1.0
 * @date 2018/8/8 2:40 PM
 */
@SuppressWarnings("unused")
public class DMSCoordinate implements Serializable {
    public final double wholeDegrees, minutes, seconds;

    DMSCoordinate(double wholeDegrees, double minutes, double seconds) {
        this.wholeDegrees = wholeDegrees;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public DMSCoordinate(double degrees) {
        this.wholeDegrees = (int) degrees;
        double remaining = abs(degrees - wholeDegrees);
        this.minutes = (int) (remaining * 60);
        remaining = remaining * 60 - minutes;
        this.seconds = new BigDecimal(remaining * 60).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    public double getWholeDegrees() {
        return wholeDegrees;
    }

    public double getMinutes() {
        return minutes;
    }

    public double getSeconds() {
        return seconds;
    }

    public double degrees() {
        double decimalDegrees = abs(wholeDegrees) + minutes / 60 + seconds / 3600;

        if (wholeDegrees < 0) {
            decimalDegrees = -decimalDegrees;
        }
        return decimalDegrees;
    }

    @Override
    public String toString() {
        return String.format("%s°%s′%s″", wholeDegrees, minutes, seconds);
    }
}
