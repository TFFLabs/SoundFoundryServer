package com.tfflabs.soundfoundry.dto;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Event {

	private String user;
	private EventType type;
	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	private Map<String, String> context = new HashMap<>();
	
	public void setContext(String key, String value){
		context.put(key, value);
	}
	
	public String getContext(String key){
		return context.get(key);
	}
	
	public Event fromJson(String json){
		Event event = new Event();
		return event;		
	}
	public String toJson(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Map<String, String> getContext() {
		return context;
	}

	public void setContext(Map<String, String> context) {
		this.context = context;
	}
	
}
