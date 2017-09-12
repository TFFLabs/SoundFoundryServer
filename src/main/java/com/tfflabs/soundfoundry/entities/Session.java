package com.tfflabs.soundfoundry.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Session {
	
	private String access_token ;
	private String refresh_token;
	private String code;
	
}
