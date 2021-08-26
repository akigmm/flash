package com.co2.sensors.service;

import com.co2.sensors.exception.NoSensorException;
import com.co2.sensors.repository.MeasurementRepository;
import com.co2.sensors.repository.SensorRepository;
import com.co2.sensors.client.MeasurementCollectRequest;
import com.co2.sensors.client.SensorStatusResponse;
import com.co2.sensors.entity.Measurement;
import com.co2.sensors.entity.Sensor;
import com.co2.sensors.rules.RuleConstants;
import com.co2.sensors.rules.SensorStatus;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class SensorServiceImpl implements SensorService{

    private final SensorRepository sensorRepository;
    private final MeasurementRepository measurementRepository;
    private final ComputeAlertAction computeAlertAction;

    @Autowired
    public SensorServiceImpl(SensorRepository sensorRepository, MeasurementRepository measurementRepository, ComputeAlertAction computeAlertAction) {
        this.sensorRepository = sensorRepository;
        this.measurementRepository = measurementRepository;
        this.computeAlertAction = computeAlertAction;
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
    public Map<String, Integer> getMetrics(String sensorId) throws NoSensorException {
        Optional<Sensor> sensorOptional = sensorRepository.findById(sensorId);
        if (!sensorOptional.isPresent()) {
            throw new NoSensorException("No metrics present for requested sensor_id");
        }

        return sensorOptional.get().getMetrics();

    }

    @Override
    public List<Map<String, Object>> getAlerts(String sensorId) throws NoSensorException {
        Optional<Sensor> sensorOptional = sensorRepository.findById(sensorId);
        if (!sensorOptional.isPresent()) {
            throw new NoSensorException("No alerts present for requested sensor_id");
        }
        return sensorOptional.get().getAlerts();
    }

    @Override
    public void postMeasurements(String sensorId, MeasurementCollectRequest collectRequest) {
        Sensor sensor = new Sensor();
        Optional<Sensor> sensorOptional = sensorRepository.findById(sensorId);
        if (!sensorOptional.isPresent()) {
            sensor.setSensorId(sensorId);
            sensor.setStatus(SensorStatus.OK.label);
            sensor.setAlerts(new ArrayList<>());
            sensor.setMetrics(new HashMap<>());
            sensorRepository.save(sensor);
        }
        Measurement measurement = new Measurement();
        measurement.setCo2(collectRequest.getCo2());
        measurement.setTime(collectRequest.getTime());
        measurement.setSensorId(sensorId);
        measurementRepository.save(measurement);

        List<Measurement> measurements = measurementRepository.findAllBySensorId(sensorId);
        Facts facts = computeAlertAction.prepareFacts(measurements, sensorOptional.orElse(sensor));
        computeAlertAction.invoke(facts);

        List<Map<String, Object>> alertsList = facts.get(RuleConstants.ATTR_ALERTS_MAP_LIST);
        List<String> statusList = facts.get(RuleConstants.ATTR_SENSOR_STATUS_LIST);
        sensor.setSensorId(sensorId);
        sensor.setStatus(statusList.isEmpty()?sensorOptional.orElse(sensor).getStatus():statusList.get(0));
        sensor.setMetrics(facts.get(RuleConstants.ATTR_METRICS_MAP));
        sensor.setAlerts(sensorOptional.map(value -> Stream.concat(alertsList.stream(),
                value.getAlerts().stream()).collect(Collectors.toList())).orElse(alertsList));
        sensorRepository.save(sensor);

    }
}
