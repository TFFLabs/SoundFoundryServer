package com.tfflabs.soundfoundry.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.Stopwatch;
import com.tfflabs.soundfoundry.entities.Room;
import com.tfflabs.soundfoundry.entities.Track;
import com.tfflabs.soundfoundry.entities.User;
import com.tfflabs.soundfoundry.repositories.RoomRepository;
import com.tfflabs.soundfoundry.repositories.TrackRepository;

@Service
public class RoomService {

	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private TrackRepository trackRepository;

	@Autowired
	private SimpMessagingTemplate template;

	@PostConstruct
	public void temporalSetup(){
		String room = "myroom";
		try{
			System.out.println(getRoomByName(room).getName() + " OBTAINED!!");
		}catch (Exception e) {
			Room newRoom = new Room("myroom");
			newRoom.setDescription("Awesome testing room");
			addRoom(newRoom);
		}
	}
	
	public void addRoom(Room room) {
		if (room == null) {
			throw new IllegalArgumentException("Room can not be null.");
		}
		roomRepository.save(room);
	}
	
	public void addRoom(String roomName) {
		Room room = new Room(roomName);
		roomRepository.save(room);
	}

	public Room getRoomByName(String roomName) {
		if (roomName == null || StringUtils.isEmpty(roomName.trim())) {
			throw new IllegalArgumentException("Room name can not be empty.");
		}

		Room room = roomRepository.findOne(roomName);

		if (room == null) {
			throw new IllegalArgumentException("Invalid room name.[" + roomName + "]");
		}
		return room;
	}

	public List<Track> getRoomTracksByRoomName(String roomName) {
		List<Track> tracks =  trackRepository.findByRooms_name(roomName);
		//there is something weird in the sorting
		Collections.sort(tracks);
		return tracks;
	}

	public void addTrack(Track track, String roomName) {
		if (track == null || StringUtils.isEmpty(track.getId())) {
			throw new IllegalArgumentException("Track id can not be empty.");
		}
		List<Track> tracks = getRoomTracksByRoomName(roomName);
		if (!tracks.stream().anyMatch(trackz -> trackz.getId().equals(track.getId()))) {
			track.getRooms().add(getRoomByName(roomName));
			track.setUpdateDate(new Date());
			trackRepository.save(track);
		}
		publishRoomTracks(roomName);
	}

	public void upvoteTrack(String roomName, String trackId, User user) {
		if (StringUtils.isEmpty(roomName) || StringUtils.isEmpty(trackId) || user == null
				|| StringUtils.isEmpty(user.getId())) {
			throw new IllegalArgumentException("Room, Track and User id's can not be null");
		}
		
		List<Track> tracks = getRoomTracksByRoomName(roomName);
		tracks.stream().filter(trackz -> trackz.getId().equals(trackId)).forEach(tr -> {
			if (tr.getVoters().stream().noneMatch(usr -> usr.getId().equals(user.getId()))) {
				tr.getVoters().add(user);
				tr.setUpdateDate(new Date());
				trackRepository.save(tr);
			}
		});
		
		publishRoomTracks(roomName);
	}

	public void downvoteTrack(String roomName, String trackId, String userId) {
		if (StringUtils.isEmpty(roomName) || StringUtils.isEmpty(trackId) || StringUtils.isEmpty(userId)) {
			throw new IllegalArgumentException("Room, Track and User id's can not be null");
		}

		List<Track> tracks = getRoomTracksByRoomName(roomName);
		tracks.stream().filter(trackz -> trackz.getId().equals(trackId)).forEach(tr -> {
			tr.getVoters().forEach(usr -> {
				if (usr.getId().equals(userId)) {
					tr.getVoters().remove(usr);
					trackRepository.save(tr);
				}
			});
		});
		
		publishRoomTracks(roomName);
	}

	public void playNextSong(Room room) {
		if (room.getCurrently_playing() == null) {
			List<Track> tracks = getRoomTracksByRoomName(room.getName());
			if(!CollectionUtils.isEmpty(tracks)){
				room.setCurrently_playing(tracks.get(0));
				
				roomRepository.save(room);
				publishRoom(room);
				
				trackRepository.delete(tracks.get(0));
				publishRoomTracks(room.getName());
			}
		}
	}

	@Scheduled(fixedRate = 200)
	private void publishRoomCronTask() {
		//TODO check how to do this for multiple rooms
		Stopwatch stopwatch = Stopwatch.createStarted();
		Room room = getRoomByName("myroom");
		if (room.getCurrently_playing() == null) {
			playNextSong(room);
		}else{
			stopwatch.stop();
			// Adding database query time
			room.getCurrently_playing().increaseProgress(100 + Math.toIntExact(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
			if (room.getCurrently_playing().getProgress() >= room.getCurrently_playing().getAdjustedDuration()) {
				room.setCurrently_playing(null);
			}
			roomRepository.save(room);
			publishRoom(room);
		}
	}
	
	private void publishRoomTracks(String roomName) {
		template.convertAndSend("/topic/room/" + roomName + "/tracks", getRoomTracksByRoomName(roomName));
	}

	private void publishRoom(Room room) {
		template.convertAndSend("/topic/room/" + room.getName(), room);
	}
}
