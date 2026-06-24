package com.mailflowai.response.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value( "${ollama.api.url}" )
    private String ollamaApiUrl;

    @Value( "${ollama.model}" )
    private String ollamaModel;

    public String generateDraftReply(String body, String subject, String category, String senderName) {
        try {

            String prompt = buildDraftPrompt(body, subject, category, senderName);

            // create a JSON object and insert the values
            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("model", ollamaModel);
            requestNode.put("prompt", prompt);
            requestNode.put("stream", false);

            // will create a json string from the object
            String requestJson = objectMapper.writeValueAsString(requestNode);

            // log the message
            log.info("Generating draft reply for subject : {}", subject);

            // create a header object and set content type to application/json , as we will be sending a json file
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    ollamaApiUrl,
                    entity,
                    String.class
            );

            String draft = parseDraftReply(response.getBody());
            log.info("Draft reply generated successfully for subject {}", subject);
            return draft;

        } catch (Exception e) {
            log.error("Error generating draft reply : {}", e.getMessage());
            return "Thank you for your email. We have received your message and will get back to you shortly.";
        }
    }

    public String buildDraftPrompt(String body, String subject, String category, String senderName)
    {
        return String.format("""
        You are a professional email assistant. Write a brief, professional reply to this email.
        
        Sender: %s
        Subject: %s
        Category: %s
        Original Email: %s
        
        Write ONLY the reply body, no subject line, no greeting header.
        Keep it under 100 words. Be professional and helpful.
        """, senderName, subject, category, body != null ? body.substring(0, Math.min(body.length(), 200)) : ""
        );
    }

    String parseDraftReply(String response)
    {
        try{
            var jsonNode = objectMapper.readTree(response);
            return jsonNode.path("response").asText().trim();
        }
        catch(Exception e) {

            log.error("Error parsing Ollama response: {}", e.getMessage());
            return "Thank you for your email. We will response shortly.";
        }

    }
}
