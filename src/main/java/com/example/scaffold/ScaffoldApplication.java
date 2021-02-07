package com.example.scaffold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class ScaffoldApplication {

	@PostConstruct
	void bootstrap() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(ScaffoldApplication.class, args);
	}

}
