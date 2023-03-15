package com.indytskyi.trafficmanagement.service;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import com.indytskyi.trafficmanagement.dto.StartTripDto;
import com.indytskyi.trafficmanagement.model.Airplane;
import com.indytskyi.trafficmanagement.model.AirplaneCharacteristic;
import com.indytskyi.trafficmanagement.model.TemporaryPoint;
import com.indytskyi.trafficmanagement.model.WayPoint;
import com.indytskyi.trafficmanagement.service.impl.AirplaneService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaneCalculation {

    private final AirplaneService airplaneService;

    private static final double EARTH_RADIUS = 6371000.0; // in meters


    public String startTrip(StartTripDto startTripDto) {

        var airplane = airplaneService.findById(startTripDto.id());


        return "The plane arrived at its destination";
    }


    public void handle(Airplane airplane, List<WayPoint> wayPoints) {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable updatePosition = () -> {
            for (int i = 0; i < wayPoints.size() - 1; i++) {

            }
            airplaneService.save(airplane);
        };

        ScheduledFuture<?> updateHandler = scheduler.scheduleAtFixedRate(updatePosition, 0, 1, TimeUnit.SECONDS);


        while (true) if (wayPoints.isEmpty()) {
            updateHandler.cancel(true);
        }
    }


    public static List<TemporaryPoint> calculateRoute(Airplane airplane, List<WayPoint> wayPoints) {
        List<TemporaryPoint> temporaryPoints;
        for (int i = 0; i < wayPoints.size() - 1; i++) {
            temporaryPoints = turnAirplane(airplane.getAirplaneCharacteristic(),
                    TemporaryPoint.builder().latitude(23.962257).longitude(49.863535).headingDegrees(10.0).build(),
                    calculateCourse(23.962257, 49.863535, 24.005926, 49.846311),
                    800);

            TemporaryPoint lastTemporaryPoint = temporaryPoints.get(temporaryPoints.size() - 1);

            TemporaryPoint temp = TemporaryPoint
                    .builder()
                    .longitude(lastTemporaryPoint.getLongitude())
                    .latitude(lastTemporaryPoint.getLatitude())
                    .build();

            while (isWithinRadius(23.962257,
                    49.86353,
                    24.005926,
                    49.846311,
                    100)) {

                double[] coordinates = airplaneShift(
                        temp.getLatitude(), temp.getLongitude(),
                        0,
                        100,
                        1);

                temp = TemporaryPoint.builder()
                        .longitude(coordinates[0])
                        .latitude(coordinates[1])
                        .altitudeMeters(100.0)
                        .flightSpeedMetersPerSecond(100.0)
                        .headingDegrees(temp.getHeadingDegrees())
                        .build();
            }

            temporaryPoints.forEach(System.out::println);

        }
//        airplane.setAirplaneCharacteristic(AirplaneCharacteristic
//                .builder()
//                        .maxSpeedMetersPerSecond(10.0)
//                        .courseChangeRateDegreesPerSecond(5.0)
//                .build());

//
//        List<TemporaryPoint> temporaryPoints = turnAirplane(airplane.getAirplaneCharacteristic(),
//                TemporaryPoint.builder().latitude(23.962257).longitude(49.863535).headingDegrees(10.0).build(),
//                calculateCourse(23.962257, 49.863535, 24.005926, 49.846311),
//                800);





        return temporaryPoints;
    }

    public static boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radius) {
        double distance = distance(lat1, lon1, lat2, lon2);
        return distance <= radius / 1000; // переводим радиус в км и сравниваем с расстоянием
    }

    public static List<TemporaryPoint> turnAirplane(
            AirplaneCharacteristic characteristic,
            TemporaryPoint startPosition,
            double angle, double altitudeMeters) {

        TemporaryPoint temp = startPosition;
        List<TemporaryPoint> pointList = new ArrayList<>();

        while (temp.getHeadingDegrees() != Math.round(angle)) {

            double headingDegreesTemp = temp.getHeadingDegrees();
            double delta = angle - headingDegreesTemp;
            double changeRateDegreesTemp = characteristic.getCourseChangeRateDegreesPerSecond();

            if (delta > 180 || (delta < 0 && delta > -180)) {
                headingDegreesTemp -= characteristic.getCourseChangeRateDegreesPerSecond();
                changeRateDegreesTemp = Math.abs(changeRateDegreesTemp);

            } else {
                headingDegreesTemp += characteristic.getCourseChangeRateDegreesPerSecond();
                changeRateDegreesTemp = -Math.abs(changeRateDegreesTemp);
            }

            if (headingDegreesTemp > 360) {
                headingDegreesTemp -= 360;
            } else if (temp.getHeadingDegrees() < 0) {
                headingDegreesTemp += 360;
            }

            if (delta < characteristic.getCourseChangeRateDegreesPerSecond()) {
                changeRateDegreesTemp = delta;
            }

            double[] coordinates = airplaneShift(
                    temp.getLatitude(), temp.getLongitude(),
                    changeRateDegreesTemp,
                    characteristic.getMaxSpeedMetersPerSecond(),
                    1);

            temp = TemporaryPoint.builder()
                    .longitude(coordinates[0])
                    .latitude(coordinates[1])
                    .altitudeMeters(altitudeMeters)
                    .flightSpeedMetersPerSecond(characteristic.getMaxSpeedMetersPerSecond())
                    .headingDegrees(headingDegreesTemp)
                    .build();

            pointList.add(temp);
        }

        return pointList;
    }



    public static double[] airplaneShift(double latitude, double longitude, double angle, double speed, double time) {


        // Переводим угол из градусов в радианы
        double angleRad = Math.toRadians(angle);

        // Вычисляем расстояние, которое прошла точка за заданное время со скоростью speed
        double distance = speed * time;

        // Вычисляем новые координаты точки
        double newLatitude = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(latitude))
                * Math.cos(distance / 6371) + Math.cos(Math.toRadians(latitude))
                * Math.sin(distance / 6371) * Math.cos(angleRad)));
        double newLongitude = longitude + Math.toDegrees(Math.atan2(Math.sin(angleRad)
                        * Math.sin(distance / 6371) * Math.cos(Math.toRadians(latitude)),
                Math.cos(distance / 6371) - Math.sin(Math.toRadians(latitude)) * Math.sin(Math.toRadians(newLatitude))));

        return new double[]{newLongitude, newLatitude};
    }

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

        return (arc * EARTH_RADIUS);
    }

    public static double calculateCourse(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double course = Math.toDegrees(Math.atan2(
                Math.sin(Math.toRadians(lon2 - lon1)) * Math.cos(Math.toRadians(lat2)),
                Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) -
                        Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                                Math.cos(Math.toRadians(lon2 - lon1))
        ));

        return (course + 360) % 360;
    }


}
