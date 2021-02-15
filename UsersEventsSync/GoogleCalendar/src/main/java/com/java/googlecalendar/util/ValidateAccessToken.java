package com.java.googlecalendar.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
@RequestMapping("/validate")
public class ValidateAccessToken {

	@RequestMapping(value = "/accessToken", method = RequestMethod.GET)
	public static boolean validateAccessToken (String accessToken) {
		
		String serverOutput; // String response from the google server
		String jsonOutput = "";	// converting the server response into String object
		try {
            URL url = new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token="+accessToken);//google URL to fetch expire time of token
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            
            if (conn.getResponseCode() != 200) {
            	conn.disconnect();
            	return false;
            }else {
	            InputStreamReader in = new InputStreamReader(conn.getInputStream());
	            BufferedReader br = new BufferedReader(in);
	            
	            while ((serverOutput = br.readLine()) != null) {
	                jsonOutput +=serverOutput;
	            }
	            
	            if (jsonOutput.length() > 1) {
	            	 @SuppressWarnings("deprecation")
	    			JsonObject jsonObject = new JsonParser().parse(jsonOutput).getAsJsonObject();
	            
		            conn.disconnect();
		            
		            if (jsonObject.get("expires_in").getAsLong() > 0) {
		            	return true;
		            }
		            else {
		            	conn.disconnect(); // close the connection
		            	return false;
		            }
	            }else {
	            	conn.disconnect(); // close the connection
	            	return false;
	            }
            }
        } catch (Exception e) {
            System.out.println("Exception in NetClientGet:- " + e);
            return false;
        }
	}
}
