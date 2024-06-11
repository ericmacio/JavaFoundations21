package com.eric.calculator;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Calculator {

    public int add(int num1, int num2) {
        return num1 + num2;
    }

    public String calcAnnuity(String R, int t, String r, int n) {
        BigDecimal a = new BigDecimal(r).divide(new BigDecimal(n));
        BigDecimal b = BigDecimal.ONE.add(a);
        BigDecimal c = b.pow(n * t);
        BigDecimal d = c.subtract(BigDecimal.ONE);
        BigDecimal e = d.divide(a);
        BigDecimal f = e.multiply(new BigDecimal(R));
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(f);
    }
}
