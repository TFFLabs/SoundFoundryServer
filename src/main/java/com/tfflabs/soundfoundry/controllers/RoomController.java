package com.tfflabs.soundfoundry.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tfflabs.soundfoundry.entities.Room;
import com.tfflabs.soundfoundry.entities.Track;
import com.tfflabs.soundfoundry.entities.User;
import com.tfflabs.soundfoundry.services.RoomService;

@RestController
@RequestMapping("/room")
public class RoomController {

	@Autowired
	RoomService roomService;
	
	@RequestMapping("/{roomName}")
	public Room RoomgetRoomByName(@PathVariable("roomName") String roomName) {
		return roomService.getRoomByName(roomName);
	}

	@RequestMapping("/{roomName}/track")
	public List<Track> getRoomTracks(@PathVariable("roomName") String roomName) {
		return roomService.getRoomTracksByRoomName(roomName);
	}

	@RequestMapping(value = "/{roomName}/track", method = RequestMethod.POST)
	public void addTrackToRoom(@PathVariable("roomName") String roomName, @RequestBody Track track) {
		roomService.addTrack(track, roomName);
	}

	@RequestMapping(value = "/{roomName}/track/{trackId}/voter", method = RequestMethod.POST)
	public void upvoteTrack(@PathVariable("roomName") String roomName, @PathVariable("trackId") String trackId, @RequestBody User user) {
		roomService.upvoteTrack(roomName, trackId, user);
	}

	@RequestMapping(value = "/{roomName}/track/{trackId}/voter/{userId}", method = RequestMethod.DELETE)
	public void downvoteTrack(@PathVariable("roomName") String roomName, @PathVariable("trackId") String trackId,
			@PathVariable("userId") String userId) {
		roomService.downvoteTrack(roomName, trackId, userId);
	}
}
