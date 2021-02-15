package com.java.gigturbo.request.pojo;

public class CreateEventRequest {

	private String uid;
	private String event_id;
	private String iCalUID;
	private String recurringEventId;
	private long startTimeEpoch;
	private int timeZoneOffset;
	private long endTimeEpoch;
	private String summary;
	private String eventPlatform;
	private String timezone;
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public String getiCalUID() {
		return iCalUID;
	}

	public void setiCalUID(String iCalUID) {
		this.iCalUID = iCalUID;
	}

	public String getRecurringEventId() {
		return recurringEventId;
	}

	public void setRecurringEventId(String recurringEventId) {
		this.recurringEventId = recurringEventId;
	}

	public long getStartTimeEpoch() {
		return startTimeEpoch;
	}

	public void setStartTimeEpoch(long startTimeEpoch) {
		this.startTimeEpoch = startTimeEpoch;
	}

	public int getTimeZoneOffset() {
		return timeZoneOffset;
	}

	public void setTimeZoneOffset(int timeZoneOffset) {
		this.timeZoneOffset = timeZoneOffset;
	}

	public long getEndTimeEpoch() {
		return endTimeEpoch;
	}

	public void setEndTimeEpoch(long endTimeEpoch) {
		this.endTimeEpoch = endTimeEpoch;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getEventPlatform() {
		return eventPlatform;
	}

	public void setEventPlatform(String eventPlatform) {
		this.eventPlatform = eventPlatform;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
}