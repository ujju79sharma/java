package com.java.gigturbo.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.java.gigturbo.dao.GoogleCalendarEventsDAO;
import com.java.gigturbo.model.GoogleCalendarEvents;

@Repository
public class GoogleCalendarEventsDAOImpl implements GoogleCalendarEventsDAO {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<GoogleCalendarEvents> getGoogleCalendarEventsByUserId(String userId, long filteredStartDate, long filteredEndDate) {
		
		if (filteredStartDate == 0 && filteredEndDate == 0) {
			Query query = new Query();
			query.addCriteria(Criteria.where("uid").is(userId));
			return mongoTemplate.find(query, GoogleCalendarEvents.class);
		} else {
			
			Query query = new Query();
			
			Criteria criteria1 = Criteria.where("uid").is(userId);
			
			Criteria criteria2 = (Criteria.where("startDateTime").lte(filteredStartDate).lte(filteredEndDate)
							.andOperator(Criteria.where("endDateTime").gte(filteredStartDate).gte(filteredEndDate)));
					
			Criteria criteria3 = (Criteria.where("startDateTime").lte(filteredStartDate).lte(filteredEndDate)
							.andOperator(Criteria.where("endDateTime").gte(filteredStartDate).lte(filteredEndDate)));
					
			Criteria criteria4 = (Criteria.where("startDateTime").gte(filteredStartDate).lte(filteredEndDate)
							.andOperator(Criteria.where("endDateTime").gte(filteredStartDate).gte(filteredEndDate)));
					
			Criteria criteria5 = (Criteria.where("startDateTime").gte(filteredStartDate).lte(filteredEndDate)
							.andOperator(Criteria.where("endDateTime").gte(filteredStartDate).lte(filteredEndDate)));
			
			return mongoTemplate.find(query.addCriteria(criteria1.orOperator(criteria2, criteria3, criteria4, criteria5)), GoogleCalendarEvents.class);
		}
	}

	@Override
	public GoogleCalendarEvents getGoogleCalendarEventsByUserIdAndEventId(String userId, String eventId) {
		
		return mongoTemplate.findOne(Query.query(Criteria.where("uid").is(userId)).addCriteria(Criteria.where("_id").is(eventId)), GoogleCalendarEvents.class);
	}

	@Override
	public long getStartDateTime(String uid) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("uid").is(uid));
		query.with(Sort.by(Sort.Direction.ASC,"startDateTime"));
	    query.limit(1);
		return mongoTemplate.findOne(query, GoogleCalendarEvents.class).getStartDateTime();
	}

	@Override
	public GoogleCalendarEvents getGoogleCalendarEventById(String eventId) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("eventId").is(eventId));
		return mongoTemplate.findOne(query, GoogleCalendarEvents.class);
	}
}