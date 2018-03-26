package com.bdas.jira.reports;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Status;
import org.apache.poi.ss.usermodel.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkflowStatusesReport extends Report{

    public WorkflowStatusesReport(JiraRestClient restClient, String reportName, String instance) {
        super(restClient, reportName, instance);
    }

    @Override
    public Workbook generateReport() throws ExecutionException, InterruptedException {
        setThirdCell(null);
        Workbook workbook = createWorksheet();
        Sheet sheet = workbook.getSheet(getName());

        Iterable<Status> statuses = getRestClient().getMetadataClient().getStatuses().get();
        AtomicInteger rowIndex = new AtomicInteger(2);

        statuses.forEach((status) -> {

            Row row = sheet.createRow(rowIndex.get());
            Cell cell = row.createCell(0);
            cell.setCellValue(status.getId());

            cell = row.createCell(1);
            cell.setCellValue(status.getName());

            rowIndex.getAndIncrement();

        });

        return workbook;
    }
}
