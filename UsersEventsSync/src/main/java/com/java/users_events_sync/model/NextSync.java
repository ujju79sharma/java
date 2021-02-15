package com.java.users_events_sync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "next_sync")
public class NextSync {

	@Id
	private String uid;
	private long nextSync;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public long getNextSync() {
		return nextSync;
	}

	public void setNextSync(long nextSync) {
		this.nextSync = nextSync;
	}

	@Override
	public String toString() {
		return "NextSync [uid=" + uid + ", nextSync=" + nextSync + "]";
	}
}