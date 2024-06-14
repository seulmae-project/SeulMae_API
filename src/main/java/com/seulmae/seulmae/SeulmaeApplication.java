package com.seulmae.seulmae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SeulmaeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeulmaeApplication.class, args);
	}

}
