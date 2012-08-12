package com.octo.android.rest.client;

import org.springframework.web.client.RestTemplate;

import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.springandroid.RestContentRequest;

public abstract class SpringAndroidContentService extends ContentService {

    public abstract RestTemplate createRestTemplate();

    @Override
    protected < T > void processRequest( CachedContentRequest< T > request ) {
        if ( request.getContentRequest() instanceof RestContentRequest ) {
            ( (RestContentRequest< T >) request.getContentRequest() ).setRestTemplate( createRestTemplate() );
        }
        super.processRequest( request );
    }

}
