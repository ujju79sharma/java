package com.java.users_events_sync.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.java.users_events_sync.dao.NextSyncDAO;
import com.java.users_events_sync.model.NextSync;
import com.java.users_events_sync.response.pojo.RestResponse;
import com.java.users_events_sync.util.SyncUserEventsDAO;

@RestController
@RequestMapping("/calendar")
public class SyncController {

	@Autowired
	private NextSyncDAO nextSyncDAO;
	@Autowired
	private SyncUserEventsDAO syncUserEventsDAO;
	
	@RequestMapping(value = "/sync", method = RequestMethod.GET)
	public ResponseEntity<RestResponse> syncUsersEvent() throws GeneralSecurityException, IOException, URISyntaxException {
		
		List<NextSync> usersEventSync = nextSyncDAO.getUsers(); // fetch all the users whose event needs to be sync: only users whose nextSyncTime is lesser than current time.

		Map<String, Object> obj = new HashMap<>();
		
		if (usersEventSync.size() > 0) {
			for (NextSync eachUser : usersEventSync) {
				obj.put(eachUser.getUid(), syncUserEventsDAO.syncEvents(eachUser.getUid()).getBody().getData()); // sync each users events.
			}
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setStatus("SUCCESS");
			restResponse.setData(obj);
			restResponse.setMessage("Events synced");
			
			return ResponseEntity.ok(restResponse);
		} else {
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setStatus("SUCCESS");
			restResponse.setData(null);
			restResponse.setMessage("No users to be synced.");
			
			return ResponseEntity.ok(restResponse);
		}
	}
}