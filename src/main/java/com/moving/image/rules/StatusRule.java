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
    public boolean when(@Fact(RuleConstants.ATTR_MEASUREMENT_LIST) List<Measurement> measurementList,
                        @Fact(RuleConstants.ATTR_CO2_LEVEL) Integer co2Level) {
        return  co2Level>2000 || measurementList.size()>=3;
    }

    @Action
    public void then(@Fact(RuleConstants.ATTR_SENSOR_STATUS) String status,
                     @Fact(RuleConstants.ATTR_MEASUREMENT_LIST) List<Measurement> measurementList,
                     @Fact(RuleConstants.ATTR_CO2_LEVEL) Integer co2Level) {

        if(measurementList.stream().allMatch(measurement -> measurement.getCo2()>=2000))
            status = SensorStatus.ALERT.label;
        else if (status.equalsIgnoreCase(SensorStatus.ALERT.label) && measurementList.stream().allMatch(measurement -> measurement.getCo2()<=2000))
            status = SensorStatus.OK.label;
        else if (co2Level > 2000)
            status = SensorStatus.WARN.label;
    }
}
