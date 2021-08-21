package com.moving.image.rules;


import java.util.HashMap;
import java.util.Map;

public enum SensorStatus {

    OK("OK"),
    ALERT("ALERT"),
    WARN("WARN");

    /**
     * Map to cache labels and their associated status instances.
     */
    private static final Map<String, SensorStatus> BY_LABEL = new HashMap<>();

    static {
        for (SensorStatus sensorStatus : values()) {
            BY_LABEL.put(sensorStatus.label, sensorStatus);
        }
    }

    /**
     * final variable to store the label.
     */
    public final String label;

    /**
     * constructor that sets the label.
     *
     */
    SensorStatus(String label) {
        this.label = label;
    }

    /**
     * Look up SegmentNames instances by the label field.
     *
     * @param label Label to look up
     * @return The SegmentNames instance with the label, or null if not found.
     */
    public static SensorStatus valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    /**
     * Override the toString() method to return the label instead of declared name;
     *
     * @return String
     */
    @Override
    public String toString() {
        return this.label;
    }
}
