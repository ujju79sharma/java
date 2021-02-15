package com.java.googlecalendar.util;

import java.util.Map;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class ConstantUtils {

	public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	public static String getApplicationName() { // fetching application name from environment variable

		Map<String, String> env = System.getenv();

		return env.get("APPLICATION_NAME");
	}
	
	public static String getClientId() { // fetching clientId from environment variable

		Map<String, String> env = System.getenv();
		
		return env.get("CLIENT_ID");
	}

	public static String getClientSecret() { // fetching clientSecret from environment variable

		Map<String, String> env = System.getenv();

		return env.get("CLIENT_SECRET");
	}
	
	public static String getDatabaseName() {
		
		Map<String, String> env = System.getenv();

		return env.get("DATABASE_NAME");
	}
}