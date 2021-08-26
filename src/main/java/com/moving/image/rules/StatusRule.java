package com.moving.image.rules;

import com.moving.image.entity.Measurement;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import java.util.*;

@Rule(name = "Sensor Status Rule", description = "alert if the CO2 concentrations reach critical levels")
public class StatusRule {

    @Condition
    public boolean when(@Fact(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST) List<Measurement> measurementListLast3,
                        @Fact(RuleConstants.ATTR_CO2_LEVEL) Integer co2Level) {
        return  co2Level>2000 || measurementListLast3.size()==3;
    }

    @Action
    public void then(@Fact(RuleConstants.ATTR_SENSOR_STATUS_LIST) List<String> sensorStatusList,
                     @Fact(RuleConstants.ATTR_SENSOR_STATUS) String status,
                     @Fact(RuleConstants.ATTR_MEASUREMENT_LAST_3_LIST) List<Measurement> measurementListLast3,
                     @Fact(RuleConstants.ATTR_CO2_LEVEL) Integer co2Level) {

        if(measurementListLast3.size() == 3 && measurementListLast3.stream().allMatch(measurement -> measurement.getCo2()>=2000)) {
            sensorStatusList.add(SensorStatus.ALERT.label);

        }
        else if (status.equalsIgnoreCase(SensorStatus.ALERT.label) && measurementListLast3.size() == 3 &&
                measurementListLast3.stream().allMatch(measurement -> measurement.getCo2()<2000)) {
            sensorStatusList.add(SensorStatus.OK.label);
        }
        else if (co2Level > 2000) {
            sensorStatusList.add(SensorStatus.WARN.label);
        }
    }
}
