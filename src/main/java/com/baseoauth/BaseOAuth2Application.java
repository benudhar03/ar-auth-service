package com.baseoauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaAuditing
@EnableAsync
public class BaseOAuth2Application {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(BaseOAuth2Application.class);
		application.setAdditionalProfiles(getActiveProfile());
		application.run(args);
	}

	private static String getActiveProfile() {
		String profile = System.getProperty("spring.profiles.active");
		return profile != null ? profile : "default";
	}
}