package com.macedo.moneymanager.utils;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Beatriz on 08/09/2015.
 */
public class AmountFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public AmountFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value) {
        return  "$" + mFormat.format(value);
    }


}
