package io.getint.recruitment_task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private static final int NUMBER_OF_CREATION_THREADS = 2;

    public static void main(String[] args) {
        try {

            CloseableHttpClient httpClient = HttpClients.createDefault();
            JiraApiClient jiraApiClient = new JiraApiClient(JIRA_URL, USERNAME, API_TOKEN, httpClient);
            IssueFetcher issueFetcher = new IssueFetcher(jiraApiClient);
            IssueCreator issueCreator = new IssueCreator(jiraApiClient, TARGET_PROJECT_KEY);

            JiraSynchronizer jiraSynchronizer = new JiraSynchronizer(issueFetcher, issueCreator);
            jiraSynchronizer.moveTasksToOtherProject();

        } catch (Exception e) {
            System.out.println("Issue happened, check logs");
            e.printStackTrace();
        }
    }

    public JiraSynchronizer(IssueFetcher issueFetcher, IssueCreator issueCreator) {
        this.issueFetcher = issueFetcher;
        this.issueCreator = issueCreator;
    }

    public void moveTasksToOtherProject() {

        CompletableFuture.supplyAsync(() -> {
                try {
                    return issueFetcher.fetchIssues(SOURCE_PROJECT_KEY, NUMBER_OF_PROJECTS_TO_MOVE);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to fetch issues", e);
                }
            })
            .thenApply(this::splitIntoChunks)
            .thenComposeAsync(issueChunks -> {
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CREATION_THREADS);

                for (JSONArray chunk : issueChunks) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            issueCreator.createIssueInTargetProject(chunk);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, executorService);
                    futures.add(future);
                }

                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                return allFutures.whenComplete((result, throwable) -> executorService.shutdown());
            })
            .join();

        System.out.println("JIRA TICKET CREATED");
    }

    private List<JSONArray> splitIntoChunks(JSONArray issues) {
        List<JSONArray> chunks = new ArrayList<>();
        int totalIssues = issues.length();
        int startIndex = 0;

        while (startIndex < totalIssues) {
            int endIndex = Math.min(startIndex + 4, totalIssues);

            JSONArray chunk = new JSONArray();
            for (int i = startIndex; i < endIndex; i++) {
                chunk.put(issues.get(i));
            }

            chunks.add(chunk);
            startIndex = endIndex;
        }

        return chunks;
    }
}
