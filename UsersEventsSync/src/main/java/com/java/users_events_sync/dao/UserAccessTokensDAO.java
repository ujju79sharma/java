package com.java.users_events_sync.dao;

import com.java.users_events_sync.model.GcalAccessTokens;

public interface UserAccessTokensDAO {

	GcalAccessTokens getUsersAccessTokenByUID (String uid);
}
