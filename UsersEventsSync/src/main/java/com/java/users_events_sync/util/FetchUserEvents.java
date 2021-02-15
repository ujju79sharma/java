package com.java.users_events_sync.util;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.java.users_events_sync.response.pojo.EventResponse;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.java.users_events_sync.util.ConstantUtils.*;
import static com.java.users_events_sync.util.FetchUserAccessToken.*;
import static com.java.users_events_sync.util.ValidateAccessToken.*;

@RestController
@RequestMapping("user")
public class FetchUserEvents {

	public static ResponseEntity<EventResponse> getAllGoogleEventsOfUser(String accessToken, String refreshToken) throws GeneralSecurityException{
		
		try {
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			
			Credential credential = null;
			
			if (accessToken.length() > 0 && validateAccessToken(accessToken)) { // validate access token expiry time
				credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
			}
			
			else {		// use refresh token to exract access token
				credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(fetchUserAccessTokenFromRefreshToken(refreshToken));
			}

			Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(getApplicationName()).build();
			
			try {
				
				long year = org.joda.time.DateTime.now().getYear();
				org.joda.time.DateTime date = org.joda.time.DateTime.parse(year+"-12"+"-31"); // fetch event from last date of this year
				long epochEndDate = date.getMillis(); // convert 31st dec to milli seconds
				long epochStartDate = System.currentTimeMillis(); // fetch current date time
				
				Events events = service.events().list("primary").setTimeMin(new DateTime(epochStartDate)).setTimeMax(new DateTime(epochEndDate)).setOrderBy("startTime").setSingleEvents(true).execute();
				List<Event> items = events.getItems();

				if (items.isEmpty()) {

					EventResponse restResponse = new EventResponse();

					restResponse.setMessage("No Events found.");
					restResponse.setData(null);
					restResponse.setStatus("SUCCESS");

					return ResponseEntity.ok(restResponse);
				} else {

					Map<String, Event> eventList = new HashMap<>();
					for (Event event : items) {
						if (event.getExtendedProperties() != null) {
							eventList.put(event.getExtendedProperties().getShared().get("eventPlatform"), event);
						
						DateTime startDate = event.getStart().getDateTime();
							if (startDate == null) {
								startDate = event.getStart().getDate();
							}
						}
					}

					EventResponse restResponse = new EventResponse();

					restResponse.setMessage(eventList.size()+" Event(s) found.");
					restResponse.setData(eventList);
					restResponse.setStatus("SUCCESS");

					return ResponseEntity.ok(restResponse);
				}
			}catch (Exception e) {

				EventResponse restResponse = new EventResponse();

				restResponse.setMessage("Exception occured.");
				restResponse.setException(e.getMessage());
				restResponse.setStatus("SUCCESS");
				restResponse.setData(null);

				return ResponseEntity.ok(restResponse);
			}
		} catch (Exception e1) {

			EventResponse restResponse = new EventResponse();

			restResponse.setMessage("Exception occured.");
			restResponse.setException(e1.getMessage());
			restResponse.setStatus("ERROR");
			restResponse.setData(null);

			return ResponseEntity.status(500).body(restResponse);
		}
	}
}