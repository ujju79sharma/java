package com.java.users_events_sync.util;

import java.util.Map;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class ConstantUtils {

	public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	public static String getClusterName() { // fetching cluster name from environment variable

		Map<String, String> env = System.getenv();
			
		return env.get("CLUSTER_NAME");
	}

	public static String getUsername() { // fetching username from environment variable

		Map<String, String> env = System.getenv();

		return env.get("USERNAME");
	}

	public static String getPassword() { // fetching password from environment variable

		Map<String, String> env = System.getenv();

		return env.get("PASSWORD");
	}

	public static String getDatabaseName() { // fetching database name from environment variable

		Map<String, String> env = System.getenv();

		return env.get("DATABASE_NAME");
	}

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
}