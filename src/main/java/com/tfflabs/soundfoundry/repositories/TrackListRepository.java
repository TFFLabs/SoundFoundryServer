package com.tfflabs.soundfoundry.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tfflabs.soundfoundry.entities.TrackList;

@Repository
public interface TrackListRepository extends MongoRepository<TrackList, String> {
	public TrackList findByRoomName(String name);
}
