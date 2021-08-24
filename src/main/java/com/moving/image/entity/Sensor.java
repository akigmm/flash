package com.moving.image.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * Represents a Sensor object with all the required attributes.
 */

@Document(collection = "sensors")
@Getter
@Setter
public class Sensor {

    @Id
    private String sensorId;

    private String status;

    private Map<String, Integer> metrics;

    private List<Map<String, Object>> alerts;
}
