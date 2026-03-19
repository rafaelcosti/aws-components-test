package com.example.sqs.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.sqs.dto.ClientDTO;
import com.example.sqs.service.S3Service;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class SqsMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(SqsMessageListener.class);
    private final ObjectMapper objectMapper;
    private final S3Service s3Service;

    public SqsMessageListener(ObjectMapper objectMapper, S3Service s3Service) {
        this.objectMapper = objectMapper;
        this.s3Service = s3Service;
    }

    @SqsListener(value = "my-queue")
    public void receiveMessage(String message, @Header("event_type") String eventType) {
        logger.info("Received message with event_type: {}", eventType);
        try {
            ClientDTO client = objectMapper.readValue(message, ClientDTO.class);
            logger.info("Message deserialized: {}", client);
            s3Service.appendToCsv(client);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing message: {}", e.getMessage());
        }
    }
}
