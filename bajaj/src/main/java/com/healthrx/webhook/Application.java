package com.healthrx.webhook;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private WebhookService webhookService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Start the API call on application startup
        webhookService.callGenerateWebhook();
    }
}
