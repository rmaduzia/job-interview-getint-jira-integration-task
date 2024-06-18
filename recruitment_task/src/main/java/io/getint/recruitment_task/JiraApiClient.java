package io.getint.recruitment_task;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class JiraApiClient {

    private final String jiraUrl;
    private final String username;
    private final String apiToken;
    private final CloseableHttpClient httpClient;

    public JiraApiClient(String jiraUrl, String username, String apiToken,
        CloseableHttpClient httpClient) {
        this.jiraUrl = jiraUrl;
        this.username = username;
        this.apiToken = apiToken;
        this.httpClient = httpClient;
    }

    public String get(String endpoint) throws IOException {
        HttpGet request = new HttpGet(jiraUrl + endpoint);
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodeCredentials());
        request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

    public String post(String endpoint, String payload) throws IOException {
        HttpPost request = new HttpPost(jiraUrl + endpoint);
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodeCredentials());
        request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        request.setEntity(new StringEntity(payload));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 201) {
                throw new RuntimeException("Failed to create issues in target project: " + EntityUtils.toString(response.getEntity()));
            }
            return EntityUtils.toString(response.getEntity());
        }
    }

    private String encodeCredentials() {
        String auth = username + ":" + apiToken;
        return java.util.Base64.getEncoder().encodeToString(auth.getBytes());
    }
}