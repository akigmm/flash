package com.co2.sensors.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasurementCollectRequest {

    @JsonProperty("co2")
    private Integer co2;

    @JsonProperty("time")
    private String time;
}
