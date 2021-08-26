package com.moving.image.rules;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moving.image.entity.Measurement;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class AlertsRuleTest {

    private RulesEngine rulesEngine;
    private Rules rules;

    @Before
    public void setUp() {
        rulesEngine = new DefaultRulesEngine();
        rules = new Rules();
    }

    @Test
    public void testSingleAlertAdd() throws IOException {
        rules.register(new AlertsRule());

        ObjectMapper objectMapper = new ObjectMapper();

        ClassPathResource jsonResource = new ClassPathResource("measurements.json");
        InputStream inputStream = jsonResource.getInputStream();
        List<Measurement> measurementList = objectMapper.readValue(inputStream,  new TypeReference<List<Measurement>>(){});
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+SS:SS", Locale.ENGLISH);
        measurementList.sort(Comparator.comparing(measurement -> LocalDate.parse(measurement.getTime(), formatter), Collections.reverseOrder()));

        Facts facts = new Facts();
        facts.put(RuleConstants.ATTR_SENSOR_STATUS, "");
        facts.put(RuleConstants.ATTR_CO2_LEVEL, 0);
        facts.put(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST, measurementList.stream().limit(3).collect(Collectors.toList()));
        facts.put(RuleConstants.ATTR_ALERTS_MAP_LIST, new ArrayList<>());
        facts.put(RuleConstants.ATTR_METRICS_MAP, new HashMap<>());

        // assertions before firing rules
        assertNotNull(facts.get(RuleConstants.ATTR_ALERTS_MAP_LIST));

        // fire rules on know facts
        rulesEngine.fire(rules, facts);
        List<Map<String, Object>> actualAlerts = facts.get(RuleConstants.ATTR_ALERTS_MAP_LIST);
        assertEquals(1, actualAlerts.size());
        actualAlerts.forEach(alert -> {
            assertNotNull(alert.get("startTime"));
            assertNotNull(alert.get("endTime"));
            assertNotNull(alert.get("measurement1"));
            assertNotNull(alert.get("measurement2"));
            assertNotNull(alert.get("measurement3"));

        });
    }

    @Test
    public void testNoAlerts() throws IOException {
        rules.register(new AlertsRule());

        ObjectMapper objectMapper = new ObjectMapper();

        ClassPathResource jsonResource = new ClassPathResource("measurements_last_3.json");
        InputStream inputStream = jsonResource.getInputStream();
        List<Measurement> measurementList = objectMapper.readValue(inputStream,  new TypeReference<List<Measurement>>(){});
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+SS:SS", Locale.ENGLISH);
        measurementList.sort(Comparator.comparing(measurement -> LocalDate.parse(measurement.getTime(), formatter), Collections.reverseOrder()));

        Facts facts = new Facts();
        facts.put(RuleConstants.ATTR_SENSOR_STATUS, "");
        facts.put(RuleConstants.ATTR_CO2_LEVEL, 0);
        facts.put(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST, measurementList.stream().limit(3).collect(Collectors.toList()));
        facts.put(RuleConstants.ATTR_ALERTS_MAP_LIST, new ArrayList<>());
        facts.put(RuleConstants.ATTR_METRICS_MAP, new HashMap<>());

        // assertions before firing rules
        assertNotNull(facts.get(RuleConstants.ATTR_ALERTS_MAP_LIST));

        // fire rules on know facts
        rulesEngine.fire(rules, facts);
        List<Map<String, Object>> actualAlerts = facts.get(RuleConstants.ATTR_ALERTS_MAP_LIST);
        assertEquals(0, actualAlerts.size());

    }
}
