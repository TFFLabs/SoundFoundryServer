package com.tfflabs.soundfoundry.entities;

public enum SortMethod {

	VOTES("votes"),
	ROUNDROBIN("roundrobin");
	
	SortMethod(String type){
		this.type = type;
	}
	
	String type = "";
}
