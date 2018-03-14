package com.bdas.workflow;

import com.bdas.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RemWorkflowSchemes {
    private String basicAuth, instance;
    private List<String> ids;
    private final static String path = "./workflow_scheme_ids.txt";

    public RemWorkflowSchemes(String username, String password, String instance) {
        this.basicAuth = basicAuth;
        this.instance = instance;
        basicAuth = "Basic " + Utils.encodeCredentials(username, password);
        ids = new ArrayList<>();
    }

    public void removeWorkflowSchemes() {
        loadIds(ids);
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
                    case 400: Utils.print("Status "+ status + ": requested scheme is active.");
                    break;
                    case 401: Utils.print("Status "+ status + ": there is no user or the user has not entered a websudo session.");
                    break;
                    case 404: Utils.print("Status "+ status + ": requested scheme does not exist.");
                    break;
                    default: Utils.print("Status "+ status + ": the scheme was deleted.");
                    break;
                }

            } catch (IOException e) {
                //e.printStackTrace();
            }
        });
    }

    private void loadIds(List<String> list) {
        try {
            String idLine = null;
            FileInputStream fis = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((idLine = reader.readLine()) != null) {
                list.add(idLine.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
