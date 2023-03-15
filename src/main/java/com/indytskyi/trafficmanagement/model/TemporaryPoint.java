package com.indytskyi.trafficmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TemporaryPoint {
    private Double latitude;
    private Double longitude;

    private Double altitudeMeters;

    private Double flightSpeedMetersPerSecond;

    private Double headingDegrees;

}
