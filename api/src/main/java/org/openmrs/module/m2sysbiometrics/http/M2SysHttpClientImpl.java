package org.openmrs.module.m2sysbiometrics.http;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.m2sysbiometrics.M2SysBiometricsConstants;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.util.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

/**
 * Serves as a base for all implementation of the resource interfaces. Provides method for basic
 * REST operations with the M2Sys servers.
 */
@Component
public class M2SysHttpClientImpl implements M2SysHttpClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(M2SysHttpClientImpl.class);
	
	private RestOperations restOperations = new RestTemplate();
	
	@Override
	public ResponseEntity<String> getServerStatus(String url) {
		try {
			return exchange(new URI(url), HttpMethod.GET, String.class);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Sends a post request to the M2Sys server.
	 * 
	 * @param request the request to be sent
	 * @return the response json
	 */
	@Override
	public M2SysResponse postRequest(String url, M2SysRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.ALL));
		
		debugRequest(url, request);
		
		try {
			ResponseEntity<M2SysResponse> responseEntity = exchange(new URI(url), HttpMethod.POST, request, headers,
			    M2SysResponse.class);
			M2SysResponse response = responseEntity.getBody();
			checkResponse(response);
			return response;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private <T> ResponseEntity<T> exchange(URI url, HttpMethod method, Class<T> responseClass) {
		return exchange(url, method, null, new HttpHeaders(), responseClass);
	}
	
	private <T> ResponseEntity<T> exchange(URI url, HttpMethod method, Object body, HttpHeaders headers,
                                           Class<T> responseClass) {
        Token token = getToken();

        headers.add("Authorization", token.getTokenType() + " " + token.getAccessToken());

        return restOperations.exchange(url, method, new HttpEntity<>(body, headers), responseClass);
    }
	
	private Token getToken() {
		String username = Context.getAdministrationService().getGlobalProperty(M2SysBiometricsConstants.M2SYS_USER);
		String password = Context.getAdministrationService().getGlobalProperty(M2SysBiometricsConstants.M2SYS_PASSWORD);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		String body = "grant_type=password&username=" + username + "&Password=" + password;
		
		return restOperations.exchange(M2SysBiometricsConstants.M2SYS_SERVER_URL + "/cstoken", HttpMethod.POST,
		    new HttpEntity<Object>(body, headers), Token.class).getBody();
		
	}
	
	private void checkResponse(M2SysResponse response) {
		if (BooleanUtils.isTrue(response.getSuccess())) {
			throw new RuntimeException(response.getResponseCode());
		}
	}
	
	private void debugRequest(String url, Object request) {
		if (LOGGER.isDebugEnabled()) {
			try {
				String json = new ObjectMapper().writeValueAsString(request);
				LOGGER.debug("{} request body: {}", url, json);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}