package com.skcc.cloudzcp.usage.view;

import java.io.BufferedOutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.web.servlet.view.AbstractView;

import com.skcc.cloudzcp.usage.model.response.MemoryUsage;
import com.skcc.cloudzcp.usage.model.response.MemoryUsageResponse;
import com.skcc.cloudzcp.usage.model.response.TimeValue;

public class ExcelView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        buildExcelDocument(model, request, response);
    }

    protected void buildExcelDocument(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<MemoryUsageResponse> usageList = (List<MemoryUsageResponse>) model.get("data");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFFont summaryFont = workbook.createFont();
        summaryFont.setFontName("Arial");
        summaryFont.setFontHeightInPoints((short) 10);
        summaryFont.setBold(true);
        HSSFFont summaryRedFont = workbook.createFont();
        summaryRedFont.setFontName("Arial");
        summaryRedFont.setFontHeightInPoints((short) 10);
        summaryRedFont.setBold(true);
        summaryRedFont.setColor(HSSFColor.DARK_RED.index);

        for (MemoryUsageResponse usages : usageList) {
            createSheet(workbook, usages, summaryFont, summaryRedFont);
        }
        String namespace = usageList.get(0).getNamespace();
        
        response.setContentType("application/vnd.ms-excel");
        String headerKey = "Content-Disposition";
        String filename = String.format("%s.xls", namespace);
        String headerValue = String.format("attachment; filename=\"%s\"",
            filename);
        response.setHeader(headerKey, headerValue);

        BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
        
        workbook.write(bos);
    }

    private void createSheet(HSSFWorkbook workbook, MemoryUsageResponse usages, HSSFFont summaryFont, HSSFFont summaryRedFont) {
        String date = usages.getDate().substring(4); // 20200224 -> 0224
        HSSFSheet sheet = workbook.createSheet(date);
        
        List<MemoryUsage> podList = usages.getPodList();
        
        makeHeader(sheet, usages.getDate(), summaryFont);
        
        int rowNum = 1;
        for (MemoryUsage pod : podList) {
            createDataRow(sheet, pod, rowNum++);
        }
        
        
    }
    
    protected void makeHeader(HSSFSheet sheet, String date, HSSFFont summaryFont) {
        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0);
        
        for (int i = 0 ; i < 24 ; i ++) {
            HSSFCell cell = headerRow.createCell(i + 1);
            cell.setCellValue(String.format("%s%02d", date, i));
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        
    }
    
    protected void createDataRow(HSSFSheet sheet, MemoryUsage pod, int rowNum) {
        HSSFRow row = sheet.createRow(rowNum);
        
        HSSFCell podNameCell = row.createCell(0);
        podNameCell.setCellValue(pod.getName());
        
        List<TimeValue> values = pod.getValues();
        TimeValue firstData = values.get(0);
        int index = attachPreEmptyData(row, firstData);
        
        for (TimeValue tv : values) {
            HSSFCell cell = row.createCell(++index);
            cell.setCellValue(tv.getValue());
        }
    }
    
    protected int attachPreEmptyData(HSSFRow row, TimeValue first) {
        String time = first.getTime();
        // yyyyMMddHH
        int firstIndex = Integer.parseInt(time.substring(8));
        
        int index = 0;
        for (index = 0 ; index < firstIndex ; index++) {
            row.createCell(index + 1);
        }
        
        return index;
    }

}
