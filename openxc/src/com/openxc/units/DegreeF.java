package com.openxc.units;

/**
 * Degree, a unit of temperature in Celsius
 *
 * TODO this shouldn't represent two things.
 */
public class DegreeF extends Quantity<Number> {
    private final String TYPE_STRING = "\u00B0";

    public DegreeF(Number value) {
        super(value);
    }

    @Override
    public String getTypeString() {
        return TYPE_STRING;
    }
}
