package com.indytskyi.trafficmanagement.service.impl;


import com.indytskyi.trafficmanagement.model.Airplane;
import com.indytskyi.trafficmanagement.repository.AirplaneRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AirplaneService {

    private final AirplaneRepository airplaneRepository;

    private final ThreadLocal<Long> currentAirplane = new ThreadLocal<>();

    public Long save(Airplane airplane) {

        return airplaneRepository.save(airplane).getId();
    }

    public Airplane findById(Long id) {
        return airplaneRepository.findById(id).orElseThrow();
    }

    public List<Airplane> findAll() {
        return airplaneRepository.findAll();
    }

    public void delete(Long id) {
        airplaneRepository.deleteById(id);
    }




}
