package com.java.googlecalendar.request.pojo;

import java.util.List;

public class CreateEventRequest {

	private String uid;
	private String event_id;
	private String iCalUID;
	private String recurringEventId;
	private long startTimeEpoch;
	private int timeZoneOffset;
	private long endTimeEpoch;
	private long endTimeZoneOffset;
	private String start;
	private String end;
	private String summary;
	private String location;
	private String description;
	private String calendarId;
	private String eventPlatform;
	private String timezone;
	private List<String> attendees;
	private String accessToken;
	
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

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
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

	public long getEndTimeZoneOffset() {
		return endTimeZoneOffset;
	}

	public void setEndTimeZoneOffset(long endTimeZoneOffset) {
		this.endTimeZoneOffset = endTimeZoneOffset;
	}

	private String refreshToken;
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEventPlatform() {
		return eventPlatform;
	}

	public void setEventPlatform(String eventPlatform) {
		this.eventPlatform = eventPlatform;
	}

	public String getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public List<String> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<String> attendees) {
		this.attendees = attendees;
	}
}