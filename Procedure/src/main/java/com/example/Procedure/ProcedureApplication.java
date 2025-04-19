package com.example.Procedure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@EnableScheduling
public class ProcedureApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcedureApplication.class, args);
	}

	@Bean
	public ConcurrentHashMap<String, CompletableFuture<String>> responseMap() {
		return new ConcurrentHashMap<>();
	}

}
