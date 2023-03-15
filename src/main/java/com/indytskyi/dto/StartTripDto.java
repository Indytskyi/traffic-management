package com.indytskyi.dto;

import com.indytskyi.trafficmanagement.model.WayPoint;
import java.util.List;

public record StartTripDto(Long id, List<WayPoint> wayPoints) { }
