package com.bdas.reports;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import org.apache.poi.ss.usermodel.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class IssueTypesReport extends Report{

    public IssueTypesReport(JiraRestClient restClient, String reportName, String instance) {
        super(restClient, reportName, instance);
    }

    @Override
    public Workbook generateReport() throws ExecutionException, InterruptedException {
        setThirdCell("Is Sub-Task?");
        Workbook workbook = createWorksheet();
        Sheet sheet = workbook.getSheet(getName());

        Iterable<IssueType> issueTypes = getRestClient().getMetadataClient().getIssueTypes().get();
        AtomicInteger rowIndex = new AtomicInteger(2);

        issueTypes.forEach(issueType -> {
                    Row row = sheet.createRow(rowIndex.get());
                    Cell cell = row.createCell(0);
                    cell.setCellValue(issueType.getId());

                    cell = row.createCell(1);
                    cell.setCellValue(issueType.getName());

                    cell = row.createCell(2);
                    cell.setCellValue(issueType.isSubtask());

                    rowIndex.getAndIncrement();
                });

       return workbook;
    }
}
