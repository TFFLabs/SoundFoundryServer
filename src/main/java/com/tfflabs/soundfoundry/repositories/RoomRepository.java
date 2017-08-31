package com.tfflabs.soundfoundry.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tfflabs.soundfoundry.entities.Room;

@Repository
public interface RoomRepository extends MongoRepository<Room, String>{

}
