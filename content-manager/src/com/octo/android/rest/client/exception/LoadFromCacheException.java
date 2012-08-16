package com.octo.android.rest.client.exception;

import com.octo.android.rest.client.ContentManager;
import com.octo.android.rest.client.ContentService;

/**
 * Exception thrown when a problem occurs while loading data from cache. Those exceptions are not thrown by default in
 * the framework.
 * 
 * @see ContentManager#setFailOnCacheError(boolean)
 * @see ContentService#setFailOnCacheError(boolean)
 * @author sni
 * 
 */
public class LoadFromCacheException extends ContentManagerException {

    private static final long serialVersionUID = -1821941621446511524L;

    public LoadFromCacheException( String detailMessage ) {
        super( detailMessage );
    }

    public LoadFromCacheException( String detailMessage, Throwable throwable ) {
        super( detailMessage, throwable );
    }

    public LoadFromCacheException( Throwable throwable ) {
        super( throwable );
    }

}
