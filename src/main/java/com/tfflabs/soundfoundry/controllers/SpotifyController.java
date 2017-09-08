package com.tfflabs.soundfoundry.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tfflabs.soundfoundry.entities.Session;
import com.tfflabs.soundfoundry.services.SpotifyService;
import com.wrapper.spotify.exceptions.WebApiException;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {
	
	@Autowired
	SpotifyService spotifyService;
	
	@RequestMapping(value = "/auth/url", method = RequestMethod.GET)
	public Map<String, String> getPopUpUrl() {
		return spotifyService.getPopUpUrl();
	}

	@RequestMapping(value = "/auth/token", method = RequestMethod.POST)
	public Session getAccessToken(@RequestBody Session session) throws IOException, WebApiException {
		return spotifyService.getAccessToken(session);
	}

	@RequestMapping(value = "/auth/token", method = RequestMethod.PUT)
	public Session refreshAccessToken(@RequestBody Session session) throws IOException, WebApiException {
		return spotifyService.refreshAccessToken(session);
	}
}
