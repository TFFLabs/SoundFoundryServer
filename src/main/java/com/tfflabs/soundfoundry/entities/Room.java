package com.tfflabs.soundfoundry.entities;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Room {
	@Id
	private String name;
	private String description;
	private Boolean isPlaying;
	private Track currently_playing;
	private Set<User> users;
	
	public Room(String name){
		if(name == null || StringUtils.isEmpty(name.trim())){
			throw new IllegalArgumentException("Room name can not be empty.");
		}
		setName(name);
		setIsPlaying(false);
	}
	
}
