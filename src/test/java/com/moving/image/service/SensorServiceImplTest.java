package com.moving.image.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moving.image.client.MeasurementCollectRequest;
import com.moving.image.client.SensorStatusResponse;
import com.moving.image.entity.Measurement;
import com.moving.image.entity.Sensor;
import com.moving.image.exception.NoSensorException;
import com.moving.image.repository.MeasurementRepository;
import com.moving.image.repository.SensorRepository;
import com.moving.image.rules.RuleConstants;
import org.jeasy.rules.api.Facts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SensorServiceImplTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private ComputeAlertAction computeAlertAction;

    private SensorServiceImpl sensorService;

    @Before
    public void setUp() {
        this.sensorService = new SensorServiceImpl(sensorRepository, measurementRepository, computeAlertAction);
    }

    @Test
    public void testGetStatus() {

        String sensorId = UUID.randomUUID().toString();
        Sensor sensor = new Sensor();
        sensor.setSensorId(sensorId);
        sensor.setMetrics(new HashMap<>());
        sensor.setAlerts(new ArrayList<>());
        sensor.setStatus("ok");

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        SensorStatusResponse actualResponse = sensorService.getStatus(sensorId);

        SensorStatusResponse expectedResponse = SensorStatusResponse.builder().status("ok").build();

        Assert.assertTrue(new ReflectionEquals(expectedResponse).matches(actualResponse));

    }

    @Test(expected = NoSensorException.class)
    public void testGetStatusNoSensor() {
        String sensorId = UUID.randomUUID().toString();

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        SensorStatusResponse actualResponse = sensorService.getStatus(sensorId);

        SensorStatusResponse expectedResponse = SensorStatusResponse.builder().status("ok").build();

        Assert.assertTrue(new ReflectionEquals(expectedResponse).matches(actualResponse));
    }

    @Test
    public void testGetMetrics() {

        String sensorId = UUID.randomUUID().toString();
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("avgLast30Days", 1200);
        metrics.put("maxLast30Days", 1000);
        Sensor sensor = new Sensor();
        sensor.setSensorId(sensorId);
        sensor.setMetrics(metrics);
        sensor.setAlerts(new ArrayList<>());
        sensor.setStatus("ok");

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        Map<String, Integer> actualResponse = sensorService.getMetrics(sensorId);

        Assert.assertTrue(new ReflectionEquals(metrics).matches(actualResponse));

    }

    @Test(expected = NoSensorException.class)
    public void testGetMetricsNoSensor() {
        String sensorId = UUID.randomUUID().toString();
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("avgLast30Days", 1200);
        metrics.put("maxLast30Days", 1000);
        Sensor sensor = new Sensor();
        sensor.setSensorId(sensorId);
        sensor.setMetrics(metrics);
        sensor.setAlerts(new ArrayList<>());
        sensor.setStatus("ok");

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        Map<String, Integer> actualResponse = sensorService.getMetrics(sensorId);

        Assert.assertTrue(new ReflectionEquals(metrics).matches(actualResponse));
    }

    @Test
    public void testGetAlerts() {

        String sensorId = UUID.randomUUID().toString();
        List<Map<String, Object>> alerts = new ArrayList<>();
        Map<String, Object> alert = new HashMap<>();
        alert.put("startTime", "2019-02-02T18:55:47+00:00");
        alert.put("endTime", "2019-02-02T20:00:47+00:00");
        alert.put("measurement1", 2100);
        alert.put("measurement2", 2200);
        alert.put("measurement3", 2100);

        alerts.add(alert);

        Sensor sensor = new Sensor();
        sensor.setSensorId(sensorId);
        sensor.setMetrics(new HashMap<>());
        sensor.setAlerts(alerts);
        sensor.setStatus("ok");

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        List<Map<String, Object>> actualResponse = sensorService.getAlerts(sensorId);

        Assert.assertTrue(new ReflectionEquals(alerts).matches(actualResponse));

    }

    @Test(expected = NoSensorException.class)
    public void testGetAlertsNoSensor() {

        String sensorId = UUID.randomUUID().toString();
        List<Map<String, Object>> alerts = new ArrayList<>();
        Map<String, Object> alert = new HashMap<>();
        alert.put("startTime", "2019-02-02T18:55:47+00:00");
        alert.put("endTime", "2019-02-02T20:00:47+00:00");
        alert.put("measurement1", 2100);
        alert.put("measurement2", 2200);
        alert.put("measurement3", 2100);

        alerts.add(alert);

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        List<Map<String, Object>> actualResponse = sensorService.getAlerts(sensorId);

        Assert.assertTrue(new ReflectionEquals(alerts).matches(actualResponse));

    }

    @Test
    public void testPostMeasurement() throws IOException {
        String sensorId = UUID.randomUUID().toString();
        Sensor sensor = new Sensor();
        sensor.setSensorId(sensorId);
        sensor.setMetrics(new HashMap<>());
        sensor.setAlerts(new ArrayList<>());
        sensor.setStatus("ok");

        String measurementId = UUID.randomUUID().toString();
        Measurement measurement = new Measurement();
        measurement.setMeasurementId(measurementId);
        measurement.setSensorId(sensorId);
        measurement.setTime("2019-02-01T18:55:47+00:00");
        measurement.setCo2(2000);

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        when(measurementRepository.save(isA(Measurement.class))).thenReturn(measurement);

        ObjectMapper objectMapper = new ObjectMapper();

        ClassPathResource jsonResource = new ClassPathResource("measurements.json");
        InputStream inputStream = jsonResource.getInputStream();
        List<Measurement> measurementList = objectMapper.readValue(inputStream,  new TypeReference<List<Measurement>>(){});

        when(measurementRepository.findAllBySensorId(sensorId)).thenReturn(measurementList);

        Facts facts = new Facts();
        facts.put(RuleConstants.ATTR_MEASUREMENT_LAST_30_LIST, new ArrayList<>());
        facts.put(RuleConstants.ATTR_SENSOR_STATUS, sensor.getStatus());
        facts.put(RuleConstants.ATTR_ALERTS_MAP_LIST, new ArrayList<>());
        facts.put(RuleConstants.ATTR_METRICS_MAP, sensor.getMetrics());
        facts.put(RuleConstants.ATTR_CO2_LEVEL, 1800);
        facts.put(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST, new ArrayList<>());
        facts.put(RuleConstants.ATTR_MEASUREMENT_LIST, new ArrayList<>());

        when(computeAlertAction.prepareFacts(measurementList, sensor)).thenReturn(facts);
        doNothing().when(computeAlertAction).invoke(facts);
        when(sensorRepository.save(isA(Sensor.class))).thenReturn(sensor);

        MeasurementCollectRequest collectRequest = MeasurementCollectRequest.builder().time("2019-02-01T18:55:47+00:00").co2(2000).build();
        sensorService.postMeasurements(sensorId,collectRequest);

    }
}
