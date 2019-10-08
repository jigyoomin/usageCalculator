package com.skcc.cloudzcp.usage.unit;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * 단위 변환기 factory class
 */
@Component
public class UnitConverterFactory {

    private static Map<String, IUnitConverter> converters;
    
    /**
     * 맞는 단위가 없을 경우 변환하지 않고 그대로 내보내는 용도
     */
    private static IUnitConverter nullConverter = new DefaultUnitConverter("byte", 1, 1);
    
    public UnitConverterFactory() {
        init();
    }
    
    private void init() {
        converters = new HashMap<String, IUnitConverter>();
        
        addConverter(new DefaultUnitConverter("k", 1000, 1));
        addConverter(new DefaultUnitConverter("m", 1000, 2));
        addConverter(new DefaultUnitConverter("g", 1000, 3));
        addConverter(new DefaultUnitConverter("t", 1000, 4));
        addConverter(new DefaultUnitConverter("ki", 1024, 1));
        addConverter(new DefaultUnitConverter("mi", 1024, 2));
        addConverter(new DefaultUnitConverter("gi", 1024, 3));
        addConverter(new DefaultUnitConverter("ti", 1024, 4));
    }
    
    private void addConverter(IUnitConverter converter) {
        converters.put(converter.getUnit(), converter);
    }
    
    public static IUnitConverter getConverter(String unit) {
        if (unit == null) {
            return nullConverter;
        }
        
        IUnitConverter unitConverter = converters.get(unit.toLowerCase());
        if (unitConverter != null) {
            return unitConverter;
        }
        
        return nullConverter;
    }
}
