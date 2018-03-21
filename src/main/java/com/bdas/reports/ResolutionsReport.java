package com.bdas.reports;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class ResolutionsReport extends Report {
    public ResolutionsReport(JiraRestClient restClient, String reportName, String instance) {
        super(restClient, reportName, instance);
    }

    @Override
    public Workbook generateReport() throws ExecutionException, InterruptedException {
        setThirdCell(null);
        Workbook workbook = createWorksheet();
        Sheet sheet = workbook.getSheet(getName());

        Iterable<Resolution> resolutions = getRestClient().getMetadataClient().getResolutions().get();
        AtomicInteger rowIndex = new AtomicInteger(2);

        resolutions.forEach(resolution -> {
            Row row = sheet.createRow(rowIndex.get());
            Cell cell = row.createCell(0);
            cell.setCellValue(resolution.getId());

            cell = row.createCell(1);
            cell.setCellValue(resolution.getName());

            rowIndex.getAndIncrement();
        });

        return workbook;
    }
}
