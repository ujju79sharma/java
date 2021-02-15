package com.java.gigturbo.controller;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.gigturbo.dao.GoogleCalendarEventsDAO;
import com.java.gigturbo.model.GoogleCalendarEvents;
import com.java.gigturbo.request.pojo.CreateEventRequest;
import com.java.gigturbo.request.pojo.DeleteEventRequest;
import com.java.gigturbo.request.pojo.UpdateEventRequest;
import com.java.gigturbo.response.pojo.RestResponse;

@RestController
@RequestMapping("/usercalendar/gcalevent/")
public class EventCRUDControllerWithDB {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private GoogleCalendarEventsDAO googleCalendarEventsDAO;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<RestResponse> createAnEvent(@RequestBody CreateEventRequest createEventDetails) throws Exception {

		try {

			if (createEventDetails.getUid() != null && createEventDetails.getEvent_id() != null) {
				
				GoogleCalendarEvents googleCalendarEvent = new GoogleCalendarEvents(); // create Google calendar Object to store in Mongo

				if (createEventDetails.getSummary() != null)
					googleCalendarEvent.setSummary(createEventDetails.getSummary()); // set summary field of google calendar mongo document

				if  (createEventDetails.getiCalUID() != null)
					googleCalendarEvent.setICalUID(createEventDetails.getiCalUID()); // set iCalUID of the google calendar event

				if (createEventDetails.getStartTimeEpoch() > 0 && createEventDetails.getEndTimeEpoch() > 0) {
					
					googleCalendarEvent.setStartDateTime(createEventDetails.getStartTimeEpoch() + createEventDetails.getTimeZoneOffset()*60000); // set start date time, end date time for the event
					googleCalendarEvent.setEndDateTime(createEventDetails.getEndTimeEpoch() + createEventDetails.getTimeZoneOffset()*60000);
					googleCalendarEvent.setTimeZone(createEventDetails.getTimezone());
					googleCalendarEvent.setTimeZoneOffset(createEventDetails.getTimeZoneOffset());
				}
				
				if (createEventDetails.getRecurringEventId() != null)
					googleCalendarEvent.setRecurringEventId(createEventDetails.getRecurringEventId()); // set RecurringEventId of google calendar event
				
				googleCalendarEvent.setUid(createEventDetails.getUid()); // set userId

				googleCalendarEvent.setEventId(createEventDetails.getEvent_id()); // set id of the google calendar Id
				
				googleCalendarEvent.setEventPlatform(createEventDetails.getEventPlatform()); // set event source
				
				mongoTemplate.save(googleCalendarEvent, "google_calendar_events"); // save the document in google calendar events collection
				
				RestResponse restResponse = new RestResponse();
	
				restResponse.setMessage("Event created.");
				restResponse.setData(googleCalendarEvent);
				restResponse.setStatus("SUCCESS");
	
				return ResponseEntity.ok(restResponse);
			} else {
				
				RestResponse restResponse = new RestResponse();
				
				restResponse.setMessage("Entered null eventId or UID.");
				restResponse.setData(null);
				restResponse.setStatus("ERROR");
	
				return ResponseEntity.ok(restResponse);
			}

		} catch (Exception e) {

			RestResponse restResponse = new RestResponse();

			e.printStackTrace();
			restResponse.setMessage("Exception occured.");
			restResponse.setData(e.getMessage());
			restResponse.setStatus("ERROR");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
		}
	}
	
	@RequestMapping (value = "/fetch", method = RequestMethod.GET)
	public ResponseEntity<RestResponse> getGoogleEventFromMongo(@RequestParam(name = "event_id", required = true, defaultValue = "") String eventId) {

		if (eventId.equals("")) {
			
			RestResponse restResponse = new RestResponse();

			restResponse.setMessage("Please enter event id. ");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
		} else {
			
			GoogleCalendarEvents googleCalendarEvent = googleCalendarEventsDAO.getGoogleCalendarEventById(eventId);
			
			RestResponse restResponse = new RestResponse();

			restResponse.setMessage("Event found.");
			restResponse.setData(googleCalendarEvent);
			restResponse.setStatus("SUCCESS");

			return ResponseEntity.ok(restResponse);
		}
	}
	
