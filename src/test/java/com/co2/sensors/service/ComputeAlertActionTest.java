package com.co2.sensors.service;

import com.co2.sensors.entity.Measurement;
import com.co2.sensors.entity.Sensor;
import com.co2.sensors.rules.RuleConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class ComputeAlertActionTest {

    @Mock
    private RulesEngine rulesEngine;

    @Mock
    private Rules rules;

    private ComputeAlertAction computeAlertAction;

    @Before
    public void setUp() {
        this.computeAlertAction = new ComputeAlertAction(rulesEngine, rules);
    }

    @Test
    public void testPrepareFacts() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        ClassPathResource jsonResource = new ClassPathResource("sensor.json");
        InputStream inputStream = jsonResource.getInputStream();
        Sensor testSensor = objectMapper.readValue(inputStream,  new TypeReference<Sensor>(){});


        ClassPathResource jsonResource2 = new ClassPathResource("measurements.json");
        InputStream inputStream2 = jsonResource2.getInputStream();
        List<Measurement> measurementList = objectMapper.readValue(inputStream2,  new TypeReference<List<Measurement>>(){});

        Facts actualFacts = computeAlertAction.prepareFacts(measurementList, testSensor);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+SS:SS", Locale.ENGLISH);
        Facts expectedFacts = new Facts();
        expectedFacts.put(RuleConstants.ATTR_MEASUREMENT_LAST_30_LIST, measurementList.stream().filter(measurement ->
                LocalDate.now().minusDays(30).compareTo(LocalDate.parse(measurement.getTime(), formatter)) <= 0).collect(Collectors.toList()));
        expectedFacts.put(RuleConstants.ATTR_SENSOR_STATUS, testSensor.getStatus());
        expectedFacts.put(RuleConstants.ATTR_ALERTS_MAP_LIST, new ArrayList<>());
        expectedFacts.put(RuleConstants.ATTR_METRICS_MAP, testSensor.getMetrics());
        expectedFacts.put(RuleConstants.ATTR_SENSOR_STATUS_LIST, new ArrayList<>());
        expectedFacts.put(RuleConstants.ATTR_CO2_LEVEL, 18000);
        expectedFacts.put(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST, measurementList.stream().limit(3).collect(Collectors.toList()));
        expectedFacts.put(RuleConstants.ATTR_MEASUREMENT_LIST, measurementList);

        Assert.assertTrue(new ReflectionEquals(expectedFacts).matches(actualFacts));
    }
}
