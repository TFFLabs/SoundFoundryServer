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
	private SortMethod sortMethod;
	
	public Room(String name){
		if(name == null || StringUtils.isEmpty(name.trim())){
			throw new IllegalArgumentException("Room name can not be empty.");
		}
		setName(name);
		setSortMethod(SortMethod.VOTES);
		setIsPlaying(false);
	}
	
	public void updateGeneralRoomFields(Room room){
		if(!StringUtils.isEmpty(room.getDescription())){
			this.setDescription(room.getDescription());
		}
		if(room.getSortMethod() != null){
			this.setSortMethod(room.getSortMethod());
		}
	}
	
}
