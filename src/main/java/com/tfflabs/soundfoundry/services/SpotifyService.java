package com.tfflabs.soundfoundry.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tfflabs.soundfoundry.entities.Session;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.authentication.AuthorizationCodeGrantRequest;
import com.wrapper.spotify.methods.authentication.RefreshAccessTokenRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.RefreshAccessTokenCredentials;

@Service
public class SpotifyService {
	@Value("${spotify.client_id}")
	private String client_id;
	@Value("${spotify.client_secret}")
	private String client_secret;
	@Value("${sportify.redirect_uri}")
	private String redirect_uri;

	public Map<String, String> getPopUpUrl() {
		final List<String> scopes = Arrays.asList("user-read-playback-state", "user-follow-modify", "user-follow-read",
				"playlist-read-private", "playlist-read-collaborative", "playlist-modify-public",
				"playlist-modify-private", "user-library-read", "user-library-modify", "user-read-private",
				"user-modify-playback-state");
		Api api = Api.builder().clientId(client_id).clientSecret(client_secret).redirectURI(redirect_uri).build();
		Map<String, String> urlWrapper = new HashMap<String, String>();
		urlWrapper.put("url", api.createAuthorizeURL(scopes).showDialog(true).build().toStringWithQueryParameters());
		return urlWrapper;
	}

	public Session getAccessToken(Session session) throws IOException, WebApiException {
		// build the api caller
		Api api = Api.builder().clientId(client_id).clientSecret(client_secret).redirectURI(redirect_uri)
				.build();

		// build the new token request
		AuthorizationCodeGrantRequest request = api.authorizationCodeGrant(session.getCode()).build();

		// return the requested token
		AuthorizationCodeCredentials credentials = request.get();
		session.setAccess_token(credentials.getAccessToken());
		session.setRefresh_token(credentials.getRefreshToken());
		return session;
	}

	public Session refreshAccessToken(Session session) throws IOException, WebApiException {
		// build the api caller
		Api api = Api.builder().clientId(client_id).clientSecret(client_secret).redirectURI(redirect_uri)
				.build();
		api.setRefreshToken(session.getRefresh_token());

		// build the resfresh request
		RefreshAccessTokenRequest request = api.refreshAccessToken().build();

		// return the refreshed token
		RefreshAccessTokenCredentials credentials = request.get();
		session.setAccess_token(credentials.getAccessToken());
		return session;
	}
}
