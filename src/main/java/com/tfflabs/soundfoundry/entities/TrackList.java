package com.tfflabs.soundfoundry.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TrackList implements Cloneable{
	@Id
	private String id;
	private String roomName;
	private LinkedHashMap<String, LinkedList<Track>> roomTracks = new LinkedHashMap<>();
	
	public TrackList(String roomName){
		setRoomName(roomName);
	}
	
	public List<Track> getTracksListRoundRobin(){
		List<Track> tracksList = new ArrayList<Track>();
		TrackList clone = this.clone();
		LinkedList<String> users = new LinkedList<>(clone.getRoomTracks().keySet());
		
		while (users.iterator().hasNext()){
			String user = users.pop();
			if(clone.getRoomTracks().get(user).size() >= 1){
				tracksList.add(clone.getRoomTracks().get(user).pop());
				users.add(user);
			}
		}
		
		return tracksList;
	}
	

	public List<Track> getTracksListSortedByVotes(){
		List<Track> tracksList = getTracksListRoundRobin();
		Collections.sort(tracksList);
		return tracksList;
	}
	
	public void addTrack(User user, Track track){
		if (track == null || StringUtils.isEmpty(track.getId())) {
			throw new IllegalArgumentException("Track id can not be empty.");
		}
		if (CollectionUtils.isEmpty(track.getVoters())) {
			throw new IllegalArgumentException("Track should have votes.");
		}
		if (user == null || StringUtils.isEmpty(user.getId())) {
			throw new IllegalArgumentException("User id can not be empty.");
		}
		List<Track> tracks = getTracksListRoundRobin();
		if (!tracks.stream().anyMatch(trackz -> trackz.getId().equals(track.getId()))) {
			track.setUpdateDate(new Date());
			if(CollectionUtils.isEmpty(getRoomTracks().get(user.getId()))){
				getRoomTracks().put(user.getId(), new LinkedList<Track>());
			}
			getRoomTracks().get(user.getId()).add(track);
		}
	}

	public void upvoteTrack(String trackId, User user) {
		if (StringUtils.isEmpty(trackId)) {
			throw new IllegalArgumentException("Track id can not be empty.");
		}
		if (user == null || StringUtils.isEmpty(user.getId())) {
			throw new IllegalArgumentException("User id can not be empty.");
		}
		List<Track> tracks = getTracksListRoundRobin();
		tracks.stream().filter(trackz -> trackz.getId().equals(trackId)).forEach(tr -> {
			if (tr.getVoters().stream().noneMatch(usr -> usr.getId().equals(user.getId()))) {
				tr.getVoters().add(user);
				tr.setUpdateDate(new Date());
			}
		});
	}

	public void downvoteTrack(String trackId, String userId) {
		List<Track> tracks = getTracksListRoundRobin();
		tracks.stream().filter(trackz -> trackz.getId().equals(trackId)).forEach(tr -> {
			User usrAux = null;
			for (User usr : tr.getVoters()) {
				if (usr.getId().equals(userId)) {
					usrAux = usr;
				}
			}
			tr.getVoters().remove(usrAux);
		});
	}

	public void removeSong(Track track) {
		if (track == null || StringUtils.isEmpty(track.getId())) {
			throw new IllegalArgumentException("Track can not be empty.");
		}
		LinkedList<Track> userTracks = new LinkedList<>();
		String userAux = null;
		for (String user : getRoomTracks().keySet()) {
			userTracks = getRoomTracks().get(user);
			if(userTracks.remove(track)){
				userAux = user;
				break;
			}
		}
		getRoomTracks().remove(userAux);
		getRoomTracks().put(userAux, userTracks);
	}
	
	@SuppressWarnings("unchecked")
	protected TrackList clone(){
		TrackList clone = new TrackList();
		clone.setRoomName(this.getRoomName());
		for (String user : getRoomTracks().keySet()) {
			clone.getRoomTracks().put(user, (LinkedList<Track>) this.getRoomTracks().get(user).clone());
		}
		return clone;
	}
	
}
