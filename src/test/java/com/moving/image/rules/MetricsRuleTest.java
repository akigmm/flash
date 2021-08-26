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

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MetricsRuleTest {

    private RulesEngine rulesEngine;
    private Rules rules;

    @Before
    public void setUp() {
        rulesEngine = new DefaultRulesEngine();
        rules = new Rules();
    }

    @Test
    public void testMetricsFalseRule() {
        rules.register(new MetricsRule());

        Facts facts = new Facts();
        facts.put(RuleConstants.ATTR_METRICS_MAP, new HashMap<>());
        facts.put(RuleConstants.ATTR_MEASUREMENT_LAST_30_LIST, new ArrayList<>());
        // assertions before firing rules
        assertNotNull(facts.get(RuleConstants.ATTR_METRICS_MAP));

        // fire rules on know facts
        rulesEngine.fire(rules, facts);
        Map<String, Integer> actualMap = facts.get(RuleConstants.ATTR_METRICS_MAP);

        assertEquals(0, actualMap.size());
    }

    @Test
    public void testMetricsTrueRule() throws IOException {
        rules.register(new MetricsRule());

        ObjectMapper objectMapper = new ObjectMapper();

        ClassPathResource jsonResource = new ClassPathResource("measurements_last_3.json");
        InputStream inputStream = jsonResource.getInputStream();
        List<Measurement> measurementList = objectMapper.readValue(inputStream,  new TypeReference<List<Measurement>>(){});

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+SS:SS", Locale.ENGLISH);

        Facts facts = new Facts();
        facts.put(RuleConstants.ATTR_METRICS_MAP, new HashMap<>());
        facts.put(RuleConstants.ATTR_MEASUREMENT_LAST_30_LIST, measurementList.stream().filter(measurement ->
                LocalDate.now().minusDays(30).compareTo(LocalDate.parse(measurement.getTime(), formatter)) <= 0).collect(Collectors.toList()));
        // assertions before firing rules
        assertNotNull(facts.get(RuleConstants.ATTR_METRICS_MAP));

        // fire rules on know facts
        rulesEngine.fire(rules, facts);
        Map<String, Integer> actualMap = facts.get(RuleConstants.ATTR_METRICS_MAP);
        Map<String, Integer> expectedMap = new HashMap<>();

        expectedMap.put("avgLast30Days", 1100);
        expectedMap.put("maxLast30Days", 1200);

        assertEquals(expectedMap.keySet(), actualMap.keySet());
    }
}
