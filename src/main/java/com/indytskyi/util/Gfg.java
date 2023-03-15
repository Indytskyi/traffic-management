package com.indytskyi.util;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Class where you calculate distance between two coordinates
 */
@UtilityClass
@Slf4j
public class Gfg {

    private static final int RADIUS_EARTH = 6371;

    /**
     * method where you calculate distance between two coordinates
     */
    public static double distance
    (
            double previousLatitude,
            double previousLongitude,
            double currentLatitude,
            double currentLongitude
    ) {

        log.info("calculate distance between two coordinates");

//        previousLongitude = toRadians(previousLongitude);
//        currentLongitude = toRadians(currentLongitude);
//        previousLatitude = toRadians(previousLatitude);
//        currentLatitude = toRadians(currentLatitude);
//
//        double subtractingLongitude = currentLongitude - previousLongitude;
//        double subtractingLatitude = currentLatitude - previousLatitude;
//        double intermediateCalculation = pow(sin(subtractingLatitude / 2), 2)
//                + cos(previousLatitude) * cos(currentLatitude)
//                * pow(sin(subtractingLongitude / 2), 2);
//
//        double arc = 2 * asin(sqrt(intermediateCalculation));
//
//        return (arc * RADIUS_EARTH);
        return 0;
    }


    /**
     * method where you find angle between two coordinates
     */
    public static double findAngle
    (
            double previousLatitude,
            double previousLongitude,
            double currentLatitude,
            double currentLongitude
    ) {
        double theta = previousLongitude - currentLongitude;
        double dist = Math.sin(Math.toRadians(previousLatitude)) * Math.sin(Math.toRadians(currentLatitude)) +
                Math.cos(Math.toRadians(previousLatitude)) * Math.cos(Math.toRadians(currentLatitude)) *
                        Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }



}