package com.openxc.measurements;

import com.openxc.units.DegreeF;
import com.openxc.util.Range;

/**  
 * Ambient Temp is the temperature outside in degrees F
 */
public class AmbientTemp extends BaseMeasurement<DegreeF> {
    private final static Range<DegreeF> RANGE = new Range<DegreeF>(
            new DegreeF(0.0), new DegreeF(120.0));
    public final static String ID = "AMB_TEMP_AVG";

    public AmbientTemp(DegreeF value) {
        super(value, RANGE);
    }

    public AmbientTemp(Number value) {
        this(new DegreeF(value));
       // value = (value *9/5 + 32);
    }

    @Override
    public String getGenericName() {
        return ID;
    }
}
