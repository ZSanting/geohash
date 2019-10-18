package org.taiji.geo.tool.geohash;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GeoCalculator Tester.
 *
 * @author tim.zhang
 * @version 1.0
 * @since <pre>Aug 10, 2018</pre>
 */
public class GeoCalculatorTest {
    private static final Logger logger = LoggerFactory.getLogger(GeoHashTest.class);
    Position p1;
    Position p2;
    Position p3;
    Position p4;

    /**
     * @throws Exception 异常
     * @see <a href="https://andrew.hedges.name/experiments/haversine/">Finding distances based on Latitude and Longitude</a>
     */
    @Before
    public void before() throws Exception {
        //p1 -> p2 ,0.513 km
        p1 = new Position(39.863955, 116.373567);
        p2 = new Position(39.866504, 116.378575);
        //p1 -> p2 ,0.626 km
        p3 = new Position(30.293022, 120.109774);
        p4 = new Position(30.296491, 120.10464);
//        p5 = new Position(30.292854, 120.094795);
//        p6 = new Position(30.293147, 120.113417);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: midPosition(Position standPosition, Position forePosition)
     */
    @Test
    public void testMidPosition() throws Exception {
//TODO: Test goes here... 
    }

    @Test
    public void testDistance() throws Exception {
        logger.info(String.format("%s -> %s ,ditance:%s m", p1, p2, 513));
        logger.info(String.format("gcd Distance: %s m", GeoCalculator.gcdDistance(p1, p2)));
        logger.info(String.format("harvesine Distance: %s m", GeoCalculator.harvesineDistance(p1, p2)));
        logger.info(String.format("vincenty Distance: %s m", GeoCalculator.vincentyDistance(p1, p2)));
        logger.info(String.format("\n%s -> %s ,ditance:%s m", p3, p4, 626));
        logger.info(String.format("gcd Distance: %s m", GeoCalculator.gcdDistance(p3, p4)));
        logger.info(String.format("harvesine Distance: %s m", GeoCalculator.harvesineDistance(p3, p4)));
        logger.info(String.format("vincenty Distance: %s m", GeoCalculator.vincentyDistance(p3, p4)));
    }

    @Test
    public void testDistanceSpend() {
        long start = System.currentTimeMillis();
        int loop = 100000;
        for (int i = 0; i < loop; i++) {
            GeoCalculator.gcdDistance(p1, p2);
        }
        logger.info(String.format("gcd distance loop %d, spend %s ms", loop, System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            GeoCalculator.harvesineDistance(p1, p2);
        }
        logger.info(String.format("harvesine distance loop %d, spend %s ms", loop, System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            GeoCalculator.vincentyDistance(p1, p2);
        }
        logger.info(String.format("vincenty distance loop %d, spend %s ms", loop, System.currentTimeMillis() - start));
    }
}
