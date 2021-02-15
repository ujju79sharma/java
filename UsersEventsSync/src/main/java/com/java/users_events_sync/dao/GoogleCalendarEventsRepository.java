package com.java.users_events_sync.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.java.users_events_sync.model.GoogleCalendarEvents;

public interface GoogleCalendarEventsRepository extends MongoRepository<GoogleCalendarEvents, Long> {

}
