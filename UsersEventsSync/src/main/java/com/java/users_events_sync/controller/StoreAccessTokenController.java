package com.java.users_events_sync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.users_events_sync.dao.UserAccessTokensDAO;
import com.java.users_events_sync.model.GcalAccessTokens;
import com.java.users_events_sync.request.pojo.AccessTokenRequest;
import com.java.users_events_sync.response.pojo.RestResponse;

@RestController
@RequestMapping("/user/accessToken")
public class StoreAccessTokenController {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserAccessTokensDAO userAccessTokenDAO;
	
	@RequestMapping(value = "/store", method = RequestMethod.POST)
	public ResponseEntity<RestResponse> createGcalAccessTokens (@RequestBody AccessTokenRequest accessTokenRequest) {
		
		GcalAccessTokens gcalRefreshtoken = new GcalAccessTokens(); // create access token collection

		if (accessTokenRequest.getUid() != null && accessTokenRequest.getUid().length() > 0)
			gcalRefreshtoken.setUid(accessTokenRequest.getUid());
		else{
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Uid not provided");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
		}
		if (accessTokenRequest.getAccessToken() != null && accessTokenRequest.getAccessToken().length() > 0) {
				gcalRefreshtoken.setAccessToken(accessTokenRequest.getAccessToken());
		}
		else {
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Access Token not provided");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
		}
		
		gcalRefreshtoken.setLastSynced(System.currentTimeMillis()); // converting current datetime in millis
		
		gcalRefreshtoken.setNextSync(System.currentTimeMillis() + 5*60*1000); // adding 5 minutes.
		
		if (accessTokenRequest.getAccessTokenExpiration() > 0)
			gcalRefreshtoken.setAccessTokenExpiration(System.currentTimeMillis()+accessTokenRequest.getAccessTokenExpiration());
		else
			gcalRefreshtoken.setAccessTokenExpiration(System.currentTimeMillis()+60*60*1000); // 1 hour into milliseconds
		
		mongoTemplate.save(gcalRefreshtoken, "gcal_access_tokens");
		
		
		RestResponse restResponse = new RestResponse();
		
		restResponse.setMessage("Access token stored.");
		restResponse.setData(gcalRefreshtoken);
		restResponse.setStatus("SUCCESS");
		
		return ResponseEntity.status(HttpStatus.OK).body(restResponse);
	}
	
	@RequestMapping(value = "/fetch", method = RequestMethod.GET)
	public ResponseEntity<RestResponse> readGcalAccessTokens (@RequestParam(value = "uid", defaultValue = "", required = true) String uid) {

		if (uid != null && uid.length() > 0) {
			
			GcalAccessTokens userRefreshToken = userAccessTokenDAO.getUsersAccessTokenByUID(uid); // fetch user id from the database
			
			RestResponse restResponse = new RestResponse();
			
			if (userRefreshToken != null) {	// if userRefreshToken object is not null
				
				restResponse.setMessage("Access token found.");
				restResponse.setData(userRefreshToken);
				restResponse.setStatus("SUCCESS");
			
				return ResponseEntity.status(HttpStatus.OK).body(restResponse);
			} else {
				
				restResponse.setMessage("No access token found.");
				restResponse.setData(null);
				restResponse.setStatus("SUCCESS");
			
				return ResponseEntity.status(HttpStatus.OK).body(restResponse);
			}
		}
		else { // when userAccessToken is null
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Uid not provided.");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
		}
	}
}