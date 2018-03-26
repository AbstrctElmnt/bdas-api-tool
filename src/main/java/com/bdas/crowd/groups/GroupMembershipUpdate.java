package com.bdas.crowd.groups;

import com.bdas.RestActions;
import com.bdas.Utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class GroupMembershipUpdate implements RestActions{
    private String basicAuth, instance, group;
    private Set<String> usernames;

    public GroupMembershipUpdate(String basicAuth, String instance, String group) {
        this.basicAuth = basicAuth;
        this.instance = instance;
        this.group = group;
        this.usernames = loadData();
    }

    @Override
    public void sendRequest() {
        Utils.print("Total items for processing: " + usernames.size());

        usernames.forEach((username) -> {
            try {
                Utils.print("Processing user: " + username);
                String crowdUrl = String.format("%srest/usermanagement/latest/group/user/direct?groupname=%s", instance, group);
                String body = String.format("{\"name\":\"%s\"}", username);
                URL url = new URL(crowdUrl);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestProperty("Authorization", basicAuth);
                httpCon.setRequestProperty("Content-Type", "Application/json");
                httpCon.setRequestMethod("POST");
                OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
                out.write(body);
                out.close();
                int status = httpCon.getResponseCode();

                String error = null;
                switch (status) {
                    case 400:
                        error = "Status " + status + ": the user could not be found.";
                        Utils.print(error);
                        break;
                    case 404:
                        error = "Status " + status + ": the group could not be found.";
                        Utils.print(error);
                        break;
                    case 409:
                        error = "Status " + status + ": the user is already a direct member of the group.";
                        Utils.print(error);
                        break;
                    case 201:
                        Utils.print("Status " + status + ": the user is successfully added as a member of the groups.");
                        break;
                    default:
                        Utils.print("Nothing happened. Check settings!");
                        break;
                }
                httpCon.getInputStream();

            } catch (IOException e) {
                    //e.printStackTrace();
                }
        });
        Utils.print("Done.");
    }
}
