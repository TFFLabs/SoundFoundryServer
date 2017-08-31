package com.tfflabs.soundfoundry.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.tfflabs.soundfoundry.entities.Room;
import com.tfflabs.soundfoundry.entities.Track;
import com.tfflabs.soundfoundry.entities.User;
import com.tfflabs.soundfoundry.repositories.RoomRepository;
import com.tfflabs.soundfoundry.repositories.TrackRepository;

@RunWith(SpringJUnit4ClassRunner.class)
public class RoomServiceTest {

	@TestConfiguration
	static class EmployeeServiceImplTestContextConfiguration {
		@Bean
		public RoomService roomService() {
			return new RoomService();
		}
	}

	@Autowired
	private RoomService roomService;

	@MockBean
	private RoomRepository roomRepositoryMock;
	
	@MockBean
	private TrackRepository trackRepository;
	
	@MockBean
	private SimpMessagingTemplate template;

	@Before
	public void setUp() {
		User user = new User("UserId");

		Track track = new Track("TrackId");
		track.getVoters().add(user);
		
		List<Track> tracks = new ArrayList<Track>();
		tracks.add(track);
		
		Room testRoom = new Room("TestRoom");
		
		Mockito.when(roomRepositoryMock.save(Mockito.any(Room.class))).thenReturn(null);
		Mockito.when(roomRepositoryMock.findOne(Mockito.anyString())).thenReturn(null);
		Mockito.when(roomRepositoryMock.findOne("TestRoom")).thenReturn(testRoom);
		
		Mockito.when(trackRepository.save(Mockito.any(Track.class))).thenReturn(null);
		Mockito.when(trackRepository.findByRooms_name(Mockito.anyString())).thenReturn(new ArrayList<Track>());
		Mockito.when(trackRepository.findByRooms_name("TestRoom")).thenReturn(tracks);
	}

	@Test
	public void addRoom() {
		roomService.addRoom("TestRoom");

	}

	@Test(expected = IllegalArgumentException.class)
	public void addRoomEmptyName() {
		roomService.addRoom("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void addRoomBlankName() {
		roomService.addRoom("   ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void addRoomNullName() {
		roomService.addRoom(null);
	}

	@Test
	public void getRoomByName() {
		String roomName = "TestRoom";
		Assert.notNull(roomService.getRoomByName(roomName), "Room shall not return null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void getRoomByName_empty() {
		String roomName = "";
		roomService.getRoomByName(roomName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getRoomByName_null() {
		String roomName = null;
		roomService.getRoomByName(roomName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getRoomByName_noMatch() {
		String roomName = "NoRoom";
		Assert.isNull(roomService.getRoomByName(roomName), "No match found");
	}

	@Test
	public void getRoomTracks() {
		String roomName = "TestRoom";
		Assert.notNull(roomService.getRoomTracksByRoomName(roomName), "Tracks retrieved");
	}

	public void getRoomTracks_noMatch() {
		String roomName = "NoRoom";
		Assert.isTrue(roomService.getRoomTracksByRoomName(roomName).size() == 0, "No tracks for that room");
	}

	@Test
	public void addTrack() {
		String roomName = "TestRoom";
		Track track = new Track("TrackId");
		roomService.addTrack(track, roomName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addTrack_Null() {
		String roomName = "TestRoom";
		Track track = null;
		roomService.addTrack(track, roomName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addTrack_NoId() {
		String roomName = "TestRoom";
		Track track = new Track();
		roomService.addTrack(track, roomName);
	}

	public void addTrack_NoValidRoom() {
		//TODO check this scenario
		String roomName = "NoRoom";
		Track track = new Track("TrackId");
		roomService.addTrack(track, roomName);
	}

	@Test
	public void upvoteTrack() {
		String roomName = "TestRoom";
		String trackId = "TrackId";
		User user = new User("NewUserId");
		roomService.upvoteTrack(roomName, trackId, user);
		Assert.isTrue(roomService.getRoomTracksByRoomName(roomName).stream().findFirst().get().getVoters().size() == 2, "Vote added to the track");
	}

	@Test(expected = IllegalArgumentException.class)
	public void upvoteTrack_nullTrackId() {
		String roomName = "TestRoom";
		String trackId = null;
		User user = new User("userId");
		roomService.upvoteTrack(roomName, trackId, user);
	}

	@Test(expected = IllegalArgumentException.class)
	public void upvoteTrack_nullUser() {
		String roomName = "TestRoom";
		String trackId = "TrackId";
		User user = null;
		roomService.upvoteTrack(roomName, trackId, user);
	}

	@Test(expected = IllegalArgumentException.class)
	public void upvoteTrack_emptyUser() {
		String roomName = "TestRoom";
		String trackId = "TrackId";
		User user = new User("");
		roomService.upvoteTrack(roomName, trackId, user);
	}

	@Test
	public void downvoteTrack() {
		String roomName = "TestRoom";
		String trackId = "TrackId";
		String userId = "UserId";
		roomService.downvoteTrack(roomName, trackId, userId);
		Assert.isTrue(roomService.getRoomTracksByRoomName(roomName).stream().findFirst().get().getVoters().size() == 0, "No votes remaining on the track");
	}

	@Test
	public void downvoteTrack_notThere() {
		String roomName = "TestRoom";
		String trackId = "TrackIdNope";
		String userId = "userId";
		roomService.downvoteTrack(roomName, trackId, userId);
	}

	@Test(expected = IllegalArgumentException.class)
	public void downvoteTrack_nullTrackId() {
		String roomName = "TestRoom";
		String trackId = null;
		String userId = "UserId";
		roomService.downvoteTrack(roomName, trackId, userId);
	}

	@Test(expected = IllegalArgumentException.class)
	public void downvoteTrack_nullUser() {
		String roomName = "TestRoom";
		String trackId = "TrackId";
		String userId = null;
		roomService.downvoteTrack(roomName, trackId, userId);
	}

	@Test(expected = IllegalArgumentException.class)
	public void downvoteTrack_emptyUser() {
		String roomName = "TestRoom";
		String trackId = "TrackId";
		String userId = "";
		roomService.downvoteTrack(roomName, trackId, userId);
	}

	@Test
	public void downvoteTrack_userNotThere() {
		String roomName = "TestRoom";
		String trackId = "TrackId";
		String userId = "UserIdNope";
		roomService.downvoteTrack(roomName, trackId, userId);
		Assert.isTrue(roomService.getRoomTracksByRoomName(roomName).stream().findFirst().get().getVoters().size() == 1, "No votes removed from the track");
	}
}
