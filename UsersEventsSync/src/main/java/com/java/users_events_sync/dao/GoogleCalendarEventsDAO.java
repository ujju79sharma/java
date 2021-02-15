package com.java.users_events_sync.dao;

import java.util.Map;

import com.java.users_events_sync.model.GoogleCalendarEvents;

public interface GoogleCalendarEventsDAO {

	Map<String, GoogleCalendarEvents> getGoogleCalendarEventsByUID(String uid);
}
