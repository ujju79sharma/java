package com.java.users_events_sync.response.pojo;

import java.util.HashMap;
import java.util.Map;

import com.google.api.services.calendar.model.Event;

public class EventResponse {

	String message;
	String status;
	Map<String, Event> data = new HashMap<>();
	String exception;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<String, Event> getData() {
		return data;
	}

	public void setData(Map<String, Event> mapData) {
		this.data = mapData;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
}
