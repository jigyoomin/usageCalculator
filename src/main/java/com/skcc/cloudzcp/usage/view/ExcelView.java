package com.skcc.cloudzcp.usage.view;

import java.io.BufferedOutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
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
        
        HSSFFont summaryFont = createSummaryFont(workbook);
        HSSFFont summaryRedFont = createSummaryFont(workbook);
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
    
    private HSSFFont createSummaryFont(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        
        return font;
    }

    private void createSheet(HSSFWorkbook workbook, MemoryUsageResponse usages, HSSFFont summaryFont, HSSFFont summaryRedFont) {
        String date = usages.getDate().substring(4); // 20200224 -> 0224
        List<MemoryUsage> podList = usages.getPodList();
        if (podList == null) {
        	System.out.println("##########################################");
        	System.out.println("Pod List is empty : " + usages.getDate());
        	System.out.println("##########################################");
        	return;
        }
        
        HSSFSheet sheet = workbook.createSheet(date);
        
        makeHeader(sheet, usages.getDate(), summaryFont);
        
        int rowNum = 1;
        for (MemoryUsage pod : podList) {
            createDataRow(sheet, pod, summaryFont, rowNum++);
        }
        
        createSummaryRow(sheet, summaryFont, summaryRedFont, rowNum);
        
        int lastCellNum = sheet.getRow(0).getLastCellNum();
        for (int i = 0 ; i <= lastCellNum ; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    protected void makeHeader(HSSFSheet sheet, String date, HSSFFont summaryFont) {
        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0);
        
        HSSFCell aveCell = headerRow.createCell(1);
        HSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setFont(summaryFont);
        aveCell.setCellStyle(style);
        aveCell.setCellValue("Average MEM (MB)");
        
        for (int i = 0 ; i < 24 ; i ++) {
            HSSFCell cell = headerRow.createCell(i + 2);
            cell.setCellValue(Integer.parseInt(String.format("%s%02d", date, i)));
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        }
    }
    
    protected void createDataRow(HSSFSheet sheet, MemoryUsage pod, HSSFFont summaryFont, int rowNum) {
        HSSFRow row = sheet.createRow(rowNum);
        
        HSSFCell podNameCell = row.createCell(0);
        podNameCell.setCellValue(pod.getName());
        
        HSSFCell aveCell = row.createCell(1);
        HSSFCellStyle style = sheet.getWorkbook().createCellStyle();
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setFont(summaryFont);
        aveCell.setCellStyle(style);
        aveCell.setCellFormula(String.format("AVERAGE(C%d:Z%d)", rowNum + 1, rowNum + 1));
        
        List<TimeValue> values = pod.getValues();
        TimeValue firstData = values.get(0);
        int index = attachPreEmptyData(row, firstData);
        
        index++;
        
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
            row.createCell(index + 2);
        }
        
        return index;
    }

    private void createSummaryRow(HSSFSheet sheet, HSSFFont summaryFont, HSSFFont summaryRedFont, int rowNum) {
        HSSFRow row = sheet.createRow(rowNum);
        
        HSSFCell nameCell = row.createCell(0);
        HSSFCellStyle nameStyle = sheet.getWorkbook().createCellStyle();
        nameStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        nameStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        nameStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        nameStyle.setFont(summaryFont);
        nameCell.setCellStyle(nameStyle);
        nameCell.setCellValue("총 메모리 사용량 (GB)");
        
        HSSFCell sumCell = row.createCell(1);
        HSSFCellStyle sumStyle = sheet.getWorkbook().createCellStyle();
        sumStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        sumStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sumStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        sumStyle.setFont(summaryRedFont);
        sumCell.setCellStyle(sumStyle);
        sumCell.setCellFormula(String.format("ROUND(SUM(B2:B%d)/1024,2)", rowNum));        
    }
    
}
