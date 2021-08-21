package com.moving.image.client;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class MeasurementCollectRequest implements Serializable {

    private Integer co2;

    private String time;
}
