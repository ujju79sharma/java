package com.java.users_events_sync.dao;

import com.java.users_events_sync.model.GcalRefreshTokens;

public interface UserRefreshTokensDAO {

	GcalRefreshTokens getUsersRefreshTokenByUID (String uid);
}
