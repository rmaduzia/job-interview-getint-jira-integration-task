package io.getint.recruitment_task;

import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class IssueFetcher {
    private final JiraApiClient jiraApiClient;

    private static final String endPointApi = Config.get("jira.api.endpoint");

    public IssueFetcher(JiraApiClient jiraApiClient) {
        this.jiraApiClient = jiraApiClient;
    }

    public JSONArray fetchIssues(String projectKey, int maxResults)  {

        String fieldsName = ListOfFieldsToCopy.getAllFieldsNames();
        String response;

        try {
            response = jiraApiClient.get(
                endPointApi + projectKey + "&maxResults=" + maxResults + fieldsName);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            throw new RuntimeException("Issue while fetching tickets");
        }

        JSONObject responseJson = new JSONObject(response);
        return responseJson.getJSONArray("issues");
    }
}