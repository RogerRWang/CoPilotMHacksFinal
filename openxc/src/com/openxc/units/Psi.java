package com.openxc.units;

/**
 * PSI is pounds per square inch
 */
public class Psi extends Quantity<Number> {
    private final String TYPE_STRING = "PSI";

    public Psi(Number value) {
        super(value);
    }

    @Override
    public String getTypeString() {
        return TYPE_STRING;
    }
}

