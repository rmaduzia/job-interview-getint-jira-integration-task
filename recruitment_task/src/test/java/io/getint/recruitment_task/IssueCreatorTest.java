package io.getint.recruitment_task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void verifyRequestToCreateJiraTickets() throws IOException {

        JSONArray jsonArrayOfIssues = loadJsonExampleData();

        issueCreator.createIssueInTargetProject(jsonArrayOfIssues);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> requestBodyCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockJiraApiClient, Mockito.times(1)).post(urlCaptor.capture(), requestBodyCaptor.capture());

        JSONObject requestBody = new JSONObject(requestBodyCaptor.getValue());
        JSONArray issueUpdates = requestBody.getJSONArray("issueUpdates");

        validateIssueUpdates(issueUpdates);
    }

    private void validateIssueUpdates(JSONArray issueUpdates) {
        String expectedProject = "TARGET_PROJECT";

        List<String> listOfTasksName = List.of("Task 8", "Task 7");
        List<String> listOfTasksDescriptions = List.of("Task 8 description", "task 7 description");
        List<String> ListOfExpectedIssueType = List.of ("Zadanie", "Story");

        for(int i = 0; i < issueUpdates.length(); i++) {

            JSONObject issues = issueUpdates.getJSONObject(i);
            JSONObject fields = new JSONObject(issues.getJSONObject("fields").toString());

            String actualProject = fields.getJSONObject("project").getString("key");
            String actualSummary = fields.getString("summary");
            String actualDescriptionText = extractDescriptionText(fields);
            String actualIssueType = fields.getJSONObject("issuetype").getString("name");

            assertEquals(expectedProject, actualProject);
            assertEquals(listOfTasksName.get(i), actualSummary);
            assertEquals(listOfTasksDescriptions.get(i), actualDescriptionText);
            assertEquals(ListOfExpectedIssueType.get(i), actualIssueType);
        }
    }

    private String extractDescriptionText(JSONObject fields) {
        return fields.getJSONObject("description")
            .getJSONArray("content")
            .getJSONObject(0)
            .getJSONArray("content")
            .getJSONObject(0)
            .getString("text");
    }

    private JSONArray loadJsonExampleData() {

        InputStream inputStream = IssueCreatorTest.class.getClassLoader().getResourceAsStream("exampleJiraApiResponse.json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder jsonStringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }

            return new JSONArray(jsonStringBuilder.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}