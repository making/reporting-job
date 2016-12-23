package com.example;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;

@SpringBootApplication
@EnableBatchProcessing
@EnableBinding(Sink.class)
public class JobWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobWorkerApplication.class, args);
	}
}
