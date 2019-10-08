package com.skcc.cloudzcp.usage.service;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.OptionalDouble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.skcc.cloudzcp.usage.model.prometheus.ContainerMemoryResponse;
import com.skcc.cloudzcp.usage.model.prometheus.Result;
import com.skcc.cloudzcp.usage.model.response.MemoryUsage;
import com.skcc.cloudzcp.usage.model.response.MemoryUsageResponse;
import com.skcc.cloudzcp.usage.model.response.TimeValue;
import com.skcc.cloudzcp.usage.unit.IUnitConverter;
import com.skcc.cloudzcp.usage.unit.UnitConverterFactory;

@Service
public class UsageService {
    
    private static final Logger logger = LoggerFactory.getLogger(UsageService.class);

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${k8s.prometheusUrl}")
    private String prometheusUrl;
    
    private SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat yyyyMMddHH = new SimpleDateFormat("yyyyMMddHH");
    
    private String queryPath = "/api/v1/query";
    private String queryRangePath = "/api/v1/query_range";
    private String queryTemplate = "sum by(pod_name) (container_memory_working_set_bytes{namespace=\"{NAMESPACE}\",pod_name=~\"{POD_PREFIX}.*\",container_name!=\"POD\",container_name!=\"\"})";
    
    public MemoryUsageResponse getUsages(String namespace, String podPrefix, String dateString, String unit) throws ParseException {
        
        Date date = yyyyMMdd.parse(dateString);
        
        // "/api/v1/query?query=container_memory_working_set_bytes{namespace=\"zcp-system\",pod_name=~\"zcp-jenkins.*\",container_name!=\"POD\",container_name!=\"\"}[1m]"
        String query = queryTemplate.replaceAll("\\{" + "NAMESPACE" + "\\}", namespace)
                .replaceAll("\\{" + "POD_PREFIX" + "\\}", podPrefix);
        

        Date startTime = getStartDateTime(date);
        long start = startTime.getTime();

        MemoryUsageResponse usageResponse = new MemoryUsageResponse();
        IUnitConverter unitConverter = UnitConverterFactory.getConverter(unit);
        usageResponse.setDate(dateString);
        usageResponse.setNamespace(namespace);
        usageResponse.setPodPrefix(podPrefix);
        usageResponse.setUnit(unitConverter.getUnit());
        
        MemoryUsage totalUsage = new MemoryUsage();
        totalUsage.setName("total sum");
        totalUsage.setResourceType("pod_total");
        
        usageResponse.setTotal(totalUsage);
        
        // 하루치를 시간단위로 끊어서 조회해옴.
        for (int i = 0 ; i <= 23; i++) {
            // i 시간만큼 더해서 조회함
            long time = start + (3600 * 1000 * i);
            String dateHour = yyyyMMddHH.format(new Date(time));
            
            ResponseEntity<ContainerMemoryResponse> responseEntity = getRequest(makeUri(query, time));
            ContainerMemoryResponse response = responseEntity.getBody();
            
            List<Result> resultList = response.getData().getResult();
            for (Result result : resultList) {
                TimeValue timeValue = getAverage(result, dateHour, unitConverter);
                String podName = result.getMetric().get("pod_name");
                
                MemoryUsage podMemoryUsage = findExistingMemoryUsage(usageResponse, podName);
                podMemoryUsage.addValue(timeValue);
                
                addValueToTotalSum(totalUsage, i, timeValue);
            }
        }
        
        return usageResponse;
    }
    
    private void addValueToTotalSum(MemoryUsage totalUsage, int i, TimeValue timeValue) {
        List<TimeValue> values = totalUsage.getValues();
        if (values == null) {
            values = new ArrayList<TimeValue>();
            totalUsage.setValues(values);
        }
        
        TimeValue totalValue = null;
        if (values.size() <= i) {
            totalValue = new TimeValue();
            totalValue.setTime(timeValue.getTime());
            totalValue.setValue(0);
            values.add(totalValue);
        }
        totalValue = values.get(i);
        
        totalValue.setValue(totalValue.getValue() + timeValue.getValue());
    }

    /**
     * podName 에 해당하는 MemoryUsage 를 가져온다.
     * 이미 존재하는 경우 해당 객체를 반환하고, 존재하지 않는 경우 신규로 생성한다.
     * 
     * @param response
     * @param podName
     * @return
     */
    private MemoryUsage findExistingMemoryUsage(MemoryUsageResponse response, String podName) {
        List<MemoryUsage> podList = response.getPodList();
        if (podList == null) {
            podList = new ArrayList<MemoryUsage>();
            response.setPodList(podList);
        }
        
        MemoryUsage podMemoryUsage = podList.parallelStream()
            .filter(item -> podName.equals(item.getName()))
            .findAny()
            .orElse(null);

        if (podMemoryUsage != null) {
            return podMemoryUsage;
        }
        
        podMemoryUsage = new MemoryUsage();
        podMemoryUsage.setName(podName);
        podMemoryUsage.setResourceType("Pod");
        
        response.addPodUsage(podMemoryUsage);
        
        return podMemoryUsage;
    }
    
    private Date getStartDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        return calendar.getTime();
    }
    
    private TimeValue getAverage(Result result, String dateHour, IUnitConverter unitConverter) {
        List<List<Long>> values = result.getValues();
        
        OptionalDouble average = values.parallelStream().mapToLong(o -> ((List<Long>) o).get(1)).average();
        
        TimeValue timeValue = new TimeValue();
        timeValue.setTime(dateHour);
        timeValue.setValue(unitConverter.convert(average.getAsDouble()));
        
        return timeValue;
    }
    
    private URI makeUri(String query, long start) {
        
        URI uri = UriComponentsBuilder.fromHttpUrl(prometheusUrl).path(queryRangePath).queryParam("query", query)
                .queryParam("start", start/1000)
                .queryParam("end", (start/1000)+3599)
                .queryParam("step", 30)
                .build().toUri();
        
        return uri;
    }
    
    private ResponseEntity<ContainerMemoryResponse> getRequest(URI uri) {
        logger.debug("request : {}", uri.toString());
        ResponseEntity<ContainerMemoryResponse> forEntity = restTemplate.getForEntity(uri, ContainerMemoryResponse.class);
        
        return forEntity;
    }
    
}
