package com.example.webhooksqlsolver.service;

import com.example.webhooksqlsolver.dto.WebhookRequest;
import com.example.webhooksqlsolver.dto.WebhookResponse;
import com.example.webhooksqlsolver.dto.SqlSolutionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Service
public class WebhookService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    private static final String WEBHOOK_GENERATE_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String WEBHOOK_SUBMIT_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    
    @Autowired
    private SqlProblemSolver sqlProblemSolver;
    
    private final RestTemplate restTemplate;
    
    public WebhookService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Main method to execute the complete webhook flow
     */
    public void executeWebhookFlow() {
        try {
            // Step 1: Generate webhook
            logger.info("Starting webhook flow...");
            WebhookResponse webhookResponse = generateWebhook();
            
            if (webhookResponse == null || webhookResponse.getWebhook() == null || webhookResponse.getAccessToken() == null) {
                logger.error("Failed to generate webhook - invalid response");
                return;
            }
            
            logger.info("Webhook generated successfully");
            logger.info("Webhook URL: {}", webhookResponse.getWebhook());
            
            // Step 2: Solve SQL problem
            String regNo = "REG12347"; // This matches the registration number used in the request
            String sqlSolution = sqlProblemSolver.solveSqlProblem(regNo);
            logger.info("SQL problem solved for regNo: {}", regNo);
            
            // Step 3: Submit solution
            submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), sqlSolution);
            
            logger.info("Webhook flow completed successfully");
            
        } catch (Exception e) {
            logger.error("Error in webhook flow: ", e);
        }
    }
    
    /**
     * Step 1: Generate webhook by sending POST request
     */
    private WebhookResponse generateWebhook() {
        try {
            WebhookRequest request = new WebhookRequest("John Doe", "REG12347", "john@example.com");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            logger.info("Sending webhook generation request to: {}", WEBHOOK_GENERATE_URL);
            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                WEBHOOK_GENERATE_URL,
                HttpMethod.POST,
                entity,
                WebhookResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Webhook generation successful");
                return response.getBody();
            } else {
                logger.error("Webhook generation failed with status: {}", response.getStatusCode());
                return null;
            }
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("HTTP error during webhook generation: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error during webhook generation: ", e);
            return null;
        }
    }
    
    /**
     * Step 3: Submit SQL solution to the webhook URL
     */
    private void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        try {
            SqlSolutionRequest solutionRequest = new SqlSolutionRequest(sqlQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            
            HttpEntity<SqlSolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);
            
            logger.info("Submitting solution to webhook URL: {}", webhookUrl);
            ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Solution submitted successfully. Response: {}", response.getBody());
            } else {
                logger.error("Solution submission failed with status: {}", response.getStatusCode());
            }
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("HTTP error during solution submission: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Unexpected error during solution submission: ", e);
        }
    }
}