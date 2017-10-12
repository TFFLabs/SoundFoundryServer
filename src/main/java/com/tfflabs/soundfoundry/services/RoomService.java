package com.tfflabs.soundfoundry.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.tfflabs.soundfoundry.entities.Room;
import com.tfflabs.soundfoundry.entities.SortMethod;
import com.tfflabs.soundfoundry.entities.Track;
import com.tfflabs.soundfoundry.entities.TrackList;
import com.tfflabs.soundfoundry.entities.User;
import com.tfflabs.soundfoundry.repositories.RoomRepository;
import com.tfflabs.soundfoundry.repositories.TrackListRepository;

@Service
public class RoomService {

	static Log log = LogFactory.getLog(RoomService.class.getName());

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private TrackListRepository trackRepository;

	@Autowired
	private SimpMessagingTemplate template;

	@PostConstruct
	public void temporalSetup() {
		String room = "myroom";
		try {
			System.out.println(getRoomByName(room).getName() + " OBTAINED!!");
		} catch (Exception e) {
			Room newRoom = new Room("myroom");
			newRoom.setDescription("Awesome testing room");
			addRoom(newRoom);
		}
		try {
			System.out.println(trackRepository.findByRoomName(room).getId() + " TRACK LIST OBTAINED!!");
		} catch (Exception e) {
			TrackList newTrackList = new TrackList("myroom");
			trackRepository.save(newTrackList);
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
	
	public void updateRoom(String roomName, Room room) {
		if (room == null) {
			throw new IllegalArgumentException("Room can not be null.");
		}
		Room aux = getRoomByName(roomName);
		aux.updateGeneralRoomFields(room);
		roomRepository.save(aux);
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
		if(SortMethod.ROUNDROBIN.equals(getRoomByName(roomName).getSortMethod())){
			return trackRepository.findByRoomName(roomName).getTracksListRoundRobin();
		}else{
			return trackRepository.findByRoomName(roomName).getTracksListSortedByVotes();	
		}
	}

	public void addTrack(Track track, String roomName) {
		if (track == null || StringUtils.isEmpty(track.getId())) {
			throw new IllegalArgumentException("Track id can not be empty.");
		}
		if (CollectionUtils.isEmpty(track.getVoters())) {
			throw new IllegalArgumentException("Track should have at least one vote.");
		}
		TrackList trackList = trackRepository.findByRoomName(roomName);
		if (trackList != null){
			trackList.addTrack((User) track.getVoters().toArray()[0], track);
			trackRepository.save(trackList);
		}
		publishRoomTracks(roomName);
	}

	public void upvoteTrack(String roomName, String trackId, User user) {
		if (StringUtils.isEmpty(roomName) || StringUtils.isEmpty(trackId) || user == null
				|| StringUtils.isEmpty(user.getId())) {
			throw new IllegalArgumentException("Room, Track and User id's can not be null");
		}
		TrackList trackList = trackRepository.findByRoomName(roomName);
		if (trackList != null){
			trackList.upvoteTrack(trackId, user);
			trackRepository.save(trackList);
		}
		publishRoomTracks(roomName);
	}

	public void downvoteTrack(String roomName, String trackId, String userId) {
		if (StringUtils.isEmpty(roomName) || StringUtils.isEmpty(trackId) || StringUtils.isEmpty(userId)) {
			throw new IllegalArgumentException("Room, Track and User id's can not be null");
		}
		
		TrackList trackList = trackRepository.findByRoomName(roomName);
		if (trackList != null){
			trackList.downvoteTrack(trackId, userId);
			trackRepository.save(trackList);
		}
		
		publishRoomTracks(roomName);
	}

	public void playNextSong(Room room) {
		if (room.getCurrently_playing() == null) {
			List<Track> tracks = getRoomTracksByRoomName(room.getName());
			if (!CollectionUtils.isEmpty(tracks)) {
				room.setCurrently_playing(tracks.get(0));

				roomRepository.save(room);
				publishRoom(room);

				TrackList trackList = trackRepository.findByRoomName(room.getName());
				if (trackList != null){
					trackList.removeSong(tracks.get(0));
					trackRepository.save(trackList);
				}
				publishRoomTracks(room.getName());
			}
		}
	}

	public Set<User> getRoomUsersByRoomName(String roomName) {
		Set<User> users = roomRepository.findOne(roomName).getUsers();
		// there is something weird in the sorting
		return users;
	}

	public void addUser(User user, String roomName) {
		log.info("Adding user with id: '" + user.getId() + "' to room with id: '" + roomName + "'");
		Room room = roomRepository.findOne(roomName);
		if (null == room.getUsers()) {
			room.setUsers(new HashSet<User>());
		}
		room.getUsers().add(user);
		roomRepository.save(room);
		publishRoom(room);
	}

	public void removeUser(String userId, String roomName) {
		log.info("Removing user with id: '" + userId + "' from room with id: '" + roomName + "'");
		Room room = roomRepository.findOne(roomName);
		if (!CollectionUtils.isEmpty(room.getUsers())) {
			for (User user : room.getUsers()) {
				if (user.getId().contentEquals(userId)) {
					room.getUsers().remove(user);
				}
			}
		}
		roomRepository.save(room);
		publishRoom(room);
	}

	@Scheduled(fixedRate = 200)
	private void publishRoomCronTask() {
		// TODO check how to do this for multiple rooms
		Room room = getRoomByName("myroom");
		if (room.getCurrently_playing() == null) {
			playNextSong(room);
		} else {
			room.getCurrently_playing().increaseProgress(200);
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
