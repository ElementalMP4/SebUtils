package main.java.elementalmp4.service;

import main.java.elementalmp4.GlobalConfig;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class SlackService {

    public static void sendPostRequest(String webhookUrl, JSONObject payload) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Unexpected code " + response.statusCode() + " when calling Slack webhook");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean enabled() {
        return GlobalConfigService.getAsBoolean(GlobalConfig.SLACK_INTEGRATION_ENABLED);
    }

    private static String url() {
        return GlobalConfigService.getValue(GlobalConfig.SLACK_WEBHOOK);
    }

    public static void sendJoinMessage(String name) {
        if (enabled()) {
            JSONObject message = new JSONObject();
            message.put("username", name);
            message.put("action", "joined");
            sendPostRequest(url(), message);
        }
    }

    public static void sendLeaveMessage(String name) {
        if (enabled()) {
            JSONObject message = new JSONObject();
            message.put("username", name);
            message.put("action", "left");
            sendPostRequest(url(), message);
        }
    }

}
