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
 * A class that calculates all calculations when flying an airplane
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

        previousLongitude = toRadians(previousLongitude);
        currentLongitude = toRadians(currentLongitude);
        previousLatitude = toRadians(previousLatitude);
        currentLatitude = toRadians(currentLatitude);

        double subtractingLongitude = currentLongitude - previousLongitude;
        double subtractingLatitude = currentLatitude - previousLatitude;
        double intermediateCalculation = pow(sin(subtractingLatitude / 2), 2)
                + cos(previousLatitude) * cos(currentLatitude)
                * pow(sin(subtractingLongitude / 2), 2);

        double arc = 2 * asin(sqrt(intermediateCalculation));

        return (arc * RADIUS_EARTH);
    }


    /**
     * A method that checks if an airplane is within the radius of a WayPoint
     */
    public static boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radius) {
        double distance = distance(lat1, lon1, lat2, lon2);
        return distance <= radius / 1000;
    }

    /**
     * A method that calculate new position of an airplane after turning
     */
    public static double[] airplaneShift(double latitude, double longitude, double angle, double speed, double time) {

        double angleRad = Math.toRadians(angle);
        double distance = speed * time;

        double newLatitude = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(latitude))
                * Math.cos(distance / 6371) + Math.cos(Math.toRadians(latitude))
                * Math.sin(distance / 6371) * Math.cos(angleRad)));
        double newLongitude = longitude + Math.toDegrees(Math.atan2(Math.sin(angleRad)
                        * Math.sin(distance / 6371) * Math.cos(Math.toRadians(latitude)),
                Math.cos(distance / 6371) - Math.sin(Math.toRadians(latitude))
                        * Math.sin(Math.toRadians(newLatitude))));

        return new double[]{newLongitude, newLatitude};
    }


    /**
     * A method calculate course between two coordinate
     */
    public static double calculateCourse(double lat1, double lon1, double lat2, double lon2) {
        double dLon = lon2 - lon1;
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double bearing = Math.atan2(y, x);
        double degrees = Math.toDegrees(bearing);

        return (degrees + 360) % 360;
    }


}