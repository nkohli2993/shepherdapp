package com.shepherd.app.ui.component.vital_stats;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class TypeXAxisValueFormatter implements IAxisValueFormatter {

    private final String[] mValues;

    public TypeXAxisValueFormatter(String[] mValues) {
        this.mValues = mValues;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mValues[Math.round(value)];
    }
}