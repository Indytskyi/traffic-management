package com.indytskyi.trafficmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class WayPoint {

    private Double latitude;
    private Double longitude;

    private Double altitudeMeters;

    private Double flightSpeedMetersPerSecond;
}
