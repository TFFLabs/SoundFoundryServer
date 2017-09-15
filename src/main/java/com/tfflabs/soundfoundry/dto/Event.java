package com.tfflabs.soundfoundry.dto;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import lombok.Data;

@Data
public class Event {

	private String user;
	private EventType type;
	private Map<String, String> context = new HashMap<>();
	
	public Event fromJson(String json){
		Event event = new Event();
		return event;		
	}
	public String toJson(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
