package io.getint.recruitment_task;

import static io.getint.recruitment_task.ListOfFieldsToCopy.DESCRIPTION_FIELD;
import static io.getint.recruitment_task.ListOfFieldsToCopy.ISSUE_TYPE;
import static io.getint.recruitment_task.ListOfFieldsToCopy.PROJECT_FIELD;
import static io.getint.recruitment_task.ListOfFieldsToCopy.SUMMARY_FIELD;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class IssueCreator {

    private final JiraApiClient jiraApiClient;
    private final String targetProjectKey;
    private static final String ENDPOINT_URL = "/rest/api/3/issue/bulk";

    public IssueCreator (JiraApiClient jiraApiClient, String targetProjectKey) {
        this.jiraApiClient = jiraApiClient;
        this.targetProjectKey = targetProjectKey;
    }

    public void createIssueInTargetProject(JSONArray issues) throws IOException {

        JSONArray issueUpdates = new JSONArray();

        for (int i = 0; i < issues.length(); i++) {
            JSONObject fields = new JSONObject();

            fields.put(PROJECT_FIELD.fieldName, new JSONObject().put("key", targetProjectKey));
            fields.put(SUMMARY_FIELD.fieldName, issues.getJSONObject(i).getJSONObject("fields").getString(SUMMARY_FIELD.fieldName));
            fields.put(DESCRIPTION_FIELD.fieldName, issues.getJSONObject(i).getJSONObject("fields").get(DESCRIPTION_FIELD.fieldName));

            String issueType = issues.getJSONObject(i).getJSONObject("fields").getJSONObject(ISSUE_TYPE.fieldName).getString("name");
            fields.put(ISSUE_TYPE.fieldName, new JSONObject().put("name", issueType));

            JSONObject payload = new JSONObject();
            payload.put("fields", fields);

            issueUpdates.put(payload);
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("issueUpdates", issueUpdates);

        jiraApiClient.post(ENDPOINT_URL, requestBody.toString());
    }
}