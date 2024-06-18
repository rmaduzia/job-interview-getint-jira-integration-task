package io.getint.recruitment_task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.io.IOException;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class IssueFetcherTest {

    @Mock
    private JiraApiClient mockJiraApiClient;
    private IssueFetcher issueFetcher;

    @Before
    public void setUp() {
        issueFetcher = new IssueFetcher(mockJiraApiClient);
    }

    @Test
    public void testFetchIssues() throws IOException {
        String response = new JSONObject().put("issues", new JSONArray()).toString();
        Mockito.when(mockJiraApiClient.get(Mockito.anyString())).thenReturn(response);

        JSONArray issues = issueFetcher.fetchIssues("PROJECT_KEY", 5);
        assertEquals(0, issues.length());
        Mockito.verify(mockJiraApiClient, Mockito.times(1)).get(Mockito.anyString());
    }
}
