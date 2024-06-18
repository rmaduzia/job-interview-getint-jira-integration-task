package io.getint.recruitment_task;

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
    public void testMoveTasksToOtherProject() throws Exception {
        JSONArray issues = new JSONArray();
        Mockito.when(mockIssueFetcher.fetchIssues(Mockito.anyString(), Mockito.anyInt())).thenReturn(issues);

        jiraSynchronizer.moveTasksToOtherProject();

        Mockito.verify(mockIssueFetcher, Mockito.times(1)).fetchIssues(Mockito.anyString(), Mockito.anyInt());
        Mockito.verify(mockIssueCreator, Mockito.times(1)).createIssueInTargetProject(Mockito.any(JSONArray.class));
    }
}