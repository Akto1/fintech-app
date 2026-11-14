package com.example.fintech_app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("application-local.properties")
@SpringBootApplication
public class FintechAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(FintechAppApplication.class, args);
	}

}
