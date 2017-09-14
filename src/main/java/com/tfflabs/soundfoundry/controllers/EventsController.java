package com.tfflabs.soundfoundry.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.tfflabs.soundfoundry.dto.Event;

@Controller
public class EventsController {

	private Logger log = LoggerFactory.getLogger(EventsController.class);
	
	@MessageMapping("/events")
	@SendTo("/topic/events")
	public Event processEvent(Event event){

		switch(event.getType()){
		
		case ADDTRACK:
			log.debug("Evento recibido: {}", event.toJson());
			break;
		default:
			log.warn("Event of type {} not processed", event.getType());
			break;
		}
		return event;
	}
}
