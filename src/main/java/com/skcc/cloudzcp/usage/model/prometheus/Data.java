package com.skcc.cloudzcp.usage.model.prometheus;

import java.util.List;

public class Data {

    private String resultType;
    
    private List<Result> result;
    
    public String getResultType() {
        return resultType;
    }
    
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
    
    public List<Result> getResult() {
        return result;
    }
    
    public void setResult(List<Result> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Data [resultType=" + resultType + ", result=" + result + "]";
    }
    
}
