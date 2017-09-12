package com.tfflabs.soundfoundry.dto;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventTest {

	@Test
	public void itSerializesToJson(){
		//Given
		Event event = new Event();
		event.setUser("amoya");
		event.setType(EventType.ADDTRACK);
		Map<String, String> context = new HashMap<>();
		context.put("title", "Black Hole Sun");
		context.put("artist", "Soundgarden");
		event.setContext(context);
		//When
		String json = event.toJson();
		//Then
		assertEquals("{\"user\":\"amoya\",\"type\":\"ADDTRACK\",\"context\":{\"artist\":\"Soundgarden\",\"title\":\"Black Hole Sun\"}}", json);
	}
}
