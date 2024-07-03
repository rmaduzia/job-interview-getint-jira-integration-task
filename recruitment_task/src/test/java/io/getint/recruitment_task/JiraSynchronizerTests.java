package io.getint.recruitment_task;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.json.JSONArray;
import org.junit.Before;
import org.mockito.Mockito;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JiraSynchronizerTests {

    private IssueFetcher mockIssueFetcher;
    private IssueCreator mockIssueCreator;
    private JiraSynchronizer jiraSynchronizer;

    @Before
    public void setUp() {
        mockIssueFetcher = Mockito.mock(IssueFetcher.class);
        mockIssueCreator = Mockito.mock(IssueCreator.class);

        jiraSynchronizer = new JiraSynchronizer(mockIssueFetcher, mockIssueCreator);
    }

    @Test
    public void testMoveTasksToOtherProject() {
        JSONArray issues = new JSONArray();

        String firstIssue = "{\"id\":1,\"title\":\"Issue 1\"}";

        issues.put(new JSONObject(firstIssue));

        Mockito.when(mockIssueFetcher.fetchIssues(Mockito.anyString(), Mockito.anyInt())).thenReturn(issues);

        jiraSynchronizer.moveTasksToOtherProject();

        Mockito.verify(mockIssueFetcher, Mockito.times(1)).fetchIssues(Mockito.anyString(), Mockito.anyInt());
        Mockito.verify(mockIssueCreator, Mockito.times(1)).createIssueInTargetProject(Mockito.any(JSONArray.class));
    }


    @Test
    public void testMoveFiveTasksToOtherProject() {
        JSONArray issues = new JSONArray();

        String firstIssue = "{\"id\":1,\"title\":\"Issue 1\"}";
        String secondIssue = "{\"id\":2,\"title\":\"Issue 2\"}";
        String thirdIssue = "{\"id\":3,\"title\":\"Issue 3\"}";
        String fourthIssue = "{\"id\":4,\"title\":\"Issue 4\"}";
        String fifthIssue = "{\"id\":5,\"title\":\"Issue 5\"}";

        issues.put(new JSONObject(firstIssue));
        issues.put(new JSONObject(secondIssue));
        issues.put(new JSONObject(thirdIssue));
        issues.put(new JSONObject(fourthIssue));
        issues.put(new JSONObject(fifthIssue));

        Mockito.when(mockIssueFetcher.fetchIssues(Mockito.anyString(), Mockito.anyInt())).thenReturn(issues);

        jiraSynchronizer.moveTasksToOtherProject();

        Mockito.verify(mockIssueFetcher, Mockito.times(1)).fetchIssues(Mockito.anyString(), Mockito.anyInt());
        Mockito.verify(mockIssueCreator, Mockito.times(3)).createIssueInTargetProject(Mockito.any(JSONArray.class));
    }

    @Test
    public void testShouldSplitIssues() {

        JSONArray issues = new JSONArray();

        String firstIssue = "{\"id\":1,\"title\":\"Issue 1\"}";
        String secondIssue = "{\"id\":2,\"title\":\"Issue 2\"}";
        String thirdIssue = "{\"id\":3,\"title\":\"Issue 3\"}";
        String fourthIssue = "{\"id\":4,\"title\":\"Issue 4\"}";
        String fifthIssue = "{\"id\":5,\"title\":\"Issue 5\"}";

        issues.put(new JSONObject(firstIssue));
        issues.put(new JSONObject(secondIssue));
        issues.put(new JSONObject(thirdIssue));
        issues.put(new JSONObject(fourthIssue));
        issues.put(new JSONObject(fifthIssue));

        List<JSONArray> chunks = jiraSynchronizer.splitIntoChunks(issues);

        assertEquals(3, chunks.size());

        assertEquals(chunks.get(0).toString(), "[" + String.join(",", firstIssue,fourthIssue) + "]");
        assertEquals(chunks.get(1).toString(), "[" + String.join(",", secondIssue,fifthIssue) + "]");
        assertEquals(chunks.get(2).toString(), "[" + thirdIssue + "]");
    }
}