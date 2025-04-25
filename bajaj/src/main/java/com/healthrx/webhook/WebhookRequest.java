package com.healthrx.webhook;

import java.util.List;

public class WebhookRequest {
    private List<int[]> outcome;

    // Constructor
    public WebhookRequest(List<int[]> outcome) {
        this.outcome = outcome;
    }

    // Getters and Setters
    public List<int[]> getOutcome() {
        return outcome;
    }

    public void setOutcome(List<int[]> outcome) {
        this.outcome = outcome;
    }
}
