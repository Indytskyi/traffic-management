package com.indytskyi.trafficmanagement.service;

import static com.indytskyi.util.Gfg.airplaneShift;
import static com.indytskyi.util.Gfg.calculateCourse;
import static com.indytskyi.util.Gfg.isWithinRadius;

import com.indytskyi.trafficmanagement.dto.StartTripDto;
import com.indytskyi.trafficmanagement.model.Airplane;
import com.indytskyi.trafficmanagement.model.AirplaneCharacteristic;
import com.indytskyi.trafficmanagement.model.Flight;
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
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaneCalculation {

    private final AirplaneService airplaneService;

    public String startTrip(StartTripDto startTripDto) {

        var airplane = airplaneService.findById(startTripDto.id());
        airplane.getFlights().add(Flight.builder()
                .number((long) (airplane.getFlights().isEmpty()
                        ? 1
                        : airplane.getFlights().size() + 1))
                .wayPoints(startTripDto.wayPoints())
                .passedPoints(new ArrayList<>())
                .build());


        calculateRoute(airplane, startTripDto.wayPoints());
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
        
    }


    @SneakyThrows
    public void calculateRoute(Airplane airplane, List<WayPoint> wayPoints) {
        TemporaryPoint temporaryPoint = airplane.getPosition();
        for (WayPoint wayPoint : wayPoints) {
            temporaryPoint = turnAirplane(airplane,
                    temporaryPoint,
                    calculateCourse(temporaryPoint.getLatitude(),
                            temporaryPoint.getLongitude(),
                            wayPoint.getLatitude(),
                            wayPoint.getLongitude()));


            while (!isWithinRadius(temporaryPoint.getLatitude(),
                    temporaryPoint.getLongitude(),
                    wayPoint.getLatitude(),
                    wayPoint.getLongitude(),
                    20)) {

                double[] coordinates = airplaneShift(
                        temporaryPoint.getLatitude(), temporaryPoint.getLongitude(),
                        0,
                        100,
                        1);

                temporaryPoint = TemporaryPoint.builder()
                        .longitude(coordinates[0])
                        .latitude(coordinates[1])
                        .altitudeMeters(100.0)
                        .flightSpeedMetersPerSecond(temporaryPoint.getFlightSpeedMetersPerSecond())
                        .headingDegrees(temporaryPoint.getHeadingDegrees())
                        .build();

                airplane.getFlights()
                        .get(airplane.getFlights().size() - 1)
                        .getPassedPoints()
                        .add(temporaryPoint);

                airplaneService.save(airplane);

                Thread.sleep(1000);
            }

        }

    }


    @SneakyThrows
    public TemporaryPoint turnAirplane(Airplane airplane, TemporaryPoint startPosition, double angle) {

        AirplaneCharacteristic characteristic = airplane.getAirplaneCharacteristic();
        TemporaryPoint temp = startPosition;

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

            headingDegreesTemp = cornerBoundaryCheck(temp, headingDegreesTemp);
            changeRateDegreesTemp = isRotationLessThanOneTurn(characteristic, delta, changeRateDegreesTemp);

            double[] coordinates = airplaneShift(
                    temp.getLatitude(), temp.getLongitude(),
                    changeRateDegreesTemp,
                    characteristic.getMaxSpeedMetersPerSecond(),
                    1);

            temp = TemporaryPoint.builder()
                    .longitude(coordinates[0])
                    .latitude(coordinates[1])
                    .flightSpeedMetersPerSecond(characteristic.getMaxSpeedMetersPerSecond())
                    .headingDegrees(headingDegreesTemp)
                    .build();

            airplane.getFlights().get(airplane.getFlights().size() - 1).getPassedPoints().add(temp);

            airplaneService.save(airplane);
            Thread.sleep(1000);
        }

        return temp;
    }

    private double cornerBoundaryCheck(TemporaryPoint temp, double headingDegreesTemp) {
        if (headingDegreesTemp > 360) {
            headingDegreesTemp -= 360;
        } else if (temp.getHeadingDegrees() < 0) {
            headingDegreesTemp += 360;
        }
        return headingDegreesTemp;
    }

    private double isRotationLessThanOneTurn(AirplaneCharacteristic characteristic, double delta, double changeRateDegreesTemp) {
        return delta < characteristic.getCourseChangeRateDegreesPerSecond()
                ? delta
                : changeRateDegreesTemp;
    }
}
