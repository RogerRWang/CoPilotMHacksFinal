package com.openxc.measurements;

import com.openxc.units.GForce;
import com.openxc.util.Range;

public class LongAccel extends BaseMeasurement<GForce> {
    private final static Range<GForce> RANGE =
            new Range<GForce>(new GForce(0),
                    new GForce(16382));
        public final static String ID = "VehAccel_X";

        public LongAccel(Number value) {
            super(new GForce(value), RANGE);
        }
        public LongAccel(GForce value) {
            super(value, RANGE);
        }

        @Override
        public String getGenericName() {
            return ID;
        }
    }


