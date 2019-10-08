package com.skcc.cloudzcp.usage.view;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import com.skcc.cloudzcp.usage.model.response.MemoryUsage;
import com.skcc.cloudzcp.usage.model.response.MemoryUsageResponse;
import com.skcc.cloudzcp.usage.model.response.TimeValue;

public class CsvView extends AbstractView {

    private static final String DELEMETER = "|";
    private static final String LINE_SEPERATOR = "\n";
    
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        buildCsvDocument(model, request, response);
    }

    protected void buildCsvDocument(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        MemoryUsageResponse usages = (MemoryUsageResponse) model.get("data");
        
        List<MemoryUsage> podList = usages.getPodList();
        StringBuffer sb = new StringBuffer();
        sb.append(makeHeader(usages.getDate()));
        
        for (MemoryUsage pod : podList) {
            sb.append(convertToCsv(pod)).append(LINE_SEPERATOR);
        }
        response.setContentType("text/csv");
        BufferedWriter bw = new BufferedWriter(response.getWriter());
        bw.write(sb.toString());
        bw.flush();
    }
    
    protected String makeHeader(String date) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("NAME").append(DELEMETER);
        for (int i = 0 ; i < 24 ; i ++) {
            sb.append(String.format("%s%02d", date, i)).append(DELEMETER);
        }
        
        sb.append(LINE_SEPERATOR);
        return sb.toString();
    }
    
    protected String convertToCsv(MemoryUsage pod) {
        StringBuffer sb = new StringBuffer();
        sb.append(pod.getName()).append(DELEMETER);
        List<TimeValue> values = pod.getValues();
        TimeValue firstData = values.get(0);
        attachPreEmptyData(firstData, sb);
        
        for (TimeValue tv : values) {
            sb.append(tv.getValue()).append(DELEMETER);
        }
        
        return sb.toString();
    }
    
    protected void attachPreEmptyData(TimeValue first, StringBuffer sb) {
        String time = first.getTime();
        // yyyyMMddHH
        int firstIndex = Integer.parseInt(time.substring(8));
        
        for (int i = 0 ; i < firstIndex ; i++) {
            sb.append(DELEMETER);
        }
    }

}
