package com.moving.image.client;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SensorStatusResponse implements Serializable {

    private String status;
}
