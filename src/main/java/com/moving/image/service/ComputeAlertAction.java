package com.moving.image.service;

import com.moving.image.entity.Measurement;
import com.moving.image.entity.Sensor;
import com.moving.image.rules.RuleConstants;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
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
    public Facts prepareFacts(List<Measurement> measurements, Sensor sensor) throws ParseException{

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss+ss:ss");

        measurements.sort(Comparator.comparingLong((Measurement m) -> {
            try {
                return formatter.parse(m.getTime()).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }));

        int co2Level = measurements.stream().mapToInt(Measurement::getCo2).sum();
        List<Measurement> measurementListTopThree = measurements.stream().limit(3).collect(Collectors.toList());

        Facts facts = new Facts();
        facts.put(RuleConstants.ATTR_MEASUREMENT_LIST, measurementListTopThree);
        facts.put(RuleConstants.ATTR_SENSOR_STATUS, sensor.getStatus());
        facts.put(RuleConstants.ATTR_ALERT_LIST, sensor.getAlerts());
        facts.put(RuleConstants.ATTR_METRICS_MAP, sensor.getMetrics());
        facts.put(RuleConstants.ATTR_CO2_LEVEL, co2Level);

        return facts;

    }
}
