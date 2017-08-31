package com.tfflabs.soundfoundry.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tfflabs.soundfoundry.entities.Track;

@Repository
public interface TrackRepository extends MongoRepository<Track, String> {
	public List<Track> findByRooms_name(String name);
}
