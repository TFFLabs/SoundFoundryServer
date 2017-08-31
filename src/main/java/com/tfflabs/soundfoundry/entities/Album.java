package com.tfflabs.soundfoundry.entities;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Album {
	@Id
	private String id;
	private String name;
	private Set<String> available_markets = new HashSet<String>();
	private Set<Image> images = new HashSet<Image>();
}
