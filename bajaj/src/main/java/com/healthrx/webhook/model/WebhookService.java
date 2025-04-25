package com.healthrx.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.ArrayList;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;

    @Value("${generate.webhook.url}")
    private String generateWebhookUrl;

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void callGenerateWebhook() {
        // Step 1: Make POST request to /generateWebhook to get data
        String requestBody = "{ \"name\": \"John Doe\", \"regNo\": \"REG12347\", \"email\": \"john@example.com\" }";

        try {
            ResponseEntity<GenerateWebhookResponse> response = restTemplate.postForEntity(generateWebhookUrl, requestBody, GenerateWebhookResponse.class);
            GenerateWebhookResponse webhookResponse = response.getBody();
            
            if (webhookResponse != null) {
                // Step 2: Process data based on regNo
                String regNo = "REG12347";  // Use static or dynamic regNo from response
                List<int[]> outcome = processUsers(webhookResponse.getData().getUsers(), regNo);

                // Step 3: Send result to the webhook
                sendResultToWebhook(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), outcome);
            }
        } catch (Exception e) {
            System.err.println("API call failed: " + e.getMessage());
        }
    }

    private List<int[]> processUsers(List<User> users, String regNo) {
        List<int[]> outcome = new ArrayList<>();
        if (Integer.parseInt(regNo.substring(regNo.length() - 1)) % 2 == 0) {
            // For even regNo, perform Nth-Level Followers logic
            outcome = getNthLevelFollowers(users, 2, 1);  // Example: findId=1, n=2
        } else {
            // For odd regNo, perform Mutual Followers logic
            outcome = findMutualFollowers(users);
        }
        return outcome;
    }

    private List<int[]> findMutualFollowers(List<User> users) {
        List<int[]> mutualFollowers = new ArrayList<>();
        for (User user : users) {
            for (Integer followId : user.getFollows()) {
                for (User targetUser : users) {
                    if (targetUser.getId() == followId && targetUser.getFollows().contains(user.getId())) {
                        mutualFollowers.add(new int[]{Math.min(user.getId(), followId), Math.max(user.getId(), followId)});
                    }
                }
            }
        }
        return mutualFollowers;
    }

    private List<int[]> getNthLevelFollowers(List<User> users, int n, int findId) {
        List<int[]> result = new ArrayList<>();
        List<Integer> currentLevelUsers = new ArrayList<>();
        currentLevelUsers.add(findId);

        for (int i = 0; i < n; i++) {
            List<Integer> nextLevelUsers = new ArrayList<>();
            for (Integer userId : currentLevelUsers) {
                User user = findUserById(users, userId);
                if (user != null) {
                    nextLevelUsers.addAll(user.getFollows());
                }
            }
            currentLevelUsers = nextLevelUsers;
        }

        for (Integer userId : currentLevelUsers) {
            result.add(new int[]{userId});
        }
        return result;
    }

    private User findUserById(List<User> users, Integer userId) {
        for (User user : users) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    private void sendResultToWebhook(String webhookUrl, String accessToken, List<int[]> outcome) {
        // Send result to the webhook URL with JWT authentication
        WebhookRequest request = new WebhookRequest(outcome);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        headers.set("Content-Type", "application/json");
        
        HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

        // Retry policy: Retry up to 4 times
        int attempts = 0;
        boolean success = false;
        while (attempts < 4 && !success) {
            try {
                restTemplate.exchange(webhookUrl, HttpMethod.POST, entity, Void.class);
                success = true;  // Success, exit loop
            } catch (Exception e) {
                attempts++;
                if (attempts == 4) {
                    System.err.println("Failed after 4 attempts.");
                }
            }
        }
    }
}
