package com.bdas.jira.priorities;

import com.bdas.RestActions;
import com.bdas.Utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PrioritySchemeAssignment implements RestActions {
    private String basicAuth, instance, schemeID, projectKey;

    public PrioritySchemeAssignment(String basicAuth, String instance, String schemeID, String projectKey) {
        this.basicAuth = basicAuth;
        this.schemeID = schemeID;
        this.projectKey = projectKey;
        this.instance = instance;
        //PUT /rest/api/2/project/projectKeyOrId/priorityscheme
    }

    @Override
    public void sendRequest() {
        String body = String.format("{\"id\": %s}", schemeID);
        String jiraUrl = String.format("%srest/api/latest/project/%s/priorityscheme", instance, projectKey);

        try {
            URL url = new URL(jiraUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestProperty("Authorization", basicAuth);
            httpCon.setRequestProperty("Content-Type", "Application/json");
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(body);
            out.close();

            int status = httpCon.getResponseCode();

            String error;
            switch (status) {
                case 400:
                    error = "Status " + status + ": the request is not valid and the priority scheme could not be updated. Eg. migration is needed as a result of operation.";
                    Utils.print(error);
                    break;
                case 403:
                    error = "Status " + status + ": the user does not have rights to assign priority schemes.";
                    Utils.print(error);
                    break;
                case 404:
                    error = "Status " + status + ": project or priority scheme is not found.";
                    Utils.print(error);
                    break;
                case 200:
                    Utils.print("Status " + status + ": priority scheme has been updated on " + projectKey + " project.");
                    break;
                default:
                    Utils.print("Nothing happened. Check settings!");
                    break;
            }

            httpCon.getInputStream();

        } catch (IOException e) {
            //e.printStackTrace();
        }


    }

}
