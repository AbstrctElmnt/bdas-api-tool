package com.bdas;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.bdas.crowd.groups.GroupMembershipUpdate;
import com.bdas.jira.priorities.PrioritySchemeAssignment;
import com.bdas.jira.project.PrjGroups;
import com.bdas.jira.project.ProjectCreation;
import com.bdas.jira.reports.CustomFieldsReport;
import com.bdas.jira.reports.IssueTypesReport;
import com.bdas.jira.reports.ResolutionsReport;
import com.bdas.jira.reports.WorkflowStatusesReport;
import com.bdas.jira.workflow.WorkflowSchemesRemoval;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Solution {
    private static String username, password, crowdApplicationUser, crowdApplicationPassword;
    private static final String VERSION = "BDAS API Tool v.1.3.1";


    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException, IOException {

        loadProperties();
        String instance = args[0];

        if (!instance.endsWith("/")) {
            instance = instance + "/";
        }

        if (args.length == 1 && args[0].equals("-version")) {
            Utils.print(VERSION);

        } else if (args.length == 1 && args[0].equals("-help")) {
            showHelp();

        } else if (args.length == 2) {
            final URI jiraServerUri = new URI(instance);
            final JiraRestClient restClient = Utils.getJiraRestClient(instance,username, password);

            String report;

            switch (args[1]) {

                //generate issue types report
                case "-it":
                    report = "IssueTypes";
                    IssueTypesReport issueTypeReport = new IssueTypesReport(restClient, report, jiraServerUri.toString());
                    issueTypeReport.write(issueTypeReport.generateReport());
                    break;

                //generate custom fields report
                case "-cf":
                    report = "CustomFields";
                    CustomFieldsReport customFieldsReport = new CustomFieldsReport(restClient, report, jiraServerUri.toString());
                    customFieldsReport.write(customFieldsReport.generateReport());
                    break;

                //generate workflow statuses report
                case "-ws":
                    report = "WorkflowStatuses";
                    WorkflowStatusesReport workflowStatusesReport = new WorkflowStatusesReport(restClient, report, jiraServerUri.toString());
                    workflowStatusesReport.write(workflowStatusesReport.generateReport());
                    break;

                //generate resolution report
                case "-r":
                    report = "Resolutions";
                    ResolutionsReport resolutionReport = new ResolutionsReport(restClient, report, jiraServerUri.toString());
                    resolutionReport.write(resolutionReport.generateReport());
                    break;

                //remove workflow schemes
                case "-rws":
                    WorkflowSchemesRemoval rws = new WorkflowSchemesRemoval(Utils.encodeCredentials(username, password), instance);
                    rws.sendRequest();
                    break;

                default:
                    showHelp();
                    break;
            }

        } else if (args.length == 3) {
            switch (args[2]) {

                //update group membership in crowd
                case "-gmu":
                    GroupMembershipUpdate groupMembershipUpdate = new GroupMembershipUpdate(Utils.encodeCredentials(crowdApplicationUser, crowdApplicationPassword), instance, args[1]);
                    groupMembershipUpdate.sendRequest();
                    break;

                //assign certain priority scheme to ALL JIRA projects
                case "-psa":
                    Iterable<BasicProject> projects = Utils.getJiraRestClient(instance,username, password).getProjectClient().getAllProjects().get();
                    final String JIRA_INSTANCE = instance;
                    projects.forEach((project) -> new PrioritySchemeAssignment(Utils.encodeCredentials(username, password), JIRA_INSTANCE, args[1], project.getKey()).sendRequest());
                    break;

                //add prj_ groups to the project
                default:
                    String project = args[1].trim().toUpperCase();
                    String jiraProjectKey = args[2].trim().toUpperCase();
                    PrjGroups addPrjGroups = new PrjGroups(Utils.encodeCredentials(username, password), instance, project, jiraProjectKey);
                    addPrjGroups.sendRequest();
                }

        //create JIRA project and add prj_ groups
        } else if (args.length == 6) {

            String project = args[1].trim().toUpperCase();
            String jiraProjectKey = args[2].trim().toUpperCase();
            String jiraProjectName = args[3].trim();

            String lead = args[4];
            if (lead.length() > 20) lead = lead.substring(0, 20);

            Utils.print("Project creation...");
            ProjectCreation projectCreation = new ProjectCreation(Utils.encodeCredentials(username, password), args[0], jiraProjectKey, jiraProjectName, lead, args[5]);
            projectCreation.sendRequest();

            Utils.print(String.format("Adding prj_%s groups...", project));
            PrjGroups addPrjGroups = new PrjGroups(Utils.encodeCredentials(username, password), instance, project, jiraProjectKey);
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
        crowdApplicationUser = prop.getProperty("crowd.application.user");
        crowdApplicationPassword = prop.getProperty("crowd.application.password");
    }

    private static void showHelp() {
        Utils.print("Command not found. Available commands:");
        Utils.print("-help - shows help (current message);");
        Utils.print("-version - shows actual version of the tool;");
        Utils.print("[JIRA URL] -is - generates issue types report;");
        Utils.print("[JIRA URL] -ws - generates workflow statuses report;");
        Utils.print("[JIRA URL] -cf - generates custom fields report;");
        Utils.print("[JIRA URL] -r - generates resolutions report;");
        Utils.print("[JIRA URL] -rws - Delete the passed workflow schemes. Pass them to input.txt file in the same dir as .jar file;");
        Utils.print("[JIRA URL] [project] [JIRA project key] - adding prj_ groups");
        Utils.print("[JIRA URL] [project] [JIRA project key] [JIRA project name] [Lead] [Project Template ID] - project creation and adding prj_ groups;");
        Utils.print("[JIRA URL] [priority scheme id] -psa - assign priority scheme to ALL JIRA projects.");
        Utils.print("[CROWD URL] [group name] -gmu - add usernames from input.txt file (should be located in the same dir as .jar file);");
    }
}