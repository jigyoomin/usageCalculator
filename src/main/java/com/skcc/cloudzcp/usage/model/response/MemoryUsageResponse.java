package com.skcc.cloudzcp.usage.model.response;

import java.util.ArrayList;
import java.util.List;

public class MemoryUsageResponse {

    private String namespace;
    private String podPrefix;
    private String unit;
    private String date;
    private MemoryUsage total;
    private List<MemoryUsage> podList;
    
    
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getNamespace() {
        return namespace;
    }
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    public String getPodPrefix() {
        return podPrefix;
    }
    public void setPodPrefix(String podPrefix) {
        this.podPrefix = podPrefix;
    }
    public MemoryUsage getTotal() {
        return total;
    }
    public void setTotal(MemoryUsage total) {
        this.total = total;
    }
    public List<MemoryUsage> getPodList() {
        return podList;
    }
    public void setPodList(List<MemoryUsage> podList) {
        this.podList = podList;
    }

    public void addPodUsage(MemoryUsage podUsage) {
        if (this.podList == null) {
            this.podList = new ArrayList<MemoryUsage>();
        }
        
        podList.add(podUsage);
    }
}
