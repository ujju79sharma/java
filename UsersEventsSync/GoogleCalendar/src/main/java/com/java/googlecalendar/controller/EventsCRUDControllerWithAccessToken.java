package com.java.googlecalendar.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.ExtendedProperties;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.java.googlecalendar.request.pojo.CreateEventDetails;
import com.java.googlecalendar.request.pojo.DeleteEventRequest;
import com.java.googlecalendar.request.pojo.UpdateEventRequest;
import com.java.googlecalendar.response.pojo.RestResponse;

import static com.java.googlecalendar.util.ValidateAccessToken.validateAccessToken;
import static com.java.googlecalendar.util.FetchUserAccessToken.fetchUserAccessTokenFromRefreshToken;
import static com.java.googlecalendar.util.ConstantUtils.*;

@RestController
@RequestMapping("/user")
public class EventsCRUDControllerWithAccessToken {

	
	@RequestMapping(value = "/event/create", method = RequestMethod.POST)
	public ResponseEntity<RestResponse> createAnEvent(@RequestBody CreateEventDetails createEventDetails) throws IOException, GeneralSecurityException, URISyntaxException {
		
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		
		Credential credential = null;
		
		if (validateAccessToken(createEventDetails.getAccessToken())) { // validate access token expiry time
			credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(createEventDetails.getAccessToken());
		}
		
		else {		// use refresh token to exract access token
			credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(fetchUserAccessTokenFromRefreshToken(createEventDetails.getRefreshToken()));
		}
		
		Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(getApplicationName()).build();
		
		// create new event object
		Event event = new Event().setSummary(createEventDetails.getSummary()) // add summary, location and description
				.setLocation(createEventDetails.getLocation()).setDescription(createEventDetails.getDescription()); 
			
		DateTime startDateTime = new DateTime(createEventDetails.getStart()); //add start Date and time
		EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone(createEventDetails.getTimezone());
		event.setStart(start);
		
		DateTime endDateTime = new DateTime(createEventDetails.getEnd()); //add end Date and time
		EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone(createEventDetails.getTimezone());
		event.setEnd(end);
		
		String[] recurrence = new String[] { "RRULE:FREQ=DAILY;COUNT=1" };
		event.setRecurrence(Arrays.asList(recurrence));
		
		List<EventAttendee> finalAttendees = new ArrayList<>();
		
		for (String eachEmail : createEventDetails.getAttendees()) {
			
			finalAttendees.add(new EventAttendee().setEmail(eachEmail));
		}
		
		event.setAttendees(finalAttendees); //add attendees

		EventReminder[] reminderOverrides = new EventReminder[] {
				new EventReminder().setMethod("email").setMinutes(24 * 60),
				new EventReminder().setMethod("popup").setMinutes(10), 
				};
		
		Event.Reminders reminders = new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminderOverrides));
		event.setReminders(reminders);
		
		Map<String, String> shared = new HashMap<>();	//setting addition properties
		shared.put("eventPlatform", createEventDetails.getEventPlatform());
		event.setExtendedProperties(new ExtendedProperties().setShared(shared));		
		String calendarId;
		
		if (createEventDetails.getCalendarId() != null) {
			calendarId = createEventDetails.getCalendarId(); // add calendar Id 
		}else {
			calendarId = "primary";
		}
			
		try {
			event = service.events().insert(calendarId, event).execute();
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Event created. ");
			restResponse.setData(event);
			restResponse.setStatus("SUCCESS");
			
			return ResponseEntity.ok(restResponse);
			
		} catch (GoogleJsonResponseException e) {
			
			RestResponse restResponse = new RestResponse();

			restResponse.setMessage("Exception occured.");
			restResponse.setData(e.getMessage());
			restResponse.setStatus("ERROR");
			
			return ResponseEntity.status(e.getStatusCode()).body(restResponse);	
		}
	}
	
	@RequestMapping(value = "/fetch/event", method = RequestMethod.GET)
	public static ResponseEntity<RestResponse> getGoogleEvents(@RequestParam(value = "eventId", defaultValue = "", required = true) String eventId, 
								@RequestParam(value = "accessToken", defaultValue = "", required = true) String accessToken,
								@RequestParam(value = "refreshToken", defaultValue = "", required = true) String refreshToken) throws Exception {
		
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		
		if (accessToken == null || accessToken.length() == 0 || eventId == null || eventId.length() == 0) {
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Access token or event Id not provided");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");
			
			return ResponseEntity.status(400).body(restResponse);
		}
		
		Credential credential = null;
		
		if (validateAccessToken(accessToken)) { // validate access token expire time

			credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
		}
		
		else {		// use refresh token to extract new access token
			credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(fetchUserAccessTokenFromRefreshToken(refreshToken));
		}
		
		Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(getApplicationName()).build();

		try {
			Event event = service.events().get("primary", eventId).execute();
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Found event. ");
			restResponse.setData(event);
			restResponse.setStatus("SUCCESS");
			
			return ResponseEntity.ok(restResponse);
			
		}catch (GoogleJsonResponseException e) {
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Exception Occured. ");
			restResponse.setData(e.getContent());
			restResponse.setStatus("ERROR");
			
			return ResponseEntity.status(e.getStatusCode()).body(restResponse);
		} 
	}
	
	@RequestMapping(value = "/fetch/events", method = RequestMethod.GET)
	public static ResponseEntity<RestResponse> getGoogleEvents(@RequestParam(value = "start", defaultValue = "", required = false) String start,
			@RequestParam(name = "end", defaultValue = "", required = false) String end, @RequestParam(value = "accessToken", required = true, defaultValue = "")
		String accessToken, @RequestParam(value = "refreshToken", required = true, defaultValue = "") String refreshToken) throws Exception {
		
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
			
			Events events = null;
			
			if (start.length() > 0 && end.length() > 0) {
				
				DateTime startDate = new DateTime(start);
				DateTime endDate = new DateTime(end);

				events = service.events().list("primary").setTimeMin(startDate).setTimeMax(endDate).setOrderBy("startTime").setSingleEvents(true).execute();
			} else {
				events = service.events().list("primary").execute();
			}
			
			List<Event> items = events.getItems();
		
			if (items.isEmpty()) {
				
				RestResponse restResponse = new RestResponse();

				restResponse.setMessage("No Events found.");
				restResponse.setData(null);
				restResponse.setStatus("SUCCESS");

				return ResponseEntity.ok(restResponse);
			} else {

				Map<Object, Event> eventList = new HashMap<>();

				for (Event event : items) {
					if (event.getExtendedProperties() != null && event.getExtendedProperties().size() > 0) {
						Collection<Object> collection = event.getExtendedProperties().values();
						String str = collection.iterator().next().toString().replaceAll("\\{eventPlatform=", "");
						str = str.replaceAll("\\}", "");
						eventList.put(str, event);
					}else {
						eventList.put(event.getSummary(), event);
					}
					DateTime startDate = event.getStart().getDateTime();
					if (startDate == null) {
						startDate = event.getStart().getDate();
					}
				}

				RestResponse restResponse = new RestResponse();

				restResponse.setMessage(eventList.size()+" Event(s) found.");
				restResponse.setData(eventList);
				restResponse.setStatus("SUCCESS");

				return ResponseEntity.ok(restResponse);
			}
		} catch (GoogleJsonResponseException e) {

			RestResponse restResponse = new RestResponse();

			restResponse.setMessage("Exception occured.");
			restResponse.setData(e.getMessage());
			restResponse.setStatus("ERROR");

			return ResponseEntity.status(e.getStatusCode()).body(restResponse);
		} catch (MalformedURLException e) {

			RestResponse restResponse = new RestResponse();

			restResponse.setMessage("Exception occured.");
			restResponse.setData(e.getMessage());
			restResponse.setStatus("ERROR");

			return ResponseEntity.status(500).body(restResponse);
		} catch (IOException e) {

			RestResponse restResponse = new RestResponse();

			restResponse.setMessage("Exception occured.");
			restResponse.setData(e.getMessage());
			restResponse.setStatus("ERROR");

			return ResponseEntity.status(500).body(restResponse);
		}
	}
	
	@RequestMapping(value = "/update/event", method = RequestMethod.PUT)
	public ResponseEntity<RestResponse> updateAnEvent(@RequestBody UpdateEventRequest updateEventRequest) throws GeneralSecurityException, IOException, URISyntaxException {
		
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		
		Credential credential = null;
		
			if (updateEventRequest.getCalendarId() != null && updateEventRequest.getEvent_id() != null) {
			
			try {	
				if (updateEventRequest.getAccessToken() != null && validateAccessToken(updateEventRequest.getAccessToken())) { // validate access token expiry time
					credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(updateEventRequest.getAccessToken());
				}
				
				else {		// use refresh token to extract access token
					credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(fetchUserAccessTokenFromRefreshToken(updateEventRequest.getRefreshToken()));
				}
				
				Calendar updatedService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(getApplicationName()).build();
				
				Events eventList = updatedService.events().instances(updateEventRequest.getCalendarId(), updateEventRequest.getEvent_id()).execute(); // find the event from google calendar.


				if (eventList.getItems().size() > 0) {

					Event updateEvent = eventList.getItems().get(0);

					if (updateEventRequest.getSummary() != null)  // update summary of the event
						updateEvent.setSummary(updateEventRequest.getSummary()); // update in google calendar

					try { // handle date time exception
						if (updateEventRequest.getStartDateTime() != null && updateEventRequest.getEndDateTime() != null) { // update startDate and endDate of the event

							DateTime startDateTime = new DateTime(updateEventRequest.getStartDateTime()); //add start Date and time
							EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone(updateEventRequest.getTimezone());
							updateEvent.setStart(start); // update startDate in google calendar

							DateTime endDateTime = new DateTime(updateEventRequest.getEndDateTime()); //add end Date and time
							EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone(updateEventRequest.getTimezone());

							updateEvent.setEnd(end); // update startDate in google calendar
						}
					} catch (Exception e) {
						RestResponse restResponse = new RestResponse();
						
						restResponse.setMessage("Exception Occured.");
						restResponse.setData(e.getMessage());
						restResponse.setStatus("ERROR");
						
						return ResponseEntity.status(500).body(restResponse);
					}

					if (updateEventRequest.getDescription() != null)
						updateEvent.setDescription(updateEventRequest.getDescription());

					if (updateEventRequest.getEventPlatform() != null) {

						Map<String, String> shared = new HashMap<>();	//setting addition properties
						shared.put("eventPlatform", updateEventRequest.getEventPlatform());
						updateEvent.setExtendedProperties(new ExtendedProperties().setShared(shared));
					}
	
					Event finalUpdatedEvent = updatedService.events().update(updateEventRequest.getCalendarId(), updateEvent.getId(), updateEvent).execute(); //updating the changes in google calendar
					
					RestResponse restResponse = new RestResponse();
					
					restResponse.setMessage("Event updated successfully.");
					restResponse.setData(finalUpdatedEvent);
					restResponse.setStatus("SUCCESS");

					return ResponseEntity.ok(restResponse);
				}	
				else {
						RestResponse restResponse = new RestResponse();
						
						restResponse.setMessage("No event updated.");
						restResponse.setData("Event Id did not exist. Please provide coorect eventId.");
						restResponse.setStatus("ERROR");
						
						return ResponseEntity.status(404).body(restResponse);
					}
				}catch (GoogleJsonResponseException e) {

					RestResponse restResponse = new RestResponse();

					restResponse.setMessage("Exception occured.");
					restResponse.setData(e.getContent());
					restResponse.setStatus("ERROR");
					
					return ResponseEntity.status(e.getStatusCode()).body(restResponse);
				}
			}else {

				RestResponse restResponse = new RestResponse();

				restResponse.setMessage("Event not found");
				restResponse.setData(null);
				restResponse.setStatus("ERROR");

				return ResponseEntity.status(404).body(restResponse);

			}	
		}

	@RequestMapping(value = "/delete/event", method = RequestMethod.DELETE)
	public ResponseEntity<RestResponse> deleteAnEvent(@RequestBody DeleteEventRequest deleteEventRequest) throws GeneralSecurityException, IOException, URISyntaxException {

		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

		if (deleteEventRequest.getId() != null || deleteEventRequest.getUid() != null) {

			Credential credential = null;

			if (deleteEventRequest.getAccessToken() != null && validateAccessToken(deleteEventRequest.getAccessToken())) { // validate access token expiry time
				credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(deleteEventRequest.getAccessToken());
			}

			else {	// use refresh token to extract new access token
				credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(fetchUserAccessTokenFromRefreshToken(deleteEventRequest.getRefreshToken()));
			}

			Calendar deleteService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(getApplicationName()).build();

			try {
			// Delete an event
				deleteService.events().delete(deleteEventRequest.getCalendarId(), deleteEventRequest.getId()).execute(); // find the event from google calendar

				RestResponse restResponse = new RestResponse();

				restResponse.setMessage("Event deleted.");
				restResponse.setData("");
				restResponse.setStatus("SUCCESS");

				return ResponseEntity.ok(restResponse);

			}catch(GoogleJsonResponseException e) {

				RestResponse restResponse = new RestResponse();
				
				restResponse.setMessage("Exception occured.");
				restResponse.setData(e.getDetails());
				restResponse.setStatus("ERROR");
				
				return ResponseEntity.status(e.getStatusCode()).body(restResponse);
			}
		} else {

			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("No event deleted.");
			restResponse.setData("EventId or userId not found");
			restResponse.setStatus("SUCCESS");
			
			return ResponseEntity.status(200).body(restResponse);
		}
	}
}