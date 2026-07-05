package com.folio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FolioBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FolioBackendApplication.class, args);
	}

}
