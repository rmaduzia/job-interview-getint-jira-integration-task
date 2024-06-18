package io.getint.recruitment_task;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IssueCreatorTest {

    @Mock
    private JiraApiClient mockJiraApiClient;
    private IssueCreator issueCreator;

    @Before
    public void setUp() {
        issueCreator = new IssueCreator(mockJiraApiClient, "TARGET_PROJECT");
    }

    @Test
    public void testCreateIssueInTargetProject() throws IOException {
        JSONArray issues = new JSONArray();
        JSONObject issue = new JSONObject();
        JSONObject fields = new JSONObject();
        fields.put("summary", "Test summary");
        fields.put("issuetype", new JSONObject().put("name", "Task"));
        fields.put("project", new JSONObject().put("key", "TARGET_PROJECT"));
        fields.put("description", "Test description");
        issue.put("fields", fields);
        issues.put(issue);

        issueCreator.createIssueInTargetProject(issues);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> requestBodyCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockJiraApiClient, Mockito.times(1)).post(urlCaptor.capture(), requestBodyCaptor.capture());

        String expectedRequestBody = new JSONObject().put("issueUpdates", issues).toString();
        assertEquals(expectedRequestBody, requestBodyCaptor.getValue());
    }
}