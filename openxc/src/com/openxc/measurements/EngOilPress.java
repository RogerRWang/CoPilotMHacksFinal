package com.openxc.measurements;

import com.openxc.units.Psi;
import com.openxc.util.Range;

/**  
 * Engine Oil Pressure is in PSI
 */
public class EngOilPress extends BaseMeasurement<Psi> {
    private final static Range<Psi> RANGE = new Range<Psi>(
            new Psi(0.0), new Psi(999.0));
    public final static String ID = "OIL_PRESS";

    public EngOilPress (Psi value) {
        super(value, RANGE);
    }

    public EngOilPress (Number value) {
        this(new Psi(value));
       
    }

    @Override
    public String getGenericName() {
        return ID;
    }
}
