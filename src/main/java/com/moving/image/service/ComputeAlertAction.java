package com.moving.image.service;

import com.moving.image.entity.Measurement;
import com.moving.image.entity.Sensor;
import com.moving.image.rules.RuleConstants;
import com.moving.image.rules.SensorStatus;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Action class to compute status based on the input fact.
 */

@Component
public class ComputeAlertAction {

    private final RulesEngine rulesEngine;
    private final Rules rules;

    @Autowired
    public ComputeAlertAction(RulesEngine rulesEngine, Rules rules) {
        this.rulesEngine = rulesEngine;
        this.rules = rules;
    }

    /**
     *
     * Starts the rule engine
     */
    public void invoke(Facts facts) {
        rulesEngine.fire(rules, facts);
    }

    /**
     *
     * Prepare inputs for the rule engine
     */
    public Facts prepareFacts(List<Measurement> measurements, Sensor sensor) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+SS:SS", Locale.ENGLISH);


        measurements.sort(Comparator.comparing(measurement -> LocalDate.parse(measurement.getTime(), formatter), Collections.reverseOrder()));

        int co2Level = measurements.stream().mapToInt(Measurement::getCo2).sum();
        List<Measurement> measurementListLastThree = measurements.stream().limit(3).collect(Collectors.toList());
        List<Measurement> measurementListLast30Days = measurements.stream().filter(measurement ->
                LocalDate.now().minusDays(30).compareTo(LocalDate.parse(measurement.getTime(), formatter)) <= 0).collect(Collectors.toList());

        List<String> sensorStatusList = new ArrayList<>();
        Facts facts = new Facts();
        facts.put(RuleConstants.ATTR_MEASUREMENT_LAST_30_LIST, measurementListLast30Days);
        facts.put(RuleConstants.ATTR_SENSOR_STATUS, sensor.getStatus());
        facts.put(RuleConstants.ATTR_SENSOR_STATUS_LIST, sensorStatusList);
        facts.put(RuleConstants.ATTR_ALERTS_MAP_LIST, new ArrayList<>());
        facts.put(RuleConstants.ATTR_METRICS_MAP, sensor.getMetrics());
        facts.put(RuleConstants.ATTR_CO2_LEVEL, co2Level);
        facts.put(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST, measurementListLastThree);
        facts.put(RuleConstants.ATTR_MEASUREMENT_LIST, measurements);

        return facts;

    }
}
