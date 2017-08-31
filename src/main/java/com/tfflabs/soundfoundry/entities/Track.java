package com.tfflabs.soundfoundry.entities;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Track implements Comparable<Track>{
	@Id
	private String id;
	private String name;
	private Set<Room> rooms = new HashSet<Room>();
	private Set<User> voters = new HashSet<User>();
	private Album album;
	private Set<Artist> artists = new HashSet<Artist>();
	private String preview_url;
	private float duration_ms = 0;
	private float progress = 0;
	private Integer progressPercentage = 0;

	public Track(String id) {
		setId(id);
	}

	public void increaseProgress(int valuems){
		this.progress += valuems;
		this.progressPercentage = Math.round((progress/duration_ms) * 100);
	}
	
	@Override
	public int compareTo(Track o) {
		//Descendent by voters
		return Integer.compare(o.getVoters().size(), this.getVoters().size());
	}
}
