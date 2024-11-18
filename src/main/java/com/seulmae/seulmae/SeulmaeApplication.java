package com.seulmae.seulmae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication
public class SeulmaeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeulmaeApplication.class, args);
	}

	@Bean
	public InMemoryHttpExchangeRepository httpExchangeRepository() {
		return new InMemoryHttpExchangeRepository();
	}
}
