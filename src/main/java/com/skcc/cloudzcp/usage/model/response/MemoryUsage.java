package com.skcc.cloudzcp.usage.model.response;

import java.util.ArrayList;
import java.util.List;

public class MemoryUsage {

    private String resourceType;
    private String name;
    private List<TimeValue> values;
    
    public String getResourceType() {
        return resourceType;
    }
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<TimeValue> getValues() {
        return values;
    }
    public void setValues(List<TimeValue> values) {
        this.values = values;
    }
    
    public void addValue(TimeValue timeValue) {
        if (this.values == null) {
            this.values = new ArrayList<TimeValue>();
        }
        
        values.add(timeValue);
    }
    
}
