package com.co2.sensors.controller;

import com.co2.sensors.client.MeasurementCollectRequest;
import com.co2.sensors.client.SensorStatusResponse;
import com.co2.sensors.service.SensorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@Validated
@RequestMapping("/api/v1/sensors")
public class SensorController {

    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService){
        this.sensorService = sensorService;
    }

    @GetMapping(path = "/{sensorId}")
    public ResponseEntity<SensorStatusResponse> fetchSensorStatus(@PathVariable(name = "sensorId") String sensorId) {

        log.info("----- Sensor Status API -----");

        SensorStatusResponse response = sensorService.getStatus(sensorId);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping(path = "/{sensorId}/metrics")
    public ResponseEntity<Map<String, Integer>> fetchSensorMetrics(@PathVariable(name = "sensorId") String sensorId) {

        log.info("----- Sensor Metrics API -----");
        Map<String, Integer> responseMap = sensorService.getMetrics(sensorId);
        return ResponseEntity.ok().body(responseMap);

    }

    @GetMapping(path = "/{sensorId}/alerts")
    public ResponseEntity<List<Map<String, Object>>> fetchSensorAlerts(@PathVariable(name = "sensorId") String sensorId) {

        log.info("----- Sensor Alerts API -----");
        List<Map<String, Object>> responseList = sensorService.getAlerts(sensorId);
        return ResponseEntity.ok().body(responseList);

    }

    @PostMapping(path = "/{sensorId}/measurements")
    public ResponseEntity<Void> collectMeasurements(@PathVariable(name = "sensorId") String sensorId,
                                                    @RequestBody MeasurementCollectRequest collectRequest) {

        log.info("----- Sensor Collect API -----");
        sensorService.postMeasurements(sensorId, collectRequest);
        return ResponseEntity.ok().build();

    }
}
