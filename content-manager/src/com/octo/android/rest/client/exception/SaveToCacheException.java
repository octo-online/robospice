package com.octo.android.rest.client.exception;

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
