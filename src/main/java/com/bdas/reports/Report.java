package com.bdas.reports;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public abstract class Report {
    private String reportName, instance;
    private JiraRestClient restClient;
    private String firstCellName = "ID";
    private String secondCellName = "Name";
    private String thirdCellName = null;

    public String getName() {
        return reportName;
    }

    public JiraRestClient getRestClient() {
        return restClient;
    }

    public String getInstance() {
        return instance;
    }

    public void setThirdCell(String thirdCellName) {
        this.thirdCellName = thirdCellName;
    }

    public Report(JiraRestClient restClient, String reportName, String instance) {
        this.reportName = reportName;
        this.restClient = restClient;
        this.instance = instance;
    }

    public abstract Workbook generateReport() throws ExecutionException, InterruptedException;

    Workbook createWorksheet() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(getName());

        Row firstRow = sheet.createRow(0);
        Cell firstCell = firstRow.createCell(0);
        firstCell.setCellValue("Generated from " + getInstance());

        Row header = sheet.createRow(1);
        CellStyle headerStyle = workbook.createCellStyle();

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(firstCellName);
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue(secondCellName);
        headerCell.setCellStyle(headerStyle);

        if (thirdCellName != null) {
            headerCell = header.createCell(2);
            headerCell.setCellValue(thirdCellName);
            headerCell.setCellStyle(headerStyle);
        }
        return workbook;
    }

    public void write(Workbook workbook) throws IOException {

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + reportName + ".xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();

    }
}
