package com.tfflabs.soundfoundry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.tfflabs.soundfoundry.repositories.RoomRepository;

@SpringBootApplication
@EnableScheduling
public class App {
	@Autowired
	RoomRepository repo;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
