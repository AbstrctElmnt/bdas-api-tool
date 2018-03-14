package com.bdas;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.bdas.prj.AddPrjGroups;
import com.bdas.reports.CustomFieldsReport;
import com.bdas.reports.IssueTypesReport;
import com.bdas.reports.ResolutionReport;
import com.bdas.reports.WorkflowStatusesReport;
import com.bdas.workflow.RemWorkflowSchemes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Solution {
    private static String username, password, instance, report;
    private static String version = "BDAS API Tool v.1.2";

    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException, IOException {
        if (args.length == 1 && args[0].equals("-version")) {
            System.out.println(version);
        } else if (args.length == 1 && args[0].equals("-help")) {
            showHelp();
        }

        loadProperties();
        instance = args[0];

        if (!instance.endsWith("/")) {
            instance = instance + "/";
        }

        if (args.length == 2 && !args[1].equals("-rws")) {
            final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            final URI jiraServerUri = new URI(instance);
            final BasicHttpAuthenticationHandler basicHttpAuthenticationHandler = new BasicHttpAuthenticationHandler(username, password);
            final JiraRestClient restClient = factory.create(jiraServerUri, basicHttpAuthenticationHandler);

            if (args[1].equals("-it")) {
                report = "IssueTypes";
                IssueTypesReport issueTypeReport = new IssueTypesReport(restClient, report, jiraServerUri.toString());
                issueTypeReport.write(issueTypeReport.generateReport());
            } else if (args[1].equals("-cf")) {
                report = "CustomFields";
                CustomFieldsReport customFieldsReport = new CustomFieldsReport(restClient, report, jiraServerUri.toString());
                customFieldsReport.write(customFieldsReport.generateReport());
            } else if (args[1].equals("-ws")) {
                report = "WorkflowStatuses";
                WorkflowStatusesReport workflowStatusesReport = new WorkflowStatusesReport(restClient, report, jiraServerUri.toString());
                workflowStatusesReport.write(workflowStatusesReport.generateReport());
            } else if (args[1].equals("-r")) {
                report = "Resolutions";
                ResolutionReport resolutionReport = new ResolutionReport(restClient, report, jiraServerUri.toString());
                resolutionReport.write(resolutionReport.generateReport());
            } else showHelp();
        }

        else if (args.length == 2 && args[1].equals("-rws")) {
            RemWorkflowSchemes rws = new RemWorkflowSchemes(username, password, instance);
            rws.removeWorkflowSchemes();
        }

        else if (args.length == 3) {
            String jiraProjectKey = args[1].trim().toUpperCase();
            String project = args[2].trim().toUpperCase();
            AddPrjGroups addPrjGroups = new AddPrjGroups(username, password, instance);
            addPrjGroups.add(jiraProjectKey, project);
        }
    }

    private static void loadProperties() {
        Properties prop = new Properties();
        try {
            prop.load(Solution.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            System.out.println("Unable to find config.properties");
            e.printStackTrace();
        }

        username = prop.getProperty("username");
        password = prop.getProperty("password");
    }

    private static void showHelp() {
        Utils.print("Command not found. Available commands:");
        Utils.print("-help - shows help (current message);");
        Utils.print("-version - shows actual version of the tool;");
        Utils.print("[JIRA link] -is - generates issue types report;");
        Utils.print("[JIRA link] -ws - generates workflow statuses report;");
        Utils.print("[JIRA link] -cf - generates custom fields report;");
        Utils.print("[JIRA link] -r - generates resolutions report;");
        Utils.print("[JIRA link] [JIRA project key] [project] - adding of prj_ groups");
        Utils.print("[JIRA link] -rws - Delete the passed workflow schemes. Pass them to workflow_scheme_ids.txt file in the same dir as .jar file;");
    }
}


