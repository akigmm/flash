package com.moving.image.rules;

import com.moving.image.entity.Measurement;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import java.util.List;
import java.util.Map;

@Rule(name = "Metrics Rule", description = "service level metrics of sensor")
public class MetricsRule {

    @Condition
    public boolean when(@Fact(RuleConstants.ATTR_MEASUREMENT_LAST_30_LIST) List<Measurement> measurementListLast30) {
        return measurementListLast30!=null && !measurementListLast30.isEmpty();
    }

    @Action
    public void then(@Fact(RuleConstants.ATTR_MEASUREMENT_LAST_30_LIST) List<Measurement> measurementListLast30,
                     @Fact(RuleConstants.ATTR_METRICS_MAP) Map<String, Integer> metricsMap) {
        metricsMap.put("avgLast30Days", (int) measurementListLast30.stream().mapToDouble(Measurement::getCo2).average().orElse(0.0));
        metricsMap.put("maxLast30Days", measurementListLast30.stream().mapToInt(Measurement::getCo2).max().orElse(0));

    }
}
