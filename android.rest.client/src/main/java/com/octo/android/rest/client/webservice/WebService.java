package com.octo.android.rest.client.webservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Prepare the android spring rest client.
 * 
 * @author mwa
 * 
 */
public class WebService  {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	/** Timeout when calling a web service (in ms). */
	private static final int WEBSERVICES_TIMEOUT = 30000;

	// ============================================================================================
	// ATTRIBUTES
	// ============================================================================================

	protected RestTemplate mRestTemplate;

	// ============================================================================================
	// CONSTRUCTOR
	// ============================================================================================

	public WebService() {

		mRestTemplate = new RestTemplate();

		// set timeout for requests
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setReadTimeout(WEBSERVICES_TIMEOUT);
		httpRequestFactory.setConnectTimeout(WEBSERVICES_TIMEOUT);
		mRestTemplate.setRequestFactory(httpRequestFactory);

		// web services support json responses
		final MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
		final List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
		jsonConverter.setSupportedMediaTypes(supportedMediaTypes);

		FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();

		final List<HttpMessageConverter<?>> listHttpMessageConverters = mRestTemplate.getMessageConverters();
		listHttpMessageConverters.add(jsonConverter);
		listHttpMessageConverters.add(formHttpMessageConverter);
		listHttpMessageConverters.add(stringHttpMessageConverter);
		mRestTemplate.setMessageConverters(listHttpMessageConverters);
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================

	public RestTemplate getRestTemplate() {
		return mRestTemplate;
	}
	
}
