package com.java.users_events_sync.util;

import static com.java.users_events_sync.util.FetchUserEvents.*;
import static com.java.users_events_sync.util.GoogleCalendarEventUtil.googleCalendarEventUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.java.users_events_sync.dao.GoogleCalendarEventsDAO;
import com.java.users_events_sync.dao.UserAccessTokensDAO;
import com.java.users_events_sync.dao.UserRefreshTokensDAO;
import com.java.users_events_sync.model.GcalAccessTokens;
import com.java.users_events_sync.model.GcalRefreshTokens;
import com.java.users_events_sync.model.GoogleCalendarEvents;
import com.java.users_events_sync.model.NextSync;
import com.java.users_events_sync.response.pojo.RestResponse;
import com.google.api.services.calendar.model.Event.ExtendedProperties;

@Component
public class SyncUserEventDAOImpl implements SyncUserEventsDAO {

	@Autowired
	private GoogleCalendarEventsDAO googleCalendarEventsDAO;
	@Autowired
	private UserAccessTokensDAO userAccessTokenDAO;
	@Autowired
	private UserRefreshTokensDAO userRefreshTokensDAO;
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public ResponseEntity<RestResponse> syncEvents(String uid) throws GeneralSecurityException, IOException, URISyntaxException {	
		
		long currentDateTime = System.currentTimeMillis(); // fetch current time in milliseconds
		
		GcalAccessTokens gcalAccessToken = userAccessTokenDAO.getUsersAccessTokenByUID(uid); // fetch user's access token
		
		if (gcalAccessToken != null && gcalAccessToken.getAccessToken().length() > 0) { // if access token exist
		
			long gcalAccessTokenExpiration = gcalAccessToken.getAccessTokenExpiration(); // fetch access token expiration
			
			if (gcalAccessTokenExpiration < currentDateTime) { // check access token expiration with current time
				
				GcalRefreshTokens gcalRefreshToken = userRefreshTokensDAO.getUsersRefreshTokenByUID(uid); // generate new access token from refresh token, get refresh token from user's refresh token collection

				if (gcalRefreshToken != null && gcalRefreshToken.getRefreshToken() != null) {

					String refreshToken = gcalRefreshToken.getRefreshToken();
					
					String updatedAccessToken = FetchUserAccessToken.fetchUserAccessTokenFromRefreshToken(refreshToken); // fetch latest access token

					if (updatedAccessToken != null && updatedAccessToken.length() > 0) {
						
						gcalAccessToken.setAccessToken(updatedAccessToken);
						gcalAccessToken.setAccessTokenExpiration(System.currentTimeMillis() + 60*60*1000); // add 1 hour
						gcalAccessToken.setLastSynced(System.currentTimeMillis()); // add current time
						gcalAccessToken.setNextSync(System.currentTimeMillis() + 5*60*1000); // add 5 minutes
	
						mongoTemplate.save(gcalAccessToken, "gcal_access_tokens");
					} else {
						
						RestResponse restResponse = new RestResponse();
						
						restResponse.setMessage("Refresh token got expired.");
						restResponse.setData(null);
						restResponse.setStatus("ERROR");
						
						return ResponseEntity.status(500).body(restResponse);
					}
				}else {
					
					RestResponse restResponse = new RestResponse();
					
					restResponse.setMessage("User's refresh token does not exist.");
					restResponse.setData(null);
					restResponse.setStatus("ERROR");
					
					return ResponseEntity.status(404).body(restResponse);
				}
			}

			Object object = getAllGoogleEventsOfUser(gcalAccessToken.getAccessToken(), null).getBody().getData(); // based on users access token fetch all the events
			
			if (!(object instanceof String)) {
				
				@SuppressWarnings("unchecked")
				Map<String, Event> googleCalendarEvents = (Map<String, Event>)object; // all google calendar events of user
				Map<String, GoogleCalendarEvents> dbCalendarEvents = googleCalendarEventsDAO.getGoogleCalendarEventsByUID(uid); // all stored database records 
				
				List<Object> eventsToAddToGoogleCal = new ArrayList<>();
				List<Object> eventsToDeleteFromGoogleCal = new ArrayList<>();
				List<Object> eventsToAddToGigTurbo = new ArrayList<>();
				List<Object> eventsToDeleteFromGigTurbo = new ArrayList<>();
				
				Set<String> gigTurboEventKeys = new HashSet<>();
				Set<String> googleCalendarEventKeys = new HashSet<>();

				//remove the 2 common events that exists in both the calendar
				for (String eachKey: new ArrayList<>(gigTurboEventKeys)) { 
					
					if (googleCalendarEvents != null && googleCalendarEvents.containsKey(eachKey)) {
						googleCalendarEvents.remove(eachKey);
						dbCalendarEvents.remove(eachKey);
					}
				}
				
				if (dbCalendarEvents != null && dbCalendarEvents.size() > 0) {

					gigTurboEventKeys = dbCalendarEvents.keySet();	// collect the keys from db calendar events collection
				}

				if (googleCalendarEvents != null && googleCalendarEvents.size() > 0) { 		// collect the keys from google calendar events collection

					googleCalendarEventKeys.addAll(googleCalendarEvents.keySet());
				}

				// add the records which are in GigTurbo but not in Google Calendar
				for (String eachKey : new ArrayList<>(gigTurboEventKeys)) { 
					
					if (eachKey.startsWith("GTE") && !googleCalendarEventKeys.contains(eachKey)) {
						
						GoogleCalendarEvents gcalEvent = dbCalendarEvents.get(eachKey);
						
						Event event = new Event();
						
						event.setSummary(gcalEvent.getSummary());
						
						EventDateTime startDateTime = new EventDateTime();
						
						startDateTime.setTimeZone(gcalEvent.getTimeZone());
						DateTime startDateTimeObj = new DateTime(gcalEvent.getStartDateTime(), gcalEvent.getTimeZoneOffset());
						startDateTime.setDateTime(startDateTimeObj);
						event.setStart(startDateTime);
						
						EventDateTime endDateTime = new EventDateTime();
						
						endDateTime.setTimeZone(gcalEvent.getTimeZone());
						DateTime enddateTimeObj = new DateTime(gcalEvent.getStartDateTime(), gcalEvent.getTimeZoneOffset());
						endDateTime.setDateTime(enddateTimeObj);
						event.setEnd(endDateTime);
						
						Map<String, String> shared = new HashMap<>();	//setting addition properties
						shared.put("eventPlatform", gcalEvent.getEventPlatform());
						event.setExtendedProperties(new ExtendedProperties().setShared(shared));
						
						//event.setICalUID(gcalEvent.getICalUID());
						event.setId(gcalEvent.getEventId());
						//event.setRecurringEventId(gcalEvent.getRecurringEventId());
						
						try {
							googleCalendarEventUtil(gcalAccessToken.getAccessToken(), event, "create"); // creates a new google calendar event
						} catch (GoogleJsonResponseException e) {
							e.printStackTrace();
						}
						eventsToAddToGoogleCal.add(event);
					}
				}
		
				// remove the records which are not in  GigTurbo but exists in Google Calendar
				for (String eachKey : new ArrayList<>(googleCalendarEventKeys)) {
					if (eachKey.startsWith("GTE") && !gigTurboEventKeys.contains(eachKey)) {
						
						Event gcalEvent = (Event) googleCalendarEvents.get(eachKey);
						
						if (gcalEvent != null) {
							Event event = new Event();
							event.setId(gcalEvent.getId());
							
							googleCalendarEventUtil(gcalAccessToken.getAccessToken(), event, "delete"); // delete a new google calendar event
							eventsToDeleteFromGoogleCal.add(googleCalendarEvents.get(eachKey));
						}	
					}
				}
				
				// add the records which are in Google Calendar but not in Gigturbo
				for (String eachKey : new ArrayList<>(googleCalendarEventKeys)) {
					if (eachKey.startsWith("GCAL") && !gigTurboEventKeys.contains(eachKey)) {
						

						Event googleEvent = googleCalendarEvents.get(eachKey);

						GoogleCalendarEvents gcalCalendarEvent = new GoogleCalendarEvents();
	
						gcalCalendarEvent.setUid(uid);
						
						if (googleEvent.getSummary() != null)
							gcalCalendarEvent.setSummary(googleEvent.getSummary());
	
						gcalCalendarEvent.setStartDateTime(googleEvent.getStart().getDateTime().getValue());
						gcalCalendarEvent.setEndDateTime(googleEvent.getEnd().getDateTime().getValue());
						gcalCalendarEvent.setTimeZoneOffset(googleEvent.getStart().getDateTime().getTimeZoneShift());
						gcalCalendarEvent.setTimeZone(googleEvent.getStart().getTimeZone());
						
						String eventPlatform = googleEvent.getExtendedProperties().getShared().get("eventPlatform");
						gcalCalendarEvent.setEventPlatform(eventPlatform);
	
						if (googleEvent.getICalUID() != null)
							gcalCalendarEvent.setiCalUID(googleEvent.getICalUID());
	
						if (googleEvent.getId() != null)
							gcalCalendarEvent.setEventId(googleEvent.getId());
	
						if(googleEvent.getRecurringEventId() != null) 
							gcalCalendarEvent.setRecurringEventId(googleEvent.getRecurringEventId());
	
						mongoTemplate.save(gcalCalendarEvent, "google_calendar_events");
						
						eventsToAddToGigTurbo.add(googleEvent);
					}
				}
		
				// remove the records which are not in google calendar but exists in GigTurbo 
				for (String eachKey : new ArrayList<>(gigTurboEventKeys)) {
					if (eachKey.startsWith("GCAL") && !googleCalendarEventKeys.contains(eachKey)) {
	
						GoogleCalendarEvents googleEvent = dbCalendarEvents.get(eachKey);
	
						mongoTemplate.remove(Query.query(Criteria.where("eventId").is(googleEvent.getEventId())), GoogleCalendarEvents.class); // remove the event from database
	
						eventsToDeleteFromGigTurbo.add(dbCalendarEvents.get(eachKey));
					}
				}

				Map<String, List<Object>> listFinal = new HashMap<>();
				
				listFinal.put("Event added to Google Calendar", eventsToAddToGoogleCal);
				listFinal.put("Event deleted from Google Calendar", eventsToDeleteFromGoogleCal); 
				listFinal.put("Event added to DB", eventsToAddToGigTurbo);
				listFinal.put("Event deleted from DB", eventsToDeleteFromGigTurbo);
				
				NextSync nextSync = new NextSync(); // store the next sync information in the collection
	
				nextSync.setUid(uid);
				nextSync.setNextSync(System.currentTimeMillis()+10*60*1000); // add 10 minutes to existing next sync time.
	
				mongoTemplate.save(nextSync, "next_sync"); // save the object in next sync collection
	
				RestResponse restResponse = new RestResponse();
	
				restResponse.setMessage("Events synced successfully. ");
				restResponse.setData(listFinal);
				restResponse.setStatus("SUCCESS");
	
				return ResponseEntity.ok().body(restResponse);
			}
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Exception occured.");
			restResponse.setData(object);
			restResponse.setStatus("ERROR");
		
			return ResponseEntity.status(404).body(restResponse);
		} else {

			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("uid not found.");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");
		
			return ResponseEntity.status(500).body(restResponse);
		}
	}
}
