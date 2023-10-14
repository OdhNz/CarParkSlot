package com.api.carparkslot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.api.carparkslot.repository")
public class CarparkslotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarparkslotApplication.class, args);
	}

}
