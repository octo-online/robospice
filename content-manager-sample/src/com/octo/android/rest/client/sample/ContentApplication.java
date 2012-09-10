package com.octo.android.rest.client.sample;

import java.util.List;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Application;

import com.octo.android.rest.client.RestContentConfiguration;
import com.octo.android.rest.client.persistence.CacheManager;
import com.octo.android.rest.client.persistence.file.InFileInputStreamObjectPersister;
import com.octo.android.rest.client.persistence.file.InFileStringObjectPersister;
import com.octo.android.rest.client.persistence.json.InJSonFileObjectPersisterFactory;

public class ContentApplication extends Application implements RestContentConfiguration {

    private static final int WEBSERVICES_TIMEOUT = 30000;
    private CacheManager cacheManager;
    private RestTemplate restTemplate;

    @Override
    public void onCreate() {
        super.onCreate();
        this.cacheManager = createCacheManager( this );
        this.restTemplate = createRestTemplate();
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public CacheManager createCacheManager( Application application ) {
        CacheManager cacheManager = new CacheManager();

        // init
        InFileStringObjectPersister inFileStringObjectPersister = new InFileStringObjectPersister( application );
        InFileInputStreamObjectPersister inFileInputStreamObjectPersister = new InFileInputStreamObjectPersister( application );
        InJSonFileObjectPersisterFactory inJSonFileObjectPersisterFactory = new InJSonFileObjectPersisterFactory( application );

        inFileStringObjectPersister.setAsyncSaveEnabled( true );
        inFileInputStreamObjectPersister.setAsyncSaveEnabled( true );
        inJSonFileObjectPersisterFactory.setAsyncSaveEnabled( true );

        cacheManager.addObjectPersisterFactory( inFileStringObjectPersister );
        cacheManager.addObjectPersisterFactory( inFileInputStreamObjectPersister );
        cacheManager.addObjectPersisterFactory( inJSonFileObjectPersisterFactory );
        return cacheManager;
    }

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
