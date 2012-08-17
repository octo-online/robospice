package com.octo.android.rest.client.exception;

import com.octo.android.rest.client.ContentManager;
import com.octo.android.rest.client.ContentService;

/**
 * Exception thrown when a problem occurs while saving data to cache. Those exceptions are not thrown by default in the
 * framework.
 * 
 * @see ContentManager#setFailOnCacheError(boolean)
 * @see ContentService#setFailOnCacheError(boolean)
 * @author sni
 * 
 */
public class CacheSavingException extends ContentManagerException {

    private static final long serialVersionUID = -633402253089445891L;

    public CacheSavingException( String detailMessage ) {
        super( detailMessage );
    }

    public CacheSavingException( String detailMessage, Throwable throwable ) {
        super( detailMessage, throwable );
    }

    public CacheSavingException( Throwable throwable ) {
        super( throwable );
    }

}
