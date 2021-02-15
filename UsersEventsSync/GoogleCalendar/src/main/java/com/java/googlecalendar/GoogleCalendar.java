package com.java.googlecalendar;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class GoogleCalendar {

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		SpringApplication.run(GoogleCalendar.class, args);
	}
}
