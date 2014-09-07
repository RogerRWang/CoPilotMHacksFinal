package com.openxc.measurements;
import com.openxc.units.DegreeF;
import com.openxc.util.Range;

/**  
 * Engine Coolant temp is in degrees F
 */
public class EngCoolTemp extends BaseMeasurement<DegreeF> {
    private final static Range<DegreeF> RANGE = new Range<DegreeF>(
            new DegreeF(0.0), new DegreeF(300.0));
    public final static String ID = "EngCoolTemp";

    public EngCoolTemp(DegreeF value) {
        super(value, RANGE);
    }

    public EngCoolTemp(Number value) {
        this(new DegreeF(value));
       // value = (value *9/5 + 32);
    }

    @Override
    public String getGenericName() {
        return ID;
    }
}


