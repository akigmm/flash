package com.moving.image.rules;

import com.moving.image.entity.Measurement;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import java.util.*;

@Rule(name = "Sensor Status Rule", description = "alert if the CO2 concentrations reach critical levels")
public class StatusAndAlertsRule {

    @Condition
    public boolean when(@Fact(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST) List<Measurement> measurementListLast3,
                        @Fact(RuleConstants.ATTR_CO2_LEVEL) Integer co2Level) {
        return  co2Level>2000 || measurementListLast3.size()==3;
    }

    @Action
    public void then(@Fact(RuleConstants.ATTR_SENSOR_STATUS) String status,
                     @Fact(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST) List<Measurement> measurementListLast3,
                     @Fact(RuleConstants.ATTR_CO2_LEVEL) Integer co2Level,
                     @Fact(RuleConstants.ATTR_ALERTS_MAP_LIST) List<Map<String, Object>> alertsMapList,
                     @Fact(RuleConstants.ATTR_MEASUREMENT_LIST) List<Measurement> measurementList) {

        if(measurementListLast3.stream().allMatch(measurement -> measurement.getCo2()>=2000)) {
            status = SensorStatus.ALERT.label;
            Map<String, Object> alertsMap = new HashMap<>();
            alertsMap.put("startTime", measurementListLast3.get(0).getTime());
            alertsMap.put("endTime", measurementListLast3.get(2).getTime());
            measurementListLast3.forEach(measurement -> alertsMap.put("measurement", measurement.getCo2()));
            alertsMapList.add(alertsMap);
        }
        else if (status.equalsIgnoreCase(SensorStatus.ALERT.label) && measurementListLast3.stream().allMatch(measurement -> measurement.getCo2()<=2000) && co2Level < 2000) {
            status = SensorStatus.OK.label;
        }
        else if (co2Level > 2000) {
            status = SensorStatus.WARN.label;
            Map<String, Object> metricsMap = new HashMap<>();
            metricsMap.put("startTime", measurementList.get(0).getTime());
            metricsMap.put("endTime", measurementList.get(measurementList.size()-1).getTime());
            measurementListLast3.forEach(measurement -> metricsMap.put("measurement", measurement.getCo2()));
            alertsMapList.add(metricsMap);
        }
    }
}
