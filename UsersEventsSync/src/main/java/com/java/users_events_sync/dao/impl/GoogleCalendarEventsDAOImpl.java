package com.java.users_events_sync.dao.impl;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.java.users_events_sync.dao.GoogleCalendarEventsDAO;
import com.java.users_events_sync.model.GoogleCalendarEvents;

@Repository
public class GoogleCalendarEventsDAOImpl implements GoogleCalendarEventsDAO {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public Map<String, GoogleCalendarEvents> getGoogleCalendarEventsByUID(String uid) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("uid").is(uid));
		return mongoTemplate.find(query, GoogleCalendarEvents.class).stream().collect(Collectors.toMap(GoogleCalendarEvents::getEventPlatform, events->events));
	}
}