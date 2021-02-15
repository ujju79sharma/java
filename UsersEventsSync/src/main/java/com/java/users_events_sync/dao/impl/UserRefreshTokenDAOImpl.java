package com.java.users_events_sync.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.java.users_events_sync.dao.UserRefreshTokensDAO;
import com.java.users_events_sync.model.GcalRefreshTokens;

@Repository
public class UserRefreshTokenDAOImpl implements UserRefreshTokensDAO{

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public GcalRefreshTokens getUsersRefreshTokenByUID(String uid) {
		
		return mongoTemplate.findOne(Query.query(Criteria.where("uid").is(uid)), GcalRefreshTokens.class);
	}
}
