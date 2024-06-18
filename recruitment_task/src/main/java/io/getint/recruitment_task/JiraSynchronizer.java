package io.getint.recruitment_task;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;

public class JiraSynchronizer {

    private final IssueFetcher issueFetcher;
    private final IssueCreator issueCreator;

    private static final String JIRA_URL = Config.get("jira.url");
    private static final String USERNAME = Config.get("jira.username");
    private static final String API_TOKEN = Config.get("jira.api.token");
    private static final String SOURCE_PROJECT_KEY = Config.get("jira.source.project.key");
    private static final String TARGET_PROJECT_KEY = Config.get("jira.target.project.key");
    private static final int NUMBER_OF_PROJECTS_TO_MOVE = Integer.parseInt(Config.get("jira.tickets.to-move"));

    public static void main(String[] args) {
        try {

            CloseableHttpClient httpClient = HttpClients.createDefault();
            JiraApiClient jiraApiClient = new JiraApiClient(JIRA_URL, USERNAME, API_TOKEN, httpClient);
            IssueFetcher issueFetcher = new IssueFetcher(jiraApiClient);
            IssueCreator issueCreator = new IssueCreator(jiraApiClient, TARGET_PROJECT_KEY);

            JiraSynchronizer jiraSynchronizer = new JiraSynchronizer(issueFetcher, issueCreator);
            jiraSynchronizer.moveTasksToOtherProject();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JiraSynchronizer(IssueFetcher issueFetcher, IssueCreator issueCreator) {
        this.issueFetcher = issueFetcher;
        this.issueCreator = issueCreator;
    }

    public void moveTasksToOtherProject() throws Exception {
        JSONArray issues = issueFetcher.fetchIssues(SOURCE_PROJECT_KEY, NUMBER_OF_PROJECTS_TO_MOVE);
        issueCreator.createIssueInTargetProject(issues);

        System.out.println("JIRA TICKET CREATED");
    }
}
