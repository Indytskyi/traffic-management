package com.indytskyi.trafficmanagement.controller;

import com.indytskyi.trafficmanagement.dto.StartTripDto;
import com.indytskyi.trafficmanagement.model.Airplane;
import com.indytskyi.trafficmanagement.service.PlaneCalculation;
import com.indytskyi.trafficmanagement.service.impl.AirplaneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/airplane")
@RequiredArgsConstructor
public class AirplaneController {

    private final AirplaneService airplaneService;
    private final PlaneCalculation planeCalculation;

    @PostMapping
    public ResponseEntity<Long> save(@RequestBody Airplane airplane) {
        return ResponseEntity.ok(airplaneService.save(airplane));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Airplane> findById(@PathVariable Long id) {
        return ResponseEntity.ok(airplaneService.findById(id));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<String> startAirplane(@PathVariable Long id, @RequestBody StartTripDto startTripDto) {
        return ResponseEntity.ok(planeCalculation.startTrip(startTripDto));
    }




}
