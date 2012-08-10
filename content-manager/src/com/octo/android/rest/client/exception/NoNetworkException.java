package com.octo.android.rest.client.exception;

public class NoNetworkException extends ContentManagerException {

    private static final long serialVersionUID = 5365883691014039322L;

    public NoNetworkException() {
        super( "Network is not available" );
    }

}
