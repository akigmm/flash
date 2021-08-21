package com.moving.image.service;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
     * @param facts Facts
     */
    public void invoke(Facts facts) {
        rulesEngine.fire(rules, facts);
    }
}
