package com.skcc.cloudzcp.usage.controller;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skcc.cloudzcp.usage.model.response.MemoryUsageResponse;
import com.skcc.cloudzcp.usage.service.UsageService;

import io.swagger.annotations.Api;

@Api(tags= {"usage"})
@RestController
@RequestMapping("/usage")
public class UsageController {

    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);
    
    @Autowired
    private UsageService usageService;
    
    
    
    @GetMapping("{namespace}/{podPrefix}/memory")
    public MemoryUsageResponse create(@PathVariable("namespace") String namespace,
            @PathVariable("podPrefix") String podPrefix,
            @RequestParam(required = true, value="date") String date,
            @RequestParam(required = false, value="unit", defaultValue="Mi") String unit) throws ParseException {
        logger.info("Getting memory usages of {} {} {} {}", namespace, podPrefix, date, unit);
        
        
        return usageService.getUsages(namespace, podPrefix, date, unit);
    }
    
}
