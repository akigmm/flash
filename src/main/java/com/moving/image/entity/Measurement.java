package com.moving.image.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;

/**
 * Represents a Measurement object with all the required attributes.
 */

@Document(collection = "measurements")
@Getter
@Setter
public class Measurement {

    @Id
    private String measurementId;

    private String time;

    private Integer co2;

    private String sensorId;
}
