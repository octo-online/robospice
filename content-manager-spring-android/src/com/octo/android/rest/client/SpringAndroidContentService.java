package com.octo.android.rest.client;

import java.util.Set;

import org.springframework.web.client.RestTemplate;

import com.octo.android.rest.client.request.CachedContentRequest;
import com.octo.android.rest.client.request.RequestListener;
import com.octo.android.rest.client.request.springandroid.RestContentRequest;

/**
 * This class offers a {@link ContentService} that injects a {@link RestTemplate} from spring android into every
 * {@link RestContentRequest} it has to execute.
 * 
 * Developpers will have to implement {@link #createRestTemplate()} in addition to the usual
 * {@link #createCacheManager(android.app.Application)} methods to create a {@link RestTemplate} and configure it.
 * 
 * @author sni
 * 
 */
public class SpringAndroidContentService extends ContentService {

    private RestTemplate restTemplate;

    @Override
    public void onCreate() {
        super.onCreate();
        if ( !( getApplication() instanceof RestContentConfiguration ) ) {
            throw new RuntimeException( "Application class :" + getApplication().getClass().getName() + " doesn't implement "
                    + RestContentConfiguration.class.getName() );
        }
        restTemplate = ( (RestContentConfiguration) getApplication() ).getRestTemplate();
    }

    @Override
    public void addRequest( CachedContentRequest< ? > request, Set< RequestListener< ? >> listRequestListener ) {
        if ( request.getContentRequest() instanceof RestContentRequest ) {
            ( (RestContentRequest< ? >) request.getContentRequest() ).setRestTemplate( restTemplate );
        }
        super.addRequest( request, listRequestListener );
    }
}
