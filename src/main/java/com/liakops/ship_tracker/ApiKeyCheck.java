package com.liakops.ship_tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyCheck implements CommandLineRunner {

    @Value("${aisstream.api-key}")
    private String apiKey;

    @Override
    public void run(String... args) {
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("AISSTREAM_API_KEY is NOT set");
        } else {
            System.out.println("API key loaded, length = " + apiKey.length());
        }
    }
}
