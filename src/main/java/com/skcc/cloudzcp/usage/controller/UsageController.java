package com.skcc.cloudzcp.usage.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.skcc.cloudzcp.usage.model.response.MemoryUsageResponse;
import com.skcc.cloudzcp.usage.service.UsageService;
import com.skcc.cloudzcp.usage.view.CsvView;
import com.skcc.cloudzcp.usage.view.ExcelView;

import io.swagger.annotations.Api;

@Api(tags= {"usage"})
@RestController
@RequestMapping("/usage")
public class UsageController {

    private static final Logger logger = LoggerFactory.getLogger(UsageController.class);
    
    @Autowired
    private UsageService usageService;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    
    
    @GetMapping("{namespace}/{podPrefix}/memory")
    public Object create(@PathVariable("namespace") String namespace,
            @PathVariable("podPrefix") String podPrefix,
            @RequestParam(required = true, value="date") String date,
            @RequestParam(required = false, value="unit", defaultValue="Mi") String unit,
            @RequestParam(required = false, value="view", defaultValue="json") String view,
            Model model) throws ParseException {
        logger.info("Getting memory usages of {} {} {} {}", namespace, podPrefix, date, unit);
        
        MemoryUsageResponse usages = usageService.getUsages(namespace, podPrefix, date, unit);
        
        if ("csv".equalsIgnoreCase(view)) {
            model.addAttribute("data", usages);
            ModelAndView mv = new ModelAndView(new CsvView());
            return mv; 
        }
        
        return usages;
    }
    
    @GetMapping("{namespace}/{podPrefix}/memory/excel")
    public Object getExcel(@PathVariable("namespace") String namespace,
            @PathVariable("podPrefix") String podPrefix,
            @RequestParam(required = true, value="date") String date,
            @RequestParam(required = false, value="unit", defaultValue="Mi") String unit,
            Model model) throws ParseException {
        logger.info("Getting memory usages of {} {} {} {}", namespace, podPrefix, date, unit);
        
        Date sDate = sdf.parse(date);
        Calendar s = Calendar.getInstance();
        s.setTime(sDate);
        
        Calendar d = Calendar.getInstance();
        d.set(Calendar.HOUR_OF_DAY, 0);
        d.set(Calendar.MINUTE, 0);
        d.set(Calendar.SECOND, 0);
        d.set(Calendar.MILLISECOND, 0);
        
        List<MemoryUsageResponse> usageList = new ArrayList<MemoryUsageResponse>();
        for (Date current = s.getTime() ; s.before(d) ; s.add(Calendar.DATE, 1), current = s.getTime()) {
            usageList.add(usageService.getUsages(namespace, podPrefix, sdf.format(current), unit));
        }
        
        model.addAttribute("data", usageList);
        ModelAndView mv = new ModelAndView(new ExcelView());
        return mv; 
    }
    
}
