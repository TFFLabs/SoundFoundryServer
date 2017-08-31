package com.tfflabs.soundfoundry.entities;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
	@Id
	private String id;
	private String display_name;
	private String country;
	private Set<Image> images = new HashSet<Image>();
	
	public User(String id){
		setId(id);
	}
}
