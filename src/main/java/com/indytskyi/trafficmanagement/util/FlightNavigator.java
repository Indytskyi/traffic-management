package com.indytskyi.trafficmanagement.util;

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
public class FlightNavigator {

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
    public static double[] airplaneShift(double latitude, double longitude, double angle, double speed) {

        // Convert speed from meters per second to meters per millisecond
        double speedPerMs = speed / 1000;
        // Calculate the distance the airplane travels in one second based on its speed
        double distanceTraveled = speedPerMs * 1000;

        // Convert turn angle from degrees to radians
        double turnAngleRadians = Math.toRadians(angle);

        // Calculate the change in latitude and longitude based on the distance traveled and turn angle
        double deltaLatitude = distanceTraveled * Math.cos(turnAngleRadians);
        double deltaLongitude = distanceTraveled * Math.sin(turnAngleRadians);

        // Calculate the new latitude and longitude based on the current location and the change in latitude and longitude
        double newLatitude = latitude + (deltaLatitude / 111111); // 111111 meters in one degree of latitude
        double newLongitude = longitude + (deltaLongitude / (111111 * Math.cos(Math.toRadians(latitude)))); // Adjust for longitude's distance due to latitude

        // Return the new latitude and longitude as an array
        return new double[] {newLatitude, newLongitude};
    }


    public static double[] calculateAirplanePosition(double speed, double course, double latitude, double longitude) {
        double earthRadius = RADIUS_EARTH * 1000; // in meters
        double distance = speed; // distance traveled in 1 second
        double courseRadians = Math.toRadians(course); // convert course to radians
        double latRadians = Math.toRadians(latitude); // convert latitude to radians
        double longRadians = Math.toRadians(longitude); // convert longitude to radians

        double newLatRadians = Math.asin(Math.sin(latRadians) * Math.cos(distance / earthRadius)
                + Math.cos(latRadians) * Math.sin(distance / earthRadius) * Math.cos(courseRadians));
        double newLongRadians = longRadians + Math.atan2(Math.sin(courseRadians) * Math.sin(distance / earthRadius) * Math.cos(latRadians),
                Math.cos(distance / earthRadius) - Math.sin(latRadians) * Math.sin(newLatRadians));

        double newLat = Math.toDegrees(newLatRadians); // convert back to degrees
        double newLong = Math.toDegrees(newLongRadians); // convert back to degrees

        double[] newPosition = { newLat, newLong };
        return newPosition;
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