	@RequestMapping(value = "/fetchEvents", method = RequestMethod.GET)
	public ResponseEntity<RestResponse> getGoogleEventsFromMongo(@RequestParam(name = "uid", required = true, defaultValue = "") String uid, @RequestParam(name = "startEpochTime", required = false, defaultValue = "0") Long start, @RequestParam(name = "endEpochTime",
		required = false, defaultValue = "0") Long end, @RequestParam(name = "timezoneShift", required = false, defaultValue = "") Integer timezoneShift ) throws Exception {

		List<GoogleCalendarEvents> googleCalendarEvents = new ArrayList<>();
		
		if (uid.equals("")) { // if nothing is mentioned

			RestResponse restResponse = new RestResponse();

			restResponse.setMessage("Please provide userId. ");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
		}
		else if (start != null && end != null && start.longValue() > 0 && end.longValue() > 0) { // if userId, startDate and endDate all are mentioned

			try {
				
				long filteredStartDateTime = start.longValue() + 60000*timezoneShift; // converting and adjusting given startEpoch 
				long filteredEndDateTime = end.longValue() + 60000*timezoneShift;
						
				googleCalendarEvents = googleCalendarEventsDAO.getGoogleCalendarEventsByUserId(uid, filteredStartDateTime, filteredEndDateTime); // fetch user's events based on uid, startDate and endDate

				RestResponse restResponse = new RestResponse();

				restResponse.setMessage(googleCalendarEvents.size() + " event(s) found.");
				restResponse.setData(googleCalendarEvents);
				restResponse.setStatus("SUCCESS");

				return ResponseEntity.ok(restResponse);
			} catch (Exception e) {

				RestResponse restResponse = new RestResponse();

				e.printStackTrace();
				restResponse.setMessage("Exception occured. ");
				restResponse.setData(e.getMessage());
				restResponse.setStatus("ERROR");

				return ResponseEntity.status(500).body(restResponse);
			}
		} else if (start != null && start.longValue() > 0 && end.longValue() == 0) {

			OffsetDateTime localDateTime = LocalDateTime.now().atOffset(ZoneOffset.ofHoursMinutes(timezoneShift/60, timezoneShift%60));
			
			long epochSec = localDateTime.toEpochSecond()*1000; // fetching current dateTime from given timeZoneShift
			
			long filteredStartDateTime = start + timezoneShift*60000; // converting into milliseconds
 
			long filteredEndDateTime = epochSec; // converting epoch seconds to milliseconds
			
			googleCalendarEvents = googleCalendarEventsDAO.getGoogleCalendarEventsByUserId(uid, filteredStartDateTime, filteredEndDateTime); // fetch user's events based on uid, startDate and endDate

			RestResponse restResponse = new RestResponse();

			restResponse.setMessage(googleCalendarEvents.size() + " event(s) found.");
			restResponse.setData(googleCalendarEvents);
			restResponse.setStatus("SUCCESS");
			
			return ResponseEntity.ok(restResponse);
			
		} else if (end != null && end.longValue() > 0 && start.longValue() == 0) { // if endDateTime value is given
			
			long filteredStartDateTime = googleCalendarEventsDAO.getStartDateTime(uid); // fetch startDateTime from database record of the first event created
			
			long filteredEndDateTime = end + timezoneShift*60000;	//converting end date to epoch milli seconds
			
			googleCalendarEvents = googleCalendarEventsDAO.getGoogleCalendarEventsByUserId(uid, filteredStartDateTime, filteredEndDateTime);

			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage(googleCalendarEvents.size() + " event(s) found.");
			restResponse.setData(googleCalendarEvents);
			restResponse.setStatus("SUCCESS");
			
			return ResponseEntity.ok(restResponse);
		}
			else { // if only userId is given.

				googleCalendarEvents = googleCalendarEventsDAO.getGoogleCalendarEventsByUserId(uid, 0, 0); //0,0 means no startDate and endDate provided
	
				RestResponse restResponse = new RestResponse();

				restResponse.setMessage(googleCalendarEvents.size()+" event(s) found. ");
				restResponse.setData(googleCalendarEvents);
				restResponse.setStatus("SUCCESS");

				return ResponseEntity.ok(restResponse);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public ResponseEntity<RestResponse> updateAnEvent(@RequestBody UpdateEventRequest updateEventRequest) throws Exception {

		if (updateEventRequest != null && updateEventRequest.getUid() != null && updateEventRequest.getEvent_id() != null) {

			GoogleCalendarEvents googleCalendarEvent = googleCalendarEventsDAO.getGoogleCalendarEventsByUserIdAndEventId(updateEventRequest.getUid(),updateEventRequest.getEvent_id());
			
			if (googleCalendarEvent != null) {

				if (updateEventRequest.getSummary() != null) // update summary of the event
					googleCalendarEvent.setSummary(updateEventRequest.getSummary()); // update in mongo document
				
				if (updateEventRequest.getUid() != null)
					googleCalendarEvent.setUid(updateEventRequest.getUid()); // set userId

				if (updateEventRequest.getEvent_id() != null)
					googleCalendarEvent.setEventId(updateEventRequest.getEvent_id()); // set id of the google calendar Id

				try { // handle date time exception
					if (updateEventRequest.getStartTimeEpoch() > 0 && updateEventRequest.getStartTimeEpoch() > 0 && updateEventRequest.getTimeZoneOffset() > 0
							&& updateEventRequest.getTimezone() != null) { // update startDate and endDate of the event

						googleCalendarEvent.setStartDateTime(updateEventRequest.getStartTimeEpoch()); // set start date time, end date time for the event
						googleCalendarEvent.setEndDateTime(updateEventRequest.getEndTimeEpoch());
						googleCalendarEvent.setTimeZone(updateEventRequest.getTimezone());
						googleCalendarEvent.setTimeZoneOffset(updateEventRequest.getTimeZoneOffset());
						
					}
				} catch (Exception e) {
					RestResponse restResponse = new RestResponse();

					restResponse.setMessage("Exception Occured.");
					restResponse.setData(e.getMessage());
					restResponse.setStatus("ERROR");

					return ResponseEntity.status(500).body(restResponse);
				}

				if (updateEventRequest.getRecurringEventId() != null)
					googleCalendarEvent.setRecurringEventId(updateEventRequest.getRecurringEventId()); // update recurring eventId
				
				if (updateEventRequest.getiCalUID() != null)
					googleCalendarEvent.setICalUID(updateEventRequest.getiCalUID()); // update iCalUID
				
				if (updateEventRequest.getEventPlatform() != null)
					googleCalendarEvent.setEventPlatform(updateEventRequest.getEventPlatform()); // set event source
				
					mongoTemplate.save(googleCalendarEvent, "google_calendar_events");
					
					RestResponse restResponse = new RestResponse();

					restResponse.setMessage("Event updated successfully.");
					restResponse.setData(googleCalendarEvent);
					restResponse.setStatus("SUCCESS");

					return ResponseEntity.ok(restResponse);

			} else {

				RestResponse restResponse = new RestResponse();

				restResponse.setMessage("Event not found");
				restResponse.setData(null);
				restResponse.setStatus("ERROR");

				return ResponseEntity.status(404).body(restResponse);

			}
		} else {

			RestResponse restResponse = new RestResponse();

			restResponse.setMessage("CalendarId or EventId not provided.");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public ResponseEntity<RestResponse> deleteAnEvent(@RequestBody DeleteEventRequest deleteEventRequest) throws Exception {

		if (deleteEventRequest.getId() != null && deleteEventRequest.getUid() != null) {

			try {
				// Delete an event
				GoogleCalendarEvents googleCalendarEvent = googleCalendarEventsDAO.getGoogleCalendarEventsByUserIdAndEventId(deleteEventRequest.getUid(), deleteEventRequest.getId()); // fetch the event of user from mongoDb

				if (googleCalendarEvent != null) {
					mongoTemplate.remove(googleCalendarEvent, "google_calendar_events"); // remove the event from database
					
					RestResponse restResponse = new RestResponse();

					restResponse.setMessage("Event deleted.");
					restResponse.setData("Deleted event info: "+googleCalendarEvent);
					restResponse.setStatus("SUCCESS");

					return ResponseEntity.ok(restResponse);
				}
				else {
					
					RestResponse restResponse = new RestResponse();

					restResponse.setMessage("No event found.");
					restResponse.setData(null);
					restResponse.setStatus("ERROR");

					return ResponseEntity.ok(restResponse);
				}

			} catch (Exception e) {

				RestResponse restResponse = new RestResponse();

				restResponse.setMessage("Exception occured.");
				restResponse.setData(e.getMessage());
				restResponse.setStatus("ERROR");

				return ResponseEntity.status(500).body(restResponse);
			}
		} else {

			RestResponse restResponse = new RestResponse();

			restResponse.setMessage("No event deleted.");
			restResponse.setData("EventId or userId not found");
			restResponse.setStatus("ERROR");

			return ResponseEntity.status(404).body(restResponse);
		}
	}
}
