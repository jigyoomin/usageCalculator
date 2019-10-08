package com.skcc.cloudzcp.usage.model.prometheus;

import java.util.List;
import java.util.Map;

public class Result {

    private Map<String, String> metric;
    
    private List<List<Long>> values;

    public Map<String, String> getMetric() {
        return metric;
    }

    public void setMetric(Map<String, String> metric) {
        this.metric = metric;
    }

    public List<List<Long>> getValues() {
        return values;
    }

    public void setValues(List<List<Long>> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Result [metric=" + metric + ", values=" + values + "]";
    }
 
}
