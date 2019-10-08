package com.skcc.cloudzcp.usage.unit;

public class DefaultUnitConverter implements IUnitConverter {

    private String unit;
    
    /**
     * 지수
     */
    private int exponent;
    
    /**
     * 밑
     */
    private int base;
    
    private double divisor;
    
    protected DefaultUnitConverter(String unit, int base, int exponent) {
        this.unit = unit;
        this.base = base;
        this.exponent = exponent;
        
        this.divisor = Math.pow(base, exponent);
    }

    @Override
    public double convert(double originalValue) {
        return originalValue / divisor;
    }

    public String getUnit() {
        return unit;
    }

    public int getExponent() {
        return exponent;
    }

    public int getBase() {
        return base;
    }
    
}
