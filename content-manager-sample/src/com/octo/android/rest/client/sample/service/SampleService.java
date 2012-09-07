package com.octo.android.rest.client.sample.service;

import java.util.List;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Application;

import com.octo.android.rest.client.SpringAndroidContentService;
import com.octo.android.rest.client.persistence.CacheManager;
import com.octo.android.rest.client.persistence.file.InFileInputStreamObjectPersister;
import com.octo.android.rest.client.persistence.file.InFileStringObjectPersister;
import com.octo.android.rest.client.persistence.json.JSonPersistenceManageFactory;

public class SampleService extends SpringAndroidContentService {

    private static final int WEBSERVICES_TIMEOUT = 30000;

    @Override
    public CacheManager createCacheManager( Application application ) {
        CacheManager cacheManager = new CacheManager();

        // init
        InFileStringObjectPersister stringPersistenceManager = new InFileStringObjectPersister( application );
        InFileInputStreamObjectPersister binaryPersistenceManager = new InFileInputStreamObjectPersister( application );
        JSonPersistenceManageFactory jSonPersistenceManageFactory = new JSonPersistenceManageFactory( application );

        cacheManager.addObjectPersisterFactory( stringPersistenceManager );
        cacheManager.addObjectPersisterFactory( binaryPersistenceManager );
        cacheManager.addObjectPersisterFactory( jSonPersistenceManageFactory );
        return cacheManager;
    }

    @Override
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // set timeout for requests

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setReadTimeout( WEBSERVICES_TIMEOUT );
        httpRequestFactory.setConnectTimeout( WEBSERVICES_TIMEOUT );
        restTemplate.setRequestFactory( httpRequestFactory );

        // web services support json responses
        MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        final List< HttpMessageConverter< ? >> listHttpMessageConverters = restTemplate.getMessageConverters();

        listHttpMessageConverters.add( jsonConverter );
        listHttpMessageConverters.add( formHttpMessageConverter );
        listHttpMessageConverters.add( stringHttpMessageConverter );
        restTemplate.setMessageConverters( listHttpMessageConverters );
        return restTemplate;
    }

}
