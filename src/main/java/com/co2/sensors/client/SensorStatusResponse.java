package com.co2.sensors.client;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SensorStatusResponse implements Serializable {

    private String status;
}
