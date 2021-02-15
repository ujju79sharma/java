package com.java.users_events_sync.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.java.users_events_sync.dao.UserAccessTokensDAO;
import com.java.users_events_sync.model.GcalAccessTokens;

@Repository
public class UserAccessTokenDAOImpl implements UserAccessTokensDAO{

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public GcalAccessTokens getUsersAccessTokenByUID(String uid) {
		
		GcalAccessTokens gcalAccessToken = mongoTemplate.findOne(Query.query(Criteria.where("uid").is(uid)), GcalAccessTokens.class);
		
		if (gcalAccessToken != null)
			return gcalAccessToken;
		else
			return null;
	}
}
