package com.indytskyi.trafficmanagement.repository;

import com.indytskyi.trafficmanagement.model.Airplane;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AirplaneRepository extends MongoRepository<Airplane, Long> {
}