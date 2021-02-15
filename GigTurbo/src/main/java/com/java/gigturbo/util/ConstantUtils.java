package com.java.gigturbo.util;

import java.util.Map;

public class ConstantUtils {

	public static String getClusterName() { // fetching database name from environment variable

		Map<String, String> env = System.getenv();
			
		return env.get("CLUSTER_NAME");
	}
	
    public static String getUsername() { // fetching database name from environment variable

		Map<String, String> env = System.getenv();
			
		return env.get("USERNAME");
	}
    
    public static String getPassword() { // fetching database name from environment variable

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