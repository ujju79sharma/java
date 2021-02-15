package com.java.gigturbo.dao;

import java.util.List;

import com.java.gigturbo.model.GoogleCalendarEvents;

public interface GoogleCalendarEventsDAO {

	List<GoogleCalendarEvents> getGoogleCalendarEventsByUserId (String userId, long filteredStartDate, long filteredEndDate);

	GoogleCalendarEvents getGoogleCalendarEventsByUserIdAndEventId(String userId, String eventId);

	long getStartDateTime(String uid);

	GoogleCalendarEvents getGoogleCalendarEventById(String eventId);

}
