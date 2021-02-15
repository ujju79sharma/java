package com.java.googlecalendar.util;

import static com.java.googlecalendar.util.ConstantUtils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FetchUserAccessToken {

	public static String fetchUserAccessTokenFromRefreshToken (String refreshToken) throws IOException {
		
		
		String request = "client_id=" + getClientId() + "&client_secret=" + getClientSecret() + "&refresh_token="+ refreshToken + "&grant_type=" + "refresh_token"; // Request Body in plain text data

		URL url = new URL("https://oauth2.googleapis.com/token");		// google server URL to extract access token
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");									// POST Request Type
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		OutputStream os = conn.getOutputStream();
		os.write(request.getBytes());
		os.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		String jsonOutput = "";
		while ((output = br.readLine()) != null) {
			jsonOutput += output;
		}
		
		@SuppressWarnings("deprecation")
		JsonObject jsonObject = new JsonParser().parse(jsonOutput).getAsJsonObject();
		
		conn.disconnect();  // closing the IO Connection

		return jsonObject.get("access_token").getAsString(); // getting the access token
	}
}