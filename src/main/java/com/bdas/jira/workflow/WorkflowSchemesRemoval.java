package com.bdas.jira.workflow;

import com.bdas.RestActions;
import com.bdas.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class WorkflowSchemesRemoval implements RestActions{
    private String basicAuth, instance;
    private Set<String> ids;

    public WorkflowSchemesRemoval(String basicAuth, String instance) {
        this.basicAuth = basicAuth;
        this.instance = instance;
        ids = loadData();
    }

    @Override
    public void sendRequest() {
        Utils.print("Total items for removal: " + ids.size());

        ids.forEach((id) -> {
            try {
                Utils.print("Processing id: " + id);
                String jiraUrl = String.format("%srest/api/latest/workflowscheme/%s", instance, id);
                URL url = new URL(jiraUrl);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestProperty("Authorization", basicAuth);
                httpCon.setRequestProperty("Content-Type", "Application/json");
                httpCon.setRequestMethod("DELETE");

                int status = httpCon.getResponseCode();
                switch (status) {
                    case 400:
                        Utils.print("Status " + status + ": requested scheme is active.");
                        break;
                    case 401:
                        Utils.print("Status " + status + ": there is no user or the user has not entered a websudo session.");
                        break;
                    case 404:
                        Utils.print("Status " + status + ": requested scheme does not exist.");
                        break;
                    case 204:
                        Utils.print("Status " + status + ": the scheme was deleted.");
                        break;
                    default:
                        Utils.print("Nothing happened. Check settings!");
                        break;
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        });
    }
}