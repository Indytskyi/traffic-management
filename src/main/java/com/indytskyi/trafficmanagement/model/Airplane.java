package com.indytskyi.trafficmanagement.model;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@Builder
public class Airplane {

    @Id
    private Long id;

    private AirplaneCharacteristic airplaneCharacteristic;

    private TemporaryPoint  position;

    private List<Flight> flights;
}
