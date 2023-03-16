package com.indytskyi.trafficmanagement.service;

import static com.indytskyi.trafficmanagement.util.FlightNavigator.airplaneShift;
import static com.indytskyi.trafficmanagement.util.FlightNavigator.calculateAirplanePosition;
import static com.indytskyi.trafficmanagement.util.FlightNavigator.calculateCourse;
import static com.indytskyi.trafficmanagement.util.FlightNavigator.isWithinRadius;

import com.indytskyi.trafficmanagement.dto.StartTripDto;
import com.indytskyi.trafficmanagement.model.Airplane;
import com.indytskyi.trafficmanagement.model.AirplaneCharacteristic;
import com.indytskyi.trafficmanagement.model.Flight;
import com.indytskyi.trafficmanagement.model.TemporaryPoint;
import com.indytskyi.trafficmanagement.model.WayPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable updatePosition = () -> calculateRoute(airplane, startTripDto.wayPoints());

        scheduler.scheduleAtFixedRate(updatePosition, 0, 1, TimeUnit.SECONDS);
        return "The airplane with id: " + startTripDto.id() + " flew";
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

            temporaryPoint = directFlightToPoint(airplane, temporaryPoint, wayPoint);
        }
    }


    @SneakyThrows
    private TemporaryPoint directFlightToPoint(Airplane airplane, TemporaryPoint temporaryPoint, WayPoint wayPoint) {
        while (!isWithinRadius(temporaryPoint.getLatitude(),
                temporaryPoint.getLongitude(),
                wayPoint.getLatitude(),
                wayPoint.getLongitude(),
                20)) {

            double[] coordinates = calculateAirplanePosition(
                    temporaryPoint.getFlightSpeedMetersPerSecond(),
                    temporaryPoint.getHeadingDegrees(),
                    temporaryPoint.getLatitude(),
                    temporaryPoint.getLongitude()
            );


            temporaryPoint = saveToAirplaneTemporaryPoint(airplane, temporaryPoint, coordinates, temporaryPoint.getHeadingDegrees());

            TimeUnit.SECONDS.sleep(1);
        }
        return temporaryPoint;
    }


    @SneakyThrows
    public TemporaryPoint turnAirplane(Airplane airplane, TemporaryPoint startPosition, double angle) {

        AirplaneCharacteristic characteristic = airplane.getAirplaneCharacteristic();
        TemporaryPoint temp = startPosition;

        while (temp.getHeadingDegrees() != angle) {

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

            headingDegreesTemp = cornerBoundaryCheck(headingDegreesTemp);
            changeRateDegreesTemp = isRotationLessThanOneTurn(characteristic, delta, changeRateDegreesTemp);
            if (changeRateDegreesTemp == delta) {
                headingDegreesTemp = angle;
            }
            double[] coordinates = airplaneShift(
                    temp.getLatitude(), temp.getLongitude(),
                    changeRateDegreesTemp,
                    characteristic.getMaxSpeedMetersPerSecond());
            temp = saveToAirplaneTemporaryPoint(airplane, temp, coordinates, headingDegreesTemp);

            TimeUnit.SECONDS.sleep(1);
        }

        return temp;
    }

    private TemporaryPoint saveToAirplaneTemporaryPoint(Airplane airplane, TemporaryPoint temp,
                                                        double[] coordinates, double headingDegreesTemp) {
        temp = TemporaryPoint.builder()
                .latitude(coordinates[0])
                .longitude(coordinates[1])
                .altitudeMeters(100.0)
                .flightSpeedMetersPerSecond(temp.getFlightSpeedMetersPerSecond())
                .headingDegrees(headingDegreesTemp)
                .build();

        airplane.getFlights().get(airplane.getFlights().size() - 1).getPassedPoints().add(temp);

        airplaneService.save(airplane);
        return temp;
    }

    private double cornerBoundaryCheck(double headingDegreesTemp) {
        if (headingDegreesTemp > 360) {
            headingDegreesTemp -= 360;
        } else if (headingDegreesTemp < 0) {
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
