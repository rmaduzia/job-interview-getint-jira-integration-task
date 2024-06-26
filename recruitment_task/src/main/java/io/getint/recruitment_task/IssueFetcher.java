package io.getint.recruitment_task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class IssueFetcher {
    private final JiraApiClient jiraApiClient;

    private static final String endPointApi = Config.get("jira.api.endpoint");

    public IssueFetcher(JiraApiClient jiraApiClient) {
        this.jiraApiClient = jiraApiClient;
    }

    public JSONArray fetchIssues(String projectKey, int maxResults) throws IOException {

        String fieldsName = ListOfFieldsToCopy.getAllFieldsNames();

        String response = jiraApiClient.get(endPointApi + projectKey + "&maxResults=" + maxResults + fieldsName);
        JSONObject responseJson = new JSONObject(response);
        return responseJson.getJSONArray("issues");
    }
}