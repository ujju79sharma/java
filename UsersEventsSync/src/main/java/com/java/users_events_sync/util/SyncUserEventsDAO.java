package com.java.users_events_sync.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.springframework.http.ResponseEntity;

import com.java.users_events_sync.response.pojo.RestResponse;

public interface SyncUserEventsDAO {

	ResponseEntity<RestResponse> syncEvents(String uid) throws GeneralSecurityException, IOException, URISyntaxException;
}
