package com.bdas;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.bdas.project.AddPrjGroups;
import com.bdas.project.ProjectCreation;
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
    private static String username;
    private static String password;
    private static final String version = "BDAS API Tool v.1.2.1";

    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException, IOException {
        if (args.length == 1 && args[0].equals("-version")) {
            System.out.println(version);
        } else if (args.length == 1 && args[0].equals("-help")) {
            showHelp();
        }
        loadProperties();
        String instance = args[0];

        if (!instance.endsWith("/")) {
            instance = instance + "/";
        }

        if (args.length == 2 && !args[1].equals("-rws")) {
            final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            final URI jiraServerUri = new URI(instance);
            final BasicHttpAuthenticationHandler basicHttpAuthenticationHandler = new BasicHttpAuthenticationHandler(username, password);
            final JiraRestClient restClient = factory.create(jiraServerUri, basicHttpAuthenticationHandler);
            String report = null;

            switch (args[1]) {
                case "-it":
                    report = "IssueTypes";
                    IssueTypesReport issueTypeReport = new IssueTypesReport(restClient, report, jiraServerUri.toString());
                    issueTypeReport.write(issueTypeReport.generateReport());
                    break;
                case "-cf":
                    report = "CustomFields";
                    CustomFieldsReport customFieldsReport = new CustomFieldsReport(restClient, report, jiraServerUri.toString());
                    customFieldsReport.write(customFieldsReport.generateReport());
                    break;
                case "-ws":
                    report = "WorkflowStatuses";
                    WorkflowStatusesReport workflowStatusesReport = new WorkflowStatusesReport(restClient, report, jiraServerUri.toString());
                    workflowStatusesReport.write(workflowStatusesReport.generateReport());
                    break;
                case "-r":
                    report = "Resolutions";
                    ResolutionReport resolutionReport = new ResolutionReport(restClient, report, jiraServerUri.toString());
                    resolutionReport.write(resolutionReport.generateReport());
                    break;
                default:
                    showHelp();
                    break;
            }

        } else if (args.length == 2 && args[1].equals("-rws")) {

            RemWorkflowSchemes rws = new RemWorkflowSchemes(Utils.encodeCredentials(username, password), instance);
            rws.sendRequest();

        } else if (args.length == 3) {

            String project = args[1].trim().toUpperCase();
            String jiraProjectKey = args[2].trim().toUpperCase();
            AddPrjGroups addPrjGroups = new AddPrjGroups(Utils.encodeCredentials(username, password), instance, project, jiraProjectKey);
            addPrjGroups.sendRequest();

        } else if (args.length == 6) {

            String project = args[1].trim().toUpperCase();
            String jiraProjectKey = args[2].trim().toUpperCase();
            String lead = args[3];

            if (lead.length() > 20) lead = lead.substring(0, 20);

            Utils.print("Project creation...");
            ProjectCreation projectCreation = new ProjectCreation(Utils.encodeCredentials(username, password), args[0], jiraProjectKey, lead, args[4], args[5]);
            projectCreation.sendRequest();

            Utils.print(String.format("Adding prj_%s groups...", project));
            AddPrjGroups addPrjGroups = new AddPrjGroups(Utils.encodeCredentials(username, password), instance, project, jiraProjectKey);
            addPrjGroups.sendRequest();
            Utils.print("Done!");

        } else showHelp();
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
        Utils.print("[JIRA URL] -is - generates issue types report;");
        Utils.print("[JIRA URL] -ws - generates workflow statuses report;");
        Utils.print("[JIRA URL] -cf - generates custom fields report;");
        Utils.print("[JIRA URL] -r - generates resolutions report;");
        Utils.print("[JIRA URL] -rws - Delete the passed workflow schemes. Pass them to workflow_scheme_ids.txt file in the same dir as .jar file;");
        Utils.print("[JIRA URL] [project] [JIRA project key] - adding prj_ groups");
        Utils.print("[JIRA URL] [project] [JIRA project key] [JIRA project name] [Lead] [Project Template ID] - project creation and adding prj_ groups");
    }
}


