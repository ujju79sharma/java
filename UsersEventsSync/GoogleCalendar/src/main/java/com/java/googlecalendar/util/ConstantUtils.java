package com.java.googlecalendar.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;

public class ConstantUtils {

	public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final String TEMP_TOKENS_DIRECTORY_PATH = "tokens/";
    public static String pathName = "token_";
    public static String STORED_TOKENS_DIRECTORY_PATH = pathName + "/";
    public static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    public static final String CREDENTIALS_FILE_PATH = "/cred.json";

    public static String applicationName = null;
	public static String clientId = null;
	public static String clientSecretId = null;

	public static String getApplicationName() { // fetching application name from environment variable

		Map<String, String> env = System.getenv();

		for (String envName : env.keySet()) {
			if (envName.equals("APPLICATION_NAME")) {
				applicationName = env.get(envName);
				break;
			}
		}
		return applicationName;
	}
	
	public static String getClientId() { // fetching clientId from environment variable

		Map<String, String> env = System.getenv();

		for (String envName : env.keySet()) {
			if (envName.equals("CLIENT_ID")) {
				clientId = env.get(envName);
				break;
			}
		}
		return clientId;
	}

	public static String getClientSecret() { // fetching clientSecret from environment variable

		Map<String, String> env = System.getenv();

		for (String envName : env.keySet()) {
			if (envName.equals("CLIENT_SECRET")) {
				clientSecretId = env.get(envName);
				break;
			}
		}
		return clientSecretId;
	}
}