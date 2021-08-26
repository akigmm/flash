package com.co2.sensors.service;

import com.co2.sensors.client.SensorStatusResponse;
import com.co2.sensors.exception.NoSensorException;
import com.co2.sensors.client.MeasurementCollectRequest;

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
    Map<String, Integer> getMetrics(String sensorId) throws NoSensorException;

    /**
     * Fetches from the sensors repository for list of alerts
     * Used for the fetch alerts API
     */
    List<Map<String, Object>> getAlerts(String sensorId) throws NoSensorException;

    /**
     * Adds into the measurement repository.
     * Used for the collect measures API
     */
    void postMeasurements(String sensorId, MeasurementCollectRequest collectRequest);

}
