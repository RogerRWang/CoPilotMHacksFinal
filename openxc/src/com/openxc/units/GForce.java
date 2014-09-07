package com.openxc.units;

/**
 * A G is the force applied divided by 9.8m/s2
 */
public class GForce extends Quantity<Number> {
    private final String TYPE_STRING = "G";

    public GForce(Number value) {
        super(value);
    }

    @Override
    public String getTypeString() {
        return TYPE_STRING;
    }
}
