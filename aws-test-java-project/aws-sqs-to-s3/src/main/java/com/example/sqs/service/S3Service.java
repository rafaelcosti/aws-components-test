package com.example.sqs.service;

import com.example.sqs.dto.ClientDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private final S3Client s3Client;
    private final String bucketName = "my-bucket";

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void appendToCsv(ClientDTO client) {
        try {
            List<String> lines = readCsvFromS3();
            lines.add(convertToCsv(client));
            writeCsvToS3(lines);
        } catch (IOException e) {
            logger.error("Error processing CSV file: {}", e.getMessage());
        }
    }

    private List<String> readCsvFromS3() throws IOException {
        List<String> lines = new ArrayList<>();
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key("clients.csv")
                    .build();
            ResponseInputStream<GetObjectResponse> s3object = s3Client.getObject(getObjectRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(s3object));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            logger.info("File not found in S3, creating a new one.");
            lines.add("id_cliente,nome,cpf");
        }
        return lines;
    }

    private void writeCsvToS3(List<String> lines) {
        String content = String.join("\n", lines);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("clients.csv")
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
        logger.info("File saved to S3.");
    }

    private String convertToCsv(ClientDTO client) {
        return client.getId_cliente() + "," + client.getNome() + "," + client.getCpf();
    }
}
