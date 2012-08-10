package com.octo.android.rest.client.request;

/**
 * Interface used to deal with request result. Two cases : request failed or succeed.
 * 
 * Implement this interface to retrieve request result or to manage error
 * 
 * @author jva
 * 
 * @param <RESULT>
 */
public interface RequestListener< RESULT > {

    void onRequestFailure( int resultCode );

    void onRequestSuccess( RESULT result );

}
