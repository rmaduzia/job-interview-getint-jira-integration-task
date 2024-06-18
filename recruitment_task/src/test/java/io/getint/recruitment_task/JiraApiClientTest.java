package io.getint.recruitment_task;

import java.io.ByteArrayInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JiraApiClientTest {

    @Mock
    private CloseableHttpClient mockClient;
    @Mock
    private CloseableHttpResponse mockResponse;
    @Mock
    private HttpEntity mockEntity;

    private JiraApiClient client;

    @Before
    public void setup() {
        client = new JiraApiClient("http://example.com", "testUser", "testToken", mockClient);
    }

    @Test
    public void testGet() throws IOException {
        String endpoint = "/rest/api/2/issue/TEST-123";
        String expectedResponse = "{\"key\":\"TEST-123\",\"summary\":\"Test issue\"}";

        when(mockClient.execute(any(HttpGet.class))).thenReturn(mockResponse);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockEntity.getContent()).thenReturn(new ByteArrayInputStream(expectedResponse.getBytes()));

        String response = client.get(endpoint);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testPost() throws IOException {
        String endpoint = "/rest/api/2/issue";
        String payload = "{\"fields\":{\"project\":{\"key\":\"TEST\"},\"summary\":\"Test issue\"}}";
        String expectedResponse = "{\"id\":\"123\",\"key\":\"TEST-124\",\"self\":\"http://example.com/rest/api/2/issue/TEST-124\"}";

        when(mockClient.execute(any(HttpPost.class))).thenReturn(mockResponse);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockEntity.getContent()).thenReturn(new ByteArrayInputStream(expectedResponse.getBytes()));
        when(mockEntity.getContent()).thenReturn(new ByteArrayInputStream(expectedResponse.getBytes()));
        when(mockResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 2, 0), 201, "Created"));

        String response = client.post(endpoint, payload);

        assertEquals(expectedResponse, response);
    }
}