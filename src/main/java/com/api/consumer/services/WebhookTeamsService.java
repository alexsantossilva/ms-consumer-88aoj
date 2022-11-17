package com.api.consumer.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookTeamsService {
    private final String url;
    private final String channel;

    public WebhookTeamsService(@Value("${webhook.incoming.teams.url}") String url,
                               @Value("${webhook.incoming.teams.channel}") String channel) {
        this.url = url;
        this.channel = channel;
    }

    private String normalize(String message) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = mapper.readValue(message, Map.class);
        var teamsMessage = map.get("message") + " Nome: " + map.get("firstName") + " " + map.get("lastName") + " - Email: " + map.get("email");
        var values = new HashMap<String, String>() {{
            put("channel", channel);
            put("username", "app_kafka_consumer");
            put("text", teamsMessage);
            put("icon_emoji", ":taxi:");
        }};

        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(values);
    }
    public void sendNotification(String message) throws JsonProcessingException {
        try {
            var requestBody = this.normalize(message);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.url.trim()))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("SUCCEEDED: Sent webhook:" + response.body());
        } catch (Exception e) {
            System.out.println("FAILED: Send webhook: " + e.getMessage());
        }
    }
}
