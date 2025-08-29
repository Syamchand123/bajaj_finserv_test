package com.example.webhooksqlsolver.config;

import com.example.webhooksqlsolver.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(StartupConfig.class);
    
    @Autowired
    private WebhookService webhookService;
    
    @Bean
    public ApplicationRunner executeOnStartup() {
        return args -> {
            logger.info("Application started - executing webhook flow...");
            webhookService.executeWebhookFlow();
        };
    }
}