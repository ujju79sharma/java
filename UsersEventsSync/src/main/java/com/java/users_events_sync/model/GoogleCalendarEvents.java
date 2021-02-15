package com.java.users_events_sync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "google_calendar_events")
public class GoogleCalendarEvents {

	@Id
	private String eventId;
	private String uid;
	private String iCalUID;
	private long startDateTime;
	private long endDateTime;
	private int timeZoneOffset;
	private String timeZone;
	private String recurringEventId;
	private String summary;
	private String eventPlatform;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String event_id) {
		this.eventId = event_id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getICalUID() {
		return iCalUID;
	}

	public void setICalUID(String iCalUID) {
		this.iCalUID = iCalUID;
	}

	public String getRecurringEventId() {
		return recurringEventId;
	}

	public void setRecurringEventId(String recurringEventId) {
		this.recurringEventId = recurringEventId;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getiCalUID() {
		return iCalUID;
	}

	public void setiCalUID(String iCalUID) {
		this.iCalUID = iCalUID;
	}
	
	public long getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(long startDateTime) {
		this.startDateTime = startDateTime;
	}

	public long getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(long endDateTime) {
		this.endDateTime = endDateTime;
	}

	public int getTimeZoneOffset() {
		return timeZoneOffset;
	}

	public void setTimeZoneOffset(int timeZoneOffset) {
		this.timeZoneOffset = timeZoneOffset;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getEventPlatform() {
		return eventPlatform;
	}

	public void setEventPlatform(String eventPlatform) {
		this.eventPlatform = eventPlatform;
	}

	@Override
	public String toString() {
		return "GoogleCalendarEvents [eventId=" + eventId + ", uid=" + uid + ", iCalUID=" + iCalUID + ", startDateTime="
				+ startDateTime + ", endDateTime=" + endDateTime + ", zoneOffset=" + timeZoneOffset + ", timeZone="
				+ timeZone + ", recurringEventId=" + recurringEventId + ", summary=" + summary + "]";
	}
}