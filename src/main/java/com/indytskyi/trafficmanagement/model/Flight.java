package com.indytskyi.trafficmanagement.model;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Flight {

    private Long number;

    private List<WayPoint> wayPoints;

    private List<TemporaryPoint> passedPoints;
}
