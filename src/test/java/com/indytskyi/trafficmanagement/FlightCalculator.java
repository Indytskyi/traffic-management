package com.indytskyi.trafficmanagement;

import java.util.ArrayList;
import java.util.List;

public class FlightCalculator {
    
    // Earth radius in meters
    private static final double EARTH_RADIUS = 6371000;
    
    public static List<Point> calculateFlight(Point start, Point end, double initialHeading,
                                               double speed, double rotationAngle, int timeInterval) {
        List<Point> points = new ArrayList<>();
        
        double distance = getDistance(start, end);
        double bearing = getBearing(start, end);
        
        double time = 0;
        Point current = start;
        double heading = initialHeading;
        while (time < distance / speed) {
            double distanceTraveled = speed * time;
            double angularDistance = distanceTraveled / EARTH_RADIUS;
            
            double newLat = Math.asin(Math.sin(current.getLat()) * Math.cos(angularDistance) +
                                       Math.cos(current.getLat()) * Math.sin(angularDistance) * Math.cos(heading));
            double newLon = current.getLon() + Math.atan2(Math.sin(heading) * Math.sin(angularDistance) * Math.cos(current.getLat()),
                                                          Math.cos(angularDistance) - Math.sin(current.getLat()) * Math.sin(newLat));
            Point newPoint = new Point(newLat, newLon);
            points.add(newPoint);
            
            heading += Math.toRadians(rotationAngle) * timeInterval;
            bearing += Math.toRadians(rotationAngle) * timeInterval;
            current = newPoint;
            time += timeInterval;
        }
        
        return points;
    }
    
    private static double getDistance(Point p1, Point p2) {
        double dLat = Math.toRadians(p2.getLat() - p1.getLat());
        double dLon = Math.toRadians(p2.getLon() - p1.getLon());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(p1.getLat())) * Math.cos(Math.toRadians(p2.getLat())) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
    
    private static double getBearing(Point p1, Point p2) {
        double dLon = Math.toRadians(p2.getLon() - p1.getLon());
        double lat1 = Math.toRadians(p1.getLat());
        double lat2 = Math.toRadians(p2.getLat());
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) -
                   Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double bearing = Math.atan2(y, x);
        return (bearing + 2 * Math.PI) % (2 * Math.PI);
    }
    
    public static class Point {
        private double lat;
        private double lon;
        
        public Point(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
        
        public double getLat() {
            return lat;
        }
        
        public double getLon() {
            return lon;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "lat=" + lat +
                    ", lon=" + lon +
                    '}';
        }
    }
}
