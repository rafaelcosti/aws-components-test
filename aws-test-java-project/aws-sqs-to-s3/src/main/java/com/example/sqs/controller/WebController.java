package com.example.sqs.controller;

import com.example.sqs.dto.ClientDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    public WebController(SqsTemplate sqsTemplate, ObjectMapper objectMapper) {
        this.sqsTemplate = sqsTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/send")
    public void sendMessage(ClientDTO client) throws JsonProcessingException {
        String jsonPayload = objectMapper.writeValueAsString(client);
        sqsTemplate.send(to -> to.queue("my-queue").payload(jsonPayload).header("event_type", "client_created"));
    }
}
