package com.mailflowai.classification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
@Slf4j
public class ClaudeApiService {

    // @Value("${claude.api.key}")
    //private String apiKey;

    //@Value("${claude.api.model}")
    //private String model;

    //@Value("${claude.api.max-tokens}")
    //private int maxTokens;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String classifyEmail(String subject, String body) {
        try {
            String prompt = buildClassificationPrompt(subject, body);   //get the prompt

            // Build request JSON manually
            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("model", "llama3.2");
            requestNode.put("prompt", prompt);
            requestNode.put("stream", false);

            String requestJson = objectMapper.writeValueAsString(requestNode);

            log.info("Sending request to Ollama for subject : {}", subject);
            log.info("Request JSON : {}", requestJson);

            // connection request headers -> content type, api key, anthropic version
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:11434/api/generate",
                    entity,
                    String.class
            );

            log.info("Ollama response: {}", response.getBody());

            return parseClassification(response.getBody());

        } catch (Exception e) {
            log.error("Error calling Ollama: {}", e.getMessage());
            return "OTHER";
        }
    }

    private String buildClassificationPrompt(String subject, String body) {

        // if body isn't null , keep the length to 200 else return empty string
        String cleanBody = body != null
                ? body.substring(0, Math.min(body.length(), 200))
                : "";

        // send this prompt along with subject and the 200 length body
        return "You are an email classifier. Read this email and reply with ONLY one word.\n\n"
                + "Rules:\n"
                + "COMPLAINT - angry customer, wants refund, bad experience, dissatisfied\n"
                + "LEAD - interested in buying, asking for price, wants demo, sales inquiry\n"
                + "SUPPORT - needs help, technical problem, cannot login, how to use, bug\n"
                + "SPAM - newsletter, job alert, promotional, social media, marketing\n"
                + "OTHER - anything else\n\n"
                + "Email Subject: " + subject + "\n"
                + "Email Body: " + cleanBody + "\n\n"
                + "Reply with ONE word only:";
    }

    // imagine read tree like a directory, folders inside directory and sub folders inside those, like a tree
    private String parseClassification(String response) {
        try {
            var jsonNode = objectMapper.readTree(response);
            String text = jsonNode
                    .path("response")
                    .asText()
                    .trim()
                    .toUpperCase();

            log.info("Ollama raw response text: {}", text);

            if (text.contains("COMPLAINT")) return "COMPLAINT";
            if (text.contains("LEAD")) return "LEAD";
            if (text.contains("SUPPORT")) return "SUPPORT";
            if (text.contains("SPAM")) return "SPAM";
            return "OTHER";

        } catch (Exception e) {
            log.error("Error parsing Ollama response: {}", e.getMessage());
            return "OTHER";
        }
    }
}