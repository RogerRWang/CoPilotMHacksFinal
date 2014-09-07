package com.openxc.measurements;

import com.openxc.units.Boolean;

/**
 * The BrakePedalStatus measurement knows if the brake pedal is pressed.
 */
public class TurnSignalRightStatus extends BaseMeasurement<Boolean> {
    public final static String ID = "TurnInd_RT_ON";

    public TurnSignalRightStatus(Boolean value) {
        super(value);
    }

    public TurnSignalRightStatus(java.lang.Boolean value) {
        this(new Boolean(value));
    }

    @Override
    public String getGenericName() {
        return ID;
    }
}