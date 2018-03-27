package com.bdas.jira.project;

import com.bdas.RestActions;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PrjGroups implements RestActions {
    private String basicAuth, instance, project, jiraProjectKey;
    private static Map<Integer, String> bodyValues;

    static {
        bodyValues = new HashMap<>(7);
        bodyValues.put(10400, "administrators");
        bodyValues.put(10401, "developers");
        bodyValues.put(10402, "external-users");
        bodyValues.put(10403, "members");
        bodyValues.put(10500, "team-leaders");
        bodyValues.put(10404, "testers");
        bodyValues.put(10405, "users");
    }

    public PrjGroups(String basicAuth, String instance, String project, String jiraProjectKey) {
        this.basicAuth = basicAuth;
        this.instance = instance;
        this.project = project;
        this.jiraProjectKey = jiraProjectKey;
    }

    @Override
    public void sendRequest() {
        bodyValues.forEach((id, role) -> {
            try {
                String body = String.format("{\"group\" : [\"prj_%s-%s\"]}", project, role);
                String jiraUrl = String.format("%srest/api/latest/project/%s/role/%d", instance, jiraProjectKey, id);
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
        });
    }
}
