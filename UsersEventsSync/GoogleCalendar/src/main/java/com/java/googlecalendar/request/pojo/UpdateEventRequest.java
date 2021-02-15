package com.java.googlecalendar.request.pojo;

public class UpdateEventRequest {

	private String uid;
	private String event_id;
	private String recurringEventId;
	private String iCalUID;
	private String calendarId;
	private String summary;
	private String description;
	private long startTimeEpoch;
	private int timeZoneOffset;
	private long endTimeEpoch;
	private String startDateTime;
	private String endDateTime;
	private String timezone;
	private String accessToken;
	private String refreshToken;
	private String eventPlatform;

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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRecurringEventId() {
		return recurringEventId;
	}

	public void setRecurringEventId(String recurringEventId) {
		this.recurringEventId = recurringEventId;
	}

	public String getiCalUID() {
		return iCalUID;
	}

	public void setiCalUID(String iCalUID) {
		this.iCalUID = iCalUID;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getEventPlatform() {
		return eventPlatform;
	}

	public void setEventPlatform(String eventPlatform) {
		this.eventPlatform = eventPlatform;
	}
}
