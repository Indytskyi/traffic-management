package com.indytskyi.trafficmanagement.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AirplaneCharacteristic {

    private Double maxSpeedMetersPerSecond;

    private Double maxAccelerationMetersPerSecondSquared;

    private Double verticalVelocityMetersPerSecond;

    private Double courseChangeRateDegreesPerSecond;
}
