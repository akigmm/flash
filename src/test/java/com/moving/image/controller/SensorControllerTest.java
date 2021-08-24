package com.moving.image.controller;

import com.moving.image.client.MeasurementCollectRequest;
import com.moving.image.client.SensorStatusResponse;
import com.moving.image.entity.Measurement;
import com.moving.image.service.SensorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(MockitoJUnitRunner.class)
public class SensorControllerTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8
    );

    @Mock
    private SensorService sensorService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SensorController(sensorService))
                .build();
    }

    @Test
    public void testGetStatusAPI() throws Exception {
        String sensorId = UUID.randomUUID().toString();
        SensorStatusResponse actualResponse = SensorStatusResponse.builder().status("ok").build();

        when(sensorService.getStatus(sensorId)).thenReturn(actualResponse);
        mockMvc.perform(get("/api/v1/sensors/{sensorId}", sensorId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ok")));
    }

    @Test
    public void testGetMetricsAPI() throws Exception {
        String sensorId = UUID.randomUUID().toString();
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("avgLast30Days", 1200);
        metrics.put("maxLast30Days", 1000);

        when(sensorService.getMetrics(sensorId)).thenReturn(metrics);
        mockMvc.perform(get("/api/v1/sensors/{sensorId}/metrics", sensorId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxLast30Days", is(1200)))
                .andExpect(jsonPath("$.avgLast30Days", is(1000)));
    }

    @Test
    public void testGetAlertsAPI() throws Exception {
        String sensorId = UUID.randomUUID().toString();
        List<Map<String, Object>> alerts = new ArrayList<>();
        Map<String, Object> alert = new HashMap<>();
        alert.put("startTime", "2019-02-02T18:55:47+00:00");
        alert.put("endTime", "2019-02-02T20:00:47+00:00");
        alert.put("measurement1", 2100);
        alert.put("measurement2", 2200);
        alert.put("measurement3", 2100);

        alerts.add(alert);

        when(sensorService.getAlerts(sensorId)).thenReturn(alerts);
        mockMvc.perform(get("/api/v1/sensors/{sensorId}/alerts", sensorId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostMeasurementsAPI() throws Exception {
        String sensorId = UUID.randomUUID().toString();
        String measurementId = UUID.randomUUID().toString();
        Measurement measurement = new Measurement();
        measurement.setMeasurementId(measurementId);
        measurement.setSensorId(sensorId);
        measurement.setTime("2019-02-01T18:55:47+00:00");
        measurement.setCo2(2000);

        MeasurementCollectRequest collectRequest = MeasurementCollectRequest.builder().time("2019-02-01T18:55:47+00:00").co2(2000).build();

        doNothing().when(sensorService).postMeasurements(sensorId,collectRequest);
        mockMvc.perform(get("/api/v1/sensors/{sensorId}/alerts", sensorId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }
}
