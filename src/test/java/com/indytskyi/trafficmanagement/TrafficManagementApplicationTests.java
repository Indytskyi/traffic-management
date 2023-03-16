package com.indytskyi.trafficmanagement;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import com.indytskyi.trafficmanagement.model.Airplane;
import com.indytskyi.trafficmanagement.service.PlaneCalculation;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TrafficManagementApplicationTests {


    private static final double EARTH_RADIUS = 6371000.0; // in meters


    @Test
    void superTest() {
//        PlaneCalculation.calculateRoute(Airplane.builder().build(), new ArrayList<>());
    }
    @Test
    void contextLoads() {
    }

    @Test
    void dfkjsdlf() {
        // задаем начальные координаты
        //49.863535, 23.962257
        //49.846311, 24.005926


        double lon = 49.863535;
        double lat = 23.962257;

        // задаем начальный курс и скорость
        double heading = 90;
        double speed = 20;

        // задаем конечные координаты
        double destLon = 49.846311;
        double destLat = 24.005926;

        // задаем угол поворота в градусах в секунду
        double turnRate = 1;

        // задаем время между обновлениями в секундах
        double timeInterval = 1;

        // конвертация скорости в км/сек
        double speedPerSecond = speed / 3600;

        // расчет расстояния между начальной и конечной точками в км
        double earthRadius = 6371; // средний радиус Земли в км
        double previousLongitude = toRadians(destLon);
        double currentLongitude = toRadians(lon);
        double previousLatitude = toRadians(destLat);
        double currentLatitude = toRadians(lat);

        double subtractingLongitude = currentLongitude - previousLongitude;
        double subtractingLatitude = currentLatitude - previousLatitude;
        double intermediateCalculation = pow(sin(subtractingLatitude / 2), 2)
                + cos(previousLatitude) * cos(currentLatitude)
                * pow(sin(subtractingLongitude / 2), 2);

        double arc = 2 * asin(sqrt(intermediateCalculation));
        double distance = earthRadius * arc;

        // расчет времени, необходимого для достижения конечной точки в секундах
        double timeToDest = distance / speedPerSecond;

        // устанавливаем начальное время и флаг окончания перелета
        double timeElapsed = 0;
        boolean flightFinished = false;

        // цикл пересчета координат
        while (!flightFinished) {
            // расчет угла поворота на основе времени, прошедшего с предыдущего обновления
            double turnAngle = turnRate * timeInterval;

            // расчет новых координат
            if (timeElapsed >= timeToDest) {
                // если время, прошедшее с предыдущего обновления, больше или равно времени, необходимому для достижения конечной точки,
                // то новые координаты - конечные координаты, и перелет завершается
                lon = destLon;
                lat = destLat;
                flightFinished = true;
            } else {
                // иначе расчитываем новые координаты на основе текущей скорости и времени, прошедшего с предыдущего обновления
                double d = speedPerSecond * timeInterval;
                double brng = Math.atan2(Math.sin(Math.toRadians(destLon - lon)) * Math.cos(Math.toRadians(destLat)),
                        Math.cos(Math.toRadians(lat)) * Math.sin(Math.toRadians(destLat)) -
                                Math.sin(Math.toRadians(lat)) * Math.cos(Math.toRadians(destLat)) *
                                        Math.cos(Math.toRadians(destLon - lon)));
                double lat2 = Math.asin(Math.sin(Math.toRadians(lat)) * Math.cos(d / earthRadius) +
                        Math.cos(Math.toRadians(lat)) * Math.sin(d / earthRadius) * Math.cos(brng));
                double lon2 = lon + Math.atan2(Math.sin(brng) * Math.sin(d / earthRadius) * Math.cos(Math.toRadians(lat)),
                        Math.cos(d / earthRadius) - Math.sin(Math.toRadians(lat)) * Math.sin(lat2));
                lat = Math.toDegrees(lat2);
                lon = Math.toDegrees(lon2);
            }

            // увеличиваем время, прошедшее с предыдущего обновления
            timeElapsed += timeInterval;

            // выводим текущие координаты и время в консоль
            System.out.println("Lat: " + lat + ", Lon: " + lon + ", Time elapsed: " + timeElapsed);

            // поворачиваем на новый угол
            heading += turnAngle;

            // проверяем, не вышли ли мы за пределы допустимых значений угла
            if (heading > 360) {
                heading -= 360;
            } else if (heading < 0) {
                heading += 360;
            }
        }

    }

    @Test
    void asdas() {

        some(49.863535, 23.962257, 40, 100, 1);

    }

    public static void some(double latitude, double longitude, double angle, double speed, double time) {


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

        System.out.println("lon " + newLongitude);
        System.out.println("lat " + newLatitude);
    }

    @Test
    void aaaa() {
        // initial coordinates
        double lat1 = 23.962257;
        double lon1 = 49.863535;

        // final coordinates
        double lat2 = 24.005926;
        double lon2 = 49.846311;

        // initial course
        double course = 120.0;

        // speed in meters per second
        double speed = 100.0;

        // angle of rotation in degrees per second
        double rotation = 2.0;

        // calculate the distance and initial bearing between the two points
        double d = distance(lat1, lon1, lat2, lon2);
        double initialBearing = calculateInitialBearing(lat1, lon1, lat2, lon2);

        // convert the course to radians
        double courseRadians = Math.toRadians(course);

        // calculate the per-second report of the airplane's movements
        double t = 0.0;
        while (t <= d / speed) {
            double distanceTraveled = speed * t;

            // calculate the new latitude and longitude based on the course and distance traveled
            double lat = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(lat1)) * Math.cos(distanceTraveled / EARTH_RADIUS) +
                    Math.cos(Math.toRadians(lat1)) * Math.sin(distanceTraveled / EARTH_RADIUS) * Math.cos(courseRadians)));

            double lon = lon1 + Math.toDegrees(Math.atan2(Math.sin(courseRadians) * Math.sin(distanceTraveled / EARTH_RADIUS) * Math.cos(Math.toRadians(lat1)),
                    Math.cos(distanceTraveled / EARTH_RADIUS) - Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat))));

            // calculate the new course based on the angle of rotation
            course = (course + rotation) % 360;

            // calculate the distance and bearing between the new point and the final destination
            double distanceToDestination = distance(lat, lon, lat2, lon2);
            double bearingToDestination = calculateInitialBearing(lat, lon, lat2, lon2);

            // output the per-second report
            System.out.println("Time: " + t + " seconds");
            System.out.println("Distance traveled: " + distanceTraveled + " meters");
            System.out.println("Latitude: " + lat);
            System.out.println("Longitude: " + lon);
            System.out.println("Course: " + course);
            System.out.println("Distance to destination: " + distanceToDestination + " meters");
            System.out.println("Bearing to destination: " + bearingToDestination);
            System.out.println();

            t++;
        }
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


    private static double calculateInitialBearing(double lat1, double lon1, double lat2, double lon2) {
        double dLon = Math.toRadians(lon2 - lon1);
        double y = Math.sin(dLon) * Math.cos(Math.toRadians(lat2));
        double x = Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) -
                Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(dLon);
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

}


