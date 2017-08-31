package com.tfflabs.soundfoundry.entities;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Artist {
	@Id
	private String id;
	private String name;
}
