package com.co2.sensors.rules;

import com.co2.sensors.entity.Measurement;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Rule(name = "Alerts Rule", description = "creates and stores alerts")
public class AlertsRule {

    @Condition
    public boolean when(@Fact(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST) List<Measurement> measurementListLast3) {
        return  measurementListLast3!=null && measurementListLast3.size()==3;
    }

    @Action
    public void then(@Fact(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST) List<Measurement> measurementListLast3,
                     @Fact(RuleConstants.ATTR_ALERTS_MAP_LIST) List<Map<String, Object>> alertsMapList) {

        if(measurementListLast3.stream().allMatch(measurement -> measurement.getCo2()>=2000)) {
            Map<String, Object> alertsMap = new HashMap<>();
            alertsMap.put("startTime", measurementListLast3.get(0).getTime());
            alertsMap.put("endTime", measurementListLast3.get(2).getTime());
            for(int i = 0; i<3; i++) {
                alertsMap.put(String.format("measurement%s", i+1), measurementListLast3.get(i));
            }
            alertsMapList.add(alertsMap);
        }
    }
}
