package com.java.users_events_sync.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.java.users_events_sync.dao.NextSyncDAO;
import com.java.users_events_sync.model.NextSync;

@Repository
public class NextSyncDAOImpl implements NextSyncDAO{

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<NextSync> getUsers() {

		return mongoTemplate.find(Query.query(Criteria.where("nextSync").lt(System.currentTimeMillis())), NextSync.class);
	}
}