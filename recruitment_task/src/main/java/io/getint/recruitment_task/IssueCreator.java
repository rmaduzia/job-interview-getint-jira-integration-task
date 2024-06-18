package io.getint.recruitment_task;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class IssueCreator {

    private final JiraApiClient jiraApiClient;
    private final String targetProjectKey;

    private static final String PROJECT_FIELD = "project";
    private static final String SUMMARY_FIELD = "summary";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String ISSUE_TYPE = "issuetype";
    private static final String ENDPOINT_URL = "/rest/api/3/issue/bulk";

    public IssueCreator (JiraApiClient jiraApiClient, String targetProjectKey) {
        this.jiraApiClient = jiraApiClient;
        this.targetProjectKey = targetProjectKey;
    }

    public void createIssueInTargetProject(JSONArray issues) throws IOException {

        JSONArray issueUpdates = new JSONArray();

        for (int i = 0; i < issues.length(); i++) {
            JSONObject fields = new JSONObject();

            fields.put(PROJECT_FIELD, new JSONObject().put("key", targetProjectKey));
            fields.put(SUMMARY_FIELD, issues.getJSONObject(i).getJSONObject("fields").getString(SUMMARY_FIELD));
            fields.put(DESCRIPTION_FIELD, issues.getJSONObject(i).getJSONObject("fields").get(DESCRIPTION_FIELD));

            String issueType = issues.getJSONObject(i).getJSONObject("fields").getJSONObject(ISSUE_TYPE).getString("name");
            fields.put(ISSUE_TYPE, new JSONObject().put("name", issueType));

            JSONObject payload = new JSONObject();
            payload.put("fields", fields);

            issueUpdates.put(payload);
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("issueUpdates", issueUpdates);

        jiraApiClient.post(ENDPOINT_URL, requestBody.toString());
    }
}