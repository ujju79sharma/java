package com.java.users_events_sync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gcal_refresh_tokens")
public class GcalRefreshTokens {

	@Id
	private String uid;
	private String refreshToken;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String accessToken) {
		this.refreshToken = accessToken;
	}

	@Override
	public String toString() {
		return "GcalAccessToken [uid=" + uid + ", accessToken=" + refreshToken+ "]";
	}
}