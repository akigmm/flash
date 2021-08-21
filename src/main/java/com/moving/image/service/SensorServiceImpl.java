package com.moving.image.service;

import com.moving.image.client.MeasurementCollectRequest;
import com.moving.image.client.SensorStatusResponse;
import com.moving.image.entity.Sensor;
import com.moving.image.exception.NoSensorException;
import com.moving.image.repository.MeasurementRepository;
import com.moving.image.repository.SensorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.Oneway;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SensorServiceImpl implements SensorService{

    private final SensorRepository sensorRepository;
    private final MeasurementRepository measurementRepository;

    @Autowired
    public SensorServiceImpl(SensorRepository sensorRepository, MeasurementRepository measurementRepository) {
        this.sensorRepository = sensorRepository;
        this.measurementRepository = measurementRepository;
    }

    @Override
    public SensorStatusResponse getStatus(String sensorId) throws NoSensorException {
        Optional<Sensor> sensorOptional = sensorRepository.findById(sensorId);
        if (!sensorOptional.isPresent()) {
            throw new NoSensorException("No sensor present for requested sensor_id");
        }

        return SensorStatusResponse.builder().status(sensorOptional.get().getStatus()).build();
    }

    @Override
    public Map<String, Integer> getMetrics(String sensorId) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getAlerts(String sensorId) {
        Optional<Sensor> sensorOptional = sensorRepository.findById(sensorId);
        if (!sensorOptional.isPresent()) {
            throw new NoSensorException("No sensor present for requested sensor_id");
        }
        return sensorOptional.get().getAlerts();
    }

    @Override
    public void postMeasurements(String sensorId, MeasurementCollectRequest collectRequest) {

    }
}
