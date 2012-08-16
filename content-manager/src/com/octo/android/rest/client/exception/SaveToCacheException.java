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
public class SaveToCacheException extends ContentManagerException {

    private static final long serialVersionUID = -633402253089445891L;

    public SaveToCacheException( String detailMessage ) {
        super( detailMessage );
    }

    public SaveToCacheException( String detailMessage, Throwable throwable ) {
        super( detailMessage, throwable );
    }

    public SaveToCacheException( Throwable throwable ) {
        super( throwable );
    }

}
