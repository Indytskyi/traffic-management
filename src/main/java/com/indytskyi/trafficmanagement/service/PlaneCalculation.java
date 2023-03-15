package com.indytskyi.trafficmanagement.service;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import com.indytskyi.dto.StartTripDto;
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

        handle(airplane, startTripDto.wayPoints());

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


    private List<TemporaryPoint> calculateRoute(AirplaneCharacteristic characteristic, List<WayPoint> wayPoints) {


        return null;
    }

    private List<TemporaryPoint> turnAirplane(
            AirplaneCharacteristic characteristic,
            TemporaryPoint startPosition,
            double angle) {
        TemporaryPoint temp = startPosition;
        List<TemporaryPoint> pointList = new ArrayList<>();


        while (temp.getHeadingDegrees() != angle) {

            double headingDegreesTemp = temp.getHeadingDegrees();
            double delta = angle - headingDegreesTemp;


            if (delta > 180 || (delta < 0 && delta > -180)) {
                headingDegreesTemp -= characteristic.getCourseChangeRateDegreesPerSecond();

            } else {
                headingDegreesTemp += characteristic.getCourseChangeRateDegreesPerSecond();
            }

            if (headingDegreesTemp > 360) {
                headingDegreesTemp -= 360;
            } else if (temp.getHeadingDegrees() < 0) {
                headingDegreesTemp += 360;
            }

            double[] coordinates = airplaneShift(
                    temp.getLatitude(), temp.getLongitude(),
                    characteristic.getCourseChangeRateDegreesPerSecond(),
                    characteristic.getMaxSpeedMetersPerSecond(),
                    1);

            temp = TemporaryPoint.builder()
                    .longitude(coordinates[0])
                    .latitude(coordinates[1])
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


}
