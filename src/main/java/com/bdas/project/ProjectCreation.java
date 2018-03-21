package com.bdas.project;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProjectCreation {
    private String basicAuth, jiraProjectKey, jiraProjectName, lead, instance,templateId;

    public ProjectCreation(String basicAuth, String instance, String jiraProjectKey, String jiraProjectName, String lead, String templateId) {
        this.basicAuth = basicAuth;
        this.instance = instance;
        this.jiraProjectKey = jiraProjectKey;
        this.jiraProjectName = jiraProjectName;
        this.lead = lead;
        this.templateId = templateId;
    }

    public String createJiraProject() {
        String body = String.format("{\"key\": \"%s\",\"name\": \"%s\", \"lead\": \"%s\"}", jiraProjectKey.toUpperCase(), jiraProjectName, lead);
        String jiraUrl = String.format("%srest/project-templates/1.0/createshared/%s", instance, templateId);
        try {
            URL url = new URL(jiraUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestProperty("Authorization", basicAuth);
            httpCon.setRequestProperty("Content-Type", "Application/json");
            httpCon.setRequestMethod("POST");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(body);
            out.close();
            httpCon.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.format("Created: " + instance + "plugins/servlet/project-config/%s", jiraProjectKey);
    }
}
