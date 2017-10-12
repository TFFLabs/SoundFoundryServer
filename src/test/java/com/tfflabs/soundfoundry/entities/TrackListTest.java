package com.tfflabs.soundfoundry.entities;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

public class TrackListTest {
	TrackList trackList = new TrackList();

	@Before
	public void setUp() {
		trackList = new TrackList();
		trackList.setRoomName("test");
		User userOne = new User("UserOne");

		Track trackOne = new Track("trackOne");
		trackOne.getVoters().add(userOne);
		trackOne.setUpdateDate(new Date());
		Track trackFour = new Track("trackFour");
		trackFour.getVoters().add(userOne);
		trackFour.setUpdateDate(new Date());

		User userTwo = new User("UserTwo");

		Track trackTwo = new Track("trackTwo");
		trackTwo.getVoters().add(userTwo);
		trackTwo.setUpdateDate(new Date());
		Track trackThree = new Track("trackThree");
		trackThree.getVoters().add(userTwo);
		trackThree.getVoters().add(userOne);
		trackThree.setUpdateDate(new Date());

		trackList.addTrack(userOne, trackOne);
		trackList.addTrack(userTwo, trackTwo);
		trackList.addTrack(userTwo, trackThree);
		trackList.addTrack(userOne, trackFour);
	}

	@Test
	public void getTracksListRoundRobin() {
		Assert.isTrue(trackList.getTracksListRoundRobin().get(0).getId().equals("trackOne"),
				"RoudnRobin sorting not working");
		Assert.isTrue(trackList.getTracksListRoundRobin().get(1).getId().equals("trackTwo"),
				"RoudnRobin sorting not working");
		Assert.isTrue(trackList.getTracksListRoundRobin().get(2).getId().equals("trackFour"),
				"RoudnRobin sorting not working");
		Assert.isTrue(trackList.getTracksListRoundRobin().get(3).getId().equals("trackThree"),
				"RoudnRobin sorting not working");
	}
	
	@Test
	public void getTracksListRoundRobin_next() {
		Assert.isTrue(trackList.getTracksListRoundRobin().get(0).getId().equals("trackOne"),
				"RoudnRobin sorting not working");
		trackList.removeSong(trackList.getTracksListRoundRobin().get(0));
		System.out.println(trackList.getTracksListRoundRobin().get(0).getId());
		Assert.isTrue(trackList.getTracksListRoundRobin().get(0).getId().equals("trackTwo"),
				"RoudnRobin sorting not working");
	}

	@Test
	public void getTracksListSortedByVotes() {
		Assert.isTrue(trackList.getTracksListSortedByVotes().get(0).getId().equals("trackThree"),
				"Votes sorting not working");
		Assert.isTrue(trackList.getTracksListSortedByVotes().get(1).getId().equals("trackOne"),
				"Votes sorting not working");
		Assert.isTrue(trackList.getTracksListSortedByVotes().get(2).getId().equals("trackTwo"),
				"Votes sorting not working");
		Assert.isTrue(trackList.getTracksListSortedByVotes().get(3).getId().equals("trackFour"),
				"Votes sorting not working");
	}

	@Test
	public void addTrack() {
		int initialSize = trackList.getTracksListSortedByVotes().size();
		User testUser = new User("testUser");
		Track testTrack = new Track("testTrack");
		testTrack.getVoters().add(testUser);
		trackList.addTrack(testUser, testTrack);
		Assert.isTrue(trackList.getTracksListSortedByVotes().size() == initialSize + 1, "Track addition not working");
	}

	@Test(expected = IllegalArgumentException.class)
	public void addTrack_trackNoVotes() {
		User testUser = new User("testUser");
		Track testTrack = new Track("testTrack");
		trackList.addTrack(testUser, testTrack);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addTrack_nullUser() {
		User testUser = new User("testUser");
		Track testTrack = new Track("testTrack");
		testTrack.getVoters().add(testUser);
		trackList.addTrack(null, testTrack);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addTrack_nullTrack() {
		User testUser = new User("testUser");
		trackList.addTrack(testUser, null);
	}

	@Test
	public void upvoteTrack() {
		String trackThreeId = "trackThree";
		int initialSize = 0;
		for (Track track : trackList.getTracksListSortedByVotes()) {
			if (track.getId().equals(trackThreeId)) {
				initialSize = track.getVoters().size();
			}
		}
		User testUser = new User("testUser");
		trackList.upvoteTrack(trackThreeId, testUser);

		for (Track track : trackList.getTracksListSortedByVotes()) {
			if (track.getId().equals(trackThreeId)) {
				Assert.isTrue(track.getVoters().size() == initialSize + 1, "Upvote track not working");
			}
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void upvoteTrack_nullUser() {
		String trackThreeId = "trackThree";
		trackList.upvoteTrack(trackThreeId, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void upvoteTrack_nullTrackId() {
		String trackId = null;
		User testUser = new User("testUser");
		trackList.upvoteTrack(trackId, testUser);
	}

	@Test
	public void downvoteTrack() {
		String trackThreeId = "trackThree";
		String userOneId = "UserOne";
		int initialSize = 0;
		for (Track track : trackList.getTracksListSortedByVotes()) {
			if (track.getId().equals(trackThreeId)) {
				initialSize = track.getVoters().size();
			}
		}
		trackList.downvoteTrack(trackThreeId, userOneId);

		for (Track track : trackList.getTracksListSortedByVotes()) {
			if (track.getId().equals(trackThreeId)) {
				Assert.isTrue(track.getVoters().size() == initialSize - 1, "Downvote track not working");
			}
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void downvoteTrack_nullUser() {
		String trackThreeId = "trackThree";
		trackList.upvoteTrack(trackThreeId, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void downvoteTrack_nullTrackId() {
		String trackId = null;
		User testUser = new User("testUser");
		trackList.upvoteTrack(trackId, testUser);
	}

	@Test
	public void removeSong() {
		int initialSize = trackList.getTracksListSortedByVotes().size();
		trackList.removeSong(trackList.getTracksListSortedByVotes().get(1));
		Assert.isTrue(trackList.getTracksListSortedByVotes().size() == initialSize - 1, "Remove track not working");
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeSongNullTrack() {
		trackList.removeSong(null);
	}
}
