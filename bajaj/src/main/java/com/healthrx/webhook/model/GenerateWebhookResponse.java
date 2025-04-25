package com.healthrx.webhook;

import java.util.List;

public class GenerateWebhookResponse {
    private String webhook;
    private String accessToken;
    private WebhookData data;

    // Getters and Setters
    public WebhookData getData() {
        return data;
    }

    public void setData(WebhookData data) {
        this.data = data;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

class WebhookData {
    private List<User> users;

    // Getters and Setters
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
