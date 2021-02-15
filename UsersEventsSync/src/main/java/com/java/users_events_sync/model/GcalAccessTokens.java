package com.java.users_events_sync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gcal_access_tokens")
public class GcalAccessTokens {

	@Id
	private String uid;
	private String accessToken;
	private long accessTokenExpiration;
	private long lastSynced;
	private long nextSync;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public long getAccessTokenExpiration() {
		return accessTokenExpiration;
	}

	public void setAccessTokenExpiration(long accessTokenExpiration) {
		this.accessTokenExpiration = accessTokenExpiration;
	}

	public long getLastSynced() {
		return lastSynced;
	}

	public void setLastSynced(long lastSynced) {
		this.lastSynced = lastSynced;
	}

	public long getNextSynced() {
		return nextSync;
	}

	public void setNextSync(long nextSync) {
		this.nextSync = nextSync;
	}

	@Override
	public String toString() {
		return "GcalAccessToken [uid=" + uid + ", accessToken=" + accessToken + ", accessTokenExpiration="
				+ accessTokenExpiration + ", lastSynced=" + lastSynced + ", nextSync=" + nextSync + "]";
	}
}