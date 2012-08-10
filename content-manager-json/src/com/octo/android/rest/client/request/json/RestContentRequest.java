package com.octo.android.rest.client.request.json;

import org.springframework.web.client.RestTemplate;

import com.octo.android.rest.client.request.ContentRequest;

public abstract class RestContentRequest< RESULT > extends ContentRequest< RESULT > {

    private RestTemplate restTemplate;

    public RestContentRequest( Class< RESULT > clazz ) {
        super( clazz );
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate( RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }

}