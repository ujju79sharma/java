package com.java.users_events_sync.controller;

import static com.java.users_events_sync.util.ConstantUtils.getClientId;
import static com.java.users_events_sync.util.ConstantUtils.getClientSecret;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.java.users_events_sync.dao.UserRefreshTokensDAO;
import com.java.users_events_sync.model.GcalAccessTokens;
import com.java.users_events_sync.model.GcalRefreshTokens;
import com.java.users_events_sync.request.pojo.RefreshTokenRequest;
import com.java.users_events_sync.response.pojo.RestResponse;
import com.java.users_events_sync.util.SyncUserEventsDAO;

@RestController
@RequestMapping("/user/refreshToken")
public class StoreRefreshTokenController {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRefreshTokensDAO userRefreshTokenDAO;
	@Autowired
	private SyncUserEventsDAO syncController;
	
	@RequestMapping(value = "/store", method = RequestMethod.POST)
	public ResponseEntity<RestResponse> createGcalRefreshTokens (@RequestBody RefreshTokenRequest refreshTokenRequest) {
		
		GcalRefreshTokens gcalRefreshtoken = new GcalRefreshTokens();

		if (refreshTokenRequest.getUid() != null && refreshTokenRequest.getUid().length() > 0)
			gcalRefreshtoken.setUid(refreshTokenRequest.getUid());
		else{
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Uid not provided");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
		}
		if (refreshTokenRequest.getRefreshToken() != null && refreshTokenRequest.getRefreshToken().length() > 0) {
				gcalRefreshtoken.setRefreshToken(refreshTokenRequest.getRefreshToken());
		}
		else {
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Refresh Token not provided");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
		}

		mongoTemplate.save(gcalRefreshtoken, "gcal_refresh_tokens");
		
		
		RestResponse restResponse = new RestResponse();
		
		restResponse.setMessage("Refresh token stored");
		restResponse.setData(gcalRefreshtoken);
		restResponse.setStatus("SUCCESS");
		
		return ResponseEntity.status(HttpStatus.OK).body(restResponse);
	}
	
	@RequestMapping(value = "/fetch", method = RequestMethod.GET)
	public ResponseEntity<RestResponse> readGcalRefreshTokens (@RequestParam(value = "uid", defaultValue = "", required = true) String uid) {

		if (uid != null && uid.length() > 0) {
			
			GcalRefreshTokens userRefreshToken = userRefreshTokenDAO.getUsersRefreshTokenByUID(uid);
			
			RestResponse restResponse = new RestResponse();
			
			if (userRefreshToken != null) {
				
				restResponse.setMessage("Refresh token found");
				restResponse.setData(userRefreshToken);
				restResponse.setStatus("SUCCESS");
			
				return ResponseEntity.status(HttpStatus.OK).body(restResponse);
			} else {
				
				restResponse.setMessage("No refresh token found.");
				restResponse.setData(null);
				restResponse.setStatus("SUCCESS");
			
				return ResponseEntity.status(HttpStatus.OK).body(restResponse);
			}
		}
		else {
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setMessage("Uid not provided");
			restResponse.setData(null);
			restResponse.setStatus("ERROR");
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
		}
	}
	
	@RequestMapping(value = "/storeAndSyncEvents", method = RequestMethod.POST)
	public ResponseEntity<RestResponse> fetchUserRefreshToken (@RequestBody RefreshTokenRequest refreshTokenRequest) throws IOException {
		
		if (refreshTokenRequest.getUid() != null && refreshTokenRequest.getUid().length() > 0 && refreshTokenRequest.getRedirectUri() != null && 
				refreshTokenRequest.getRedirectUri().length() > 0 && refreshTokenRequest.getCode() != null &&
				refreshTokenRequest.getCode().length() > 0) {
			
			String request = "code="+ refreshTokenRequest.getCode()+ "&client_id=" + getClientId() + "&client_secret=" + getClientSecret() + 
							"&redirect_uri="+ refreshTokenRequest.getRedirectUri() + "&grant_type=" + "authorization_code"; // Request Body in plain text data
			
			URL url = new URL("https://oauth2.googleapis.com/token");		// google server URL to extract latest refresh & access token
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");									// POST Request Type
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // Content Type of the request
	
			
			OutputStream os = conn.getOutputStream();
			os.write(request.getBytes());
			os.flush();
	
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			
				String output;
				String jsonOutput = "";
				while ((output = br.readLine()) != null) {
					jsonOutput += output;
				}
				
				@SuppressWarnings("deprecation")
				JsonObject jsonObject = new JsonParser().parse(jsonOutput).getAsJsonObject();
				
				String accessToken = jsonObject.get("access_token").getAsString(); //fetch the latest accessToken.
				String refreshToken = jsonObject.get("refresh_token").getAsString(); //fetch the latest refreshToken.
				int expiresIn = jsonObject.get("expires_in").getAsInt();	//fetch the latest accessToken expiration time.
				
				conn.disconnect();  // closing the IO Connection
				
				GcalRefreshTokens gcalRefreshToken = new GcalRefreshTokens(); // store user's latest refresh token
				
				gcalRefreshToken.setUid(refreshTokenRequest.getUid());
				gcalRefreshToken.setRefreshToken(refreshToken);
				
				mongoTemplate.save(gcalRefreshToken, "gcal_refresh_tokens"); // save the gcalRefreshToken document
				
				GcalAccessTokens gcalAccessToken = new GcalAccessTokens(); // store user's latest access token and its expiration time
				
				gcalAccessToken.setUid(refreshTokenRequest.getUid());
				gcalAccessToken.setAccessToken(accessToken);
				
				long currentDateTime = System.currentTimeMillis(); // calculate current time 
				
				gcalAccessToken.setLastSynced(System.currentTimeMillis()*1000);
				gcalAccessToken.setNextSync(currentDateTime+5*60*1000);
				gcalAccessToken.setAccessTokenExpiration(currentDateTime+expiresIn*1000);
				
				mongoTemplate.save(gcalAccessToken, "gcal_access_tokens");	// save the gcalAccessToken document
				
			} catch (IOException e) {
				
				RestResponse restResponse = new RestResponse();
				
				restResponse.setStatus("ERROR");
				restResponse.setData(e.getMessage());
				restResponse.setMessage("Exception occured.");
				
				return ResponseEntity.ok(restResponse);
			}
			
			Map<String, Object> eventsObj = new HashMap<>();
			
			try {
				eventsObj.put(refreshTokenRequest.getUid(), syncController.syncEvents(refreshTokenRequest.getUid()).getBody().getData());
				
			} catch (GeneralSecurityException | IOException | URISyntaxException e) {
				
				RestResponse restResponse = new RestResponse();

				restResponse.setMessage("Exception occured.");
				restResponse.setData(e.getMessage());
				restResponse.setStatus("ERROR");

				return ResponseEntity.status(403).body(restResponse);
			}
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setStatus("SUCCESS");
			restResponse.setData(eventsObj);
			restResponse.setMessage("Stored refresh token successfully");
			
			return ResponseEntity.ok(restResponse);
		}else {
			
			RestResponse restResponse = new RestResponse();
			
			restResponse.setStatus("ERROR");
			restResponse.setData(null);
			restResponse.setMessage("Uid or redirect uri or code not provided.");
			
			return ResponseEntity.ok(restResponse);
		}
	}
}