/*
// задаем начальные координаты
double lon = startLon;
double lat = startLat;

// задаем начальный курс и скорость
double heading = initialHeading;
double speed = initialSpeed;

// задаем конечные координаты
double destLon = endLon;
double destLat = endLat;

// задаем угол поворота в градусах в секунду
double turnRate = turnRateDegPerSec;

// задаем время между обновлениями в секундах
double timeInterval = 1;

// конвертация скорости в км/сек
double speedPerSecond = speed / 3600;

// расчет расстояния между начальной и конечной точками в км
GeodeticCalculator gc = new GeodeticCalculator();
gc.setStartingPosition(new DirectPosition2D(lon, lat));
gc.setDestinationPosition(new DirectPosition2D(destLon, destLat));
double distance = gc.getOrthodromicDistance() / 1000;

// расчет времени, необходимого для достижения конечной точки в секундах
double timeToDest = distance / speedPerSecond;

// устанавливаем начальное время и флаг окончания перелета
double timeElapsed = 0;
boolean flightFinished = false;

// цикл пересчета координат
while (!flightFinished) {
    // расчет угла поворота на основе времени, прошедшего с предыдущего обновления
    double turnAngle = turnRate * timeInterval;

    // расчет новых координат
    if (timeElapsed >= timeToDest) {
        // если время, прошедшее с предыдущего обновления, больше или равно времени, необходимому для достижения конечной точки,
        // то новые координаты - конечные координаты, и перелет завершается
        lon = destLon;
        lat = destLat;
        flightFinished = true;
    } else {
        // иначе расчитываем новые координаты на основе текущей скорости и времени, прошедшего с предыдущего обновления
        gc.setStartingPosition(new DirectPosition2D(lon, lat));
        gc.setDirection(heading, speedPerSecond * timeInterval);
        DirectPosition2D newPosition = gc.getDestinationPosition();
        lon = newPosition.getX();
        lat = newPosition.getY();
        heading += turnAngle; // корректируем курс на основе угла поворота
    }

    // выводим новые координаты и курс
    System.out.printf("New position: (%f, %f), heading: %f\n", lon, lat, heading);

    // увеличиваем время, прошедшее с предыдущего обновления
    timeElapsed += timeInterval;
}

  private void  check() {
        // initial coordinates
        double lat1 = 23.962257;
        double lon1 = 49.863535;

        // final coordinates
        double lat2 = 24.005926;
        double lon2 = 49.846311;

        // initial course
        double course = 45.0;

        // speed in meters per second
        double speed = 100.0;

        // angle of rotation in degrees per second
        double rotation = 1.0;

        // calculate the distance and initial bearing between the two points
        double d = distance(lat1, lon1, lat2, lon2);

        // convert the course to radians
        double courseRadians = Math.toRadians(course);

        // calculate the per-second report of the airplane's movements
        double t = 0.0;
        while (t <= d / speed) {
            double distanceTraveled = speed * t;

            // calculate the new latitude and longitude based on the course and distance traveled
            double lat = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(lat1)) * Math.cos(distanceTraveled / EARTH_RADIUS) +
                    Math.cos(Math.toRadians(lat1)) * Math.sin(distanceTraveled / EARTH_RADIUS) * Math.cos(courseRadians)));

            double lon = lon1 + Math.toDegrees(Math.atan2(Math.sin(courseRadians) * Math.sin(distanceTraveled / EARTH_RADIUS) * Math.cos(Math.toRadians(lat1)),
                    Math.cos(distanceTraveled / EARTH_RADIUS) - Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat))));

            // calculate the new course based on the angle of rotation
            course = (course + rotation) % 360;

            // calculate the distance and bearing between the new point and the final destination
            double distanceToDestination = distance(lat, lon, lat2, lon2);
            double bearingToDestination = calculateInitialBearing(lat, lon, lat2, lon2);

            // output the per-second report
            System.out.println("Time: " + t + " seconds");
            System.out.println("Distance traveled: " + distanceTraveled + " meters");
            System.out.println("Latitude: " + lat);
            System.out.println("Longitude: " + lon);
            System.out.println("Course: " + course);
            System.out.println("Distance to destination: " + distanceToDestination + " meters");
            System.out.println("Bearing to destination: " + bearingToDestination);
            System.out.println();

            t++;
        }


    }

 */

