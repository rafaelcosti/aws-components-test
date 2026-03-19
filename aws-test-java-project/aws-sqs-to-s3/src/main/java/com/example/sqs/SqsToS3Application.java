package com.example.sqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SqsToS3Application {

	public static void main(String[] args) {
		SpringApplication.run(SqsToS3Application.class, args);
	}

}
