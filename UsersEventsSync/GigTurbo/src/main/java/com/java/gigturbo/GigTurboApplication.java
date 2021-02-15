package com.java.gigturbo;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class GigTurboApplication {

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		SpringApplication.run(GigTurboApplication.class, args);
	}
}
