package com.java.users_events_sync.util;

import static com.java.users_events_sync.util.ConstantUtils.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

public class GoogleCalendarEventUtil {

	public static void googleCalendarEventUtil(String accessToken, Event event, String status) throws GeneralSecurityException, IOException {
	
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		
		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
		try	{
			
			Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(getApplicationName()).build();
			
			if (status.equalsIgnoreCase("create"))
				service.events().insert("primary", event).execute();
				
			else if (status.equalsIgnoreCase("delete"))
				service.events().delete("primary", event.getId()).execute();
		} catch (GoogleJsonResponseException e) {
			e.printStackTrace();
		}
	}
}
