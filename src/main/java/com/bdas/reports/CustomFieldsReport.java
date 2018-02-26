package com.bdas.reports;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Field;
import org.apache.poi.ss.usermodel.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomFieldsReport extends Report {
    public CustomFieldsReport(JiraRestClient restClient, String reportName, String instance) {
        super(restClient, reportName, instance);
    }

    @Override
    public Workbook generateReport() throws ExecutionException, InterruptedException {
        setThirdCell("Type");
        Workbook workbook = createWorksheet();
        Sheet sheet = workbook.getSheet(getName());

        Iterable<Field> fields = getRestClient().getMetadataClient().getFields().get();
        AtomicInteger rowIndex = new AtomicInteger(2);

        fields.forEach(field -> {
            if (field.getFieldType().name().equals("CUSTOM")) {

                Row row = sheet.createRow(rowIndex.get());
                Cell cell = row.createCell(0);

                cell.setCellValue(field.getSchema().getCustomId());

                cell = row.createCell(1);
                cell.setCellValue(field.getName());

                cell = row.createCell(2);
                cell.setCellValue(field.getSchema().getCustom().substring(field.getSchema().getCustom().indexOf(":")+1));

                rowIndex.getAndIncrement();
            }
        });

        return workbook;
    }
}
