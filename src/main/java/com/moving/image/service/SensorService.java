package com.moving.image.service;

import com.moving.image.client.MeasurementCollectRequest;
import com.moving.image.client.SensorStatusResponse;
import com.moving.image.exception.NoSensorException;

import java.util.List;
import java.util.Map;

public interface SensorService {

    /**
     * Fetches from the sensors repository.
     * Used for the fetch status API
     */
    SensorStatusResponse getStatus(String sensorId) throws NoSensorException;

    /**
     * Fetches from the sensors repository and compute metrics
     * Used for the fetch metrics API
     */
    Map<String, Integer> getMetrics(String sensorId);

    /**
     * Fetches from the sensors repository for list of alerts
     * Used for the fetch alerts API
     */
    List<Map<String, Object>> getAlerts(String sensorId);

    /**
     * Adds into the measurement repository.
     * Used for the collect measures API
     */
    void postMeasurements(String sensorId, MeasurementCollectRequest collectRequest);

}
