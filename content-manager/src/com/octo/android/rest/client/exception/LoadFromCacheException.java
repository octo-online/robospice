package com.octo.android.rest.client.exception;

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
