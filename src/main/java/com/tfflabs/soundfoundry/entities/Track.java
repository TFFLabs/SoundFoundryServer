package com.tfflabs.soundfoundry.entities;

import java.util.Date;
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
	private Date updateDate;

	public Track(String id) {
		setId(id);
	}

	public void increaseProgress(int valuems){
		this.progress += valuems;
		this.progressPercentage = Math.round((progress/getAdjustedDuration()) * 100);
	}
	
	public float getAdjustedDuration(){
		// 1% of tolerance to send the next song
		return (float) (roundToThousands(this.getDuration_ms()) * 0.99);
	}
	
	private float roundToThousands(float duration_ms){
		return Math.round(duration_ms/1000)*1000;
	}
	
	@Override
	public int compareTo(Track o) {
		//Descendent by voters, ascendant by dates
		int orderValue = Integer.compare(o.getVoters().size(), this.getVoters().size());
		if(orderValue == 0){
			if (o.getUpdateDate() != null && this.getUpdateDate() != null){
				orderValue = Long.compare(this.getUpdateDate().getTime(), o.getUpdateDate().getTime());	
			}
		}
		return orderValue;
	}
}
