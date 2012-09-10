package com.octo.android.rest.client;

import org.springframework.web.client.RestTemplate;

public interface RestContentConfiguration extends ContentConfiguration {
    public abstract RestTemplate getRestTemplate();
}
