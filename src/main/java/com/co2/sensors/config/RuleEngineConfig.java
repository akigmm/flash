package com.co2.sensors.config;

import com.co2.sensors.rules.AlertsRule;
import com.co2.sensors.rules.MetricsRule;
import com.co2.sensors.rules.StatusRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;

/**
 * This configuration class will be used as source of beans when loading application context using Spring.
 *
 * It will be injected to the rule engine in real time and load all the rules
 *
 */
@Configuration
public class RuleEngineConfig {

    /**
     *
     * @return RulesEngine
     */
    @Bean
    public RulesEngine rulesEngine() {
        return new DefaultRulesEngine();
    }

    /**
     * Registered set of rules.
     *
     * @return Rules
     */
    @Bean
    public Rules rules() {
        Rules rules = new Rules();
        rules.register(new StatusRule());
        rules.register(new MetricsRule());
        rules.register(new AlertsRule());
        return rules;
    }
